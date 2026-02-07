# Flujo “Eliminar” Torneo (Admin) = Desactivación legal (sin borrar) — Arquitectura Hexagonal

Contexto: hoy el sistema maneja el ciclo de vida con `TournamentStatus` (`ABIERTO/INICIADO/FINALIZADO/CANCELADO`), y la “cancelación” actual (`POST /api/tournaments/{id}/cancel`) **borra** relaciones/equipos vía `TournamentCleanupPort`. Para moderación “admin dios” y requisito legal (“no se elimina”), conviene implementar una **desactivación por moderación** que:

- **No borre** datos ni relaciones (equipos, inscripciones, matches, resultados, reputación).
- **Corte toda expectativa funcional** (no inscripciones, no fixtures, no resultados, no updates, etc.).
- **Oculte completamente** el torneo a cualquier no-admin (incluyendo organizador), como si no existiera.
- Registre **auditoría** (quién, cuándo, por qué) y opcionalmente notifique.

---

## 1) Decisión de un senior: ¿404 vs 410/403 para no-admin?

Recomendación “senior/correcta” para este caso de moderación:

- **No-admin (incluye organizador)**: responder **404 Not Found** en *cualquier* endpoint que apunte al torneo desactivado (`GET/POST/PUT/... /api/tournaments/{id}...`).
  - Motivo: evita filtración de existencia (anti-enumeración) y es consistente con “está retirado por moderación”.
  - 410 (“Gone”) es útil cuando querés *confirmar* que existía y fue removido. En moderación suele ser mejor no revelar.
- Listados públicos: **no incluir** desactivados.
- Admin: puede ver y operar vía `/api/admin/**` (ya protegido por `SecurityConfig`).

---

## 2) Modelo de Dominio (Core)

Mantener `TournamentStatus` para el “estado deportivo” y agregar un “estado de moderación” para el “estado legal/visibilidad”.

### Nuevo concepto
- `TournamentModerationStatus`:
  - `ACTIVE`
  - `DEACTIVATED`

### Campos sugeridos en `Tournament`
- `TournamentModerationStatus moderationStatus`
- `Date moderatedAt`
- `Long moderatedByAdminId` (o email; id es preferible)
- `String moderationReason`

Regla de dominio:
- Si `moderationStatus == DEACTIVATED` ⇒ torneo inaccesible para no-admin y bloqueado para cualquier operación no-admin.

---

## 3) Persistencia (DB + JPA)

### Tabla `tournaments` (agregar columnas)
- `moderation_status` (varchar, default `'ACTIVE'`, not null)
- `moderated_at` (timestamp null)
- `moderated_by_admin_id` (bigint null)
- `moderation_reason` (text/varchar null)

### JPA
- Extender `TournamentJpaEntity` y `TournamentMapper` para mapear estos campos.

### Consultas públicas (muy importante)
- `findByFilters` / listados “public”: deben filtrar `moderation_status = 'ACTIVE'` por defecto.
  - Alternativa hexa limpia: agregar un parámetro `includeDeactivated` (solo en casos admin) o exponer un puerto específico para admin.

---

## 4) Puertos (Hexagonal)

### Puertos de entrada (core/ports/in)
1) `AdminDeactivateTournamentPort`
   - `DeactivateResult deactivate(Long tournamentId, Long adminId, String reason)`
2) `AdminReactivateTournamentPort` (recomendado)
   - `ReactivateResult reactivate(Long tournamentId, Long adminId, String reason)`
3) (Opcional) `AdminGetTournamentByIdPort` para ver torneos incluso desactivados.

### Puertos de salida (core/ports/out)
Reusar:
- `TournamentRepositoryPort` (agregar soporte a nuevos campos en update/save).
- `FindTournamentsPort` (ajustar para filtrar `ACTIVE` en público).
- `NotificationPort` para aviso masivo (opcional).

---

## 5) Casos de uso (core/application/usecase)

### 5.1 AdminDeactivateTournamentUseCase
**Input**: `tournamentId`, `adminId`, `reason`.

**Validaciones**
- torneo existe
- `adminId` válido (ya viene del contexto admin; el controller puede extraerlo del JWT o parámetro seguro)
- `reason` requerido (trazabilidad)
- idempotencia: si ya está `DEACTIVATED`, devolver OK con estado actual.

**Efectos**
- set `moderationStatus=DEACTIVATED`
- set auditoría (`moderatedAt`, `moderatedByAdminId`, `reason`)
- persistir `TournamentRepositoryPort.update(tournament)`
- notificar participantes (opcional): “Torneo dado de baja por moderación”

**NO HACER**
- NO llamar `TournamentCleanupPort.removeTeamsAndRegistrations` (eso es destructivo).
- NO cambiar `TournamentStatus` (deportivo) salvo que producto lo exija. Se conserva para historial.

### 5.2 AdminReactivateTournamentUseCase
Revertir a `ACTIVE` + auditoría.

---

## 6) “Cortafuegos” en el Core (para que no esperen nada)

En tu sistema actual hay muchas operaciones habilitadas solo por `TournamentStatus`. Con moderación, hay que bloquear adicionalmente.

### Regla técnica
Agregar al inicio de cada use case que toca el torneo:
- `if (tournament.getModerationStatus() == DEACTIVATED) throw new IllegalArgumentException("Torneo no encontrado");`

Nota: se usa **IllegalArgumentException** con mensaje “not found” para mapear a 404 en controllers y no filtrar que está moderado.

### Use cases a blindar (muta o implica expectativa)
- Inscripciones:
  - `RegisterToTournamentUseCase`
  - `RegisterTeamToTournamentUseCase`
  - `RegisterRunnerToTournamentUseCase`
  - `RemoveTeamFromTournamentUseCase`
- Lifecycle / edición:
  - `StartTournamentUseCase`
  - `UpdateTournamentUseCase`
  - `FinalizeTournamentUseCase`
  - `CancelTournamentUseCase` (si no-admin intenta, debe ver 404 si está desactivado)
- Fixture / resultados:
  - `GenerateEliminationFixtureUseCase`
  - `GenerateLeagueFixtureUseCase`
  - `ReportMatchResultUseCase`
  - `ReportLeagueMatchResultUseCase`
  - `ReportRaceResultsUseCase`
- Lecturas relacionadas que crean expectativa:
  - `GetRaceResultsUseCase` (hoy devuelve placeholder)
  - endpoints de fixture / standings (si se implementan validaciones en use cases o en controller)
- “Vías laterales”:
  - `GetTournamentUseCase.getSubscribedTournaments(...)` (usa `findAllWithTeams()`): debe filtrar `ACTIVE` para no-admin.
  - `GetAllTournamentsUseCase` si se usa para vistas de usuarios: idem.

---

## 7) Adaptadores de entrada (Controllers)

### 7.1 Controller Admin (nuevo)
Prefijo: `/api/admin/tournaments`

- `POST /api/admin/tournaments/{id}/deactivate`
  - body: `{ "reason": "spam/abuso/DMCA/etc" }`
  - response 200: `{ "tournamentId": 10, "moderationStatus": "DEACTIVATED", "moderatedAt": "...", "moderatedByAdminId": 1, "reason": "..." }`

- `POST /api/admin/tournaments/{id}/reactivate`
  - body: `{ "reason": "apelación aprobada" }`
  - response 200: `{ "tournamentId": 10, "moderationStatus": "ACTIVE", ... }`

### 7.2 Controllers no-admin existentes (TournamentController, UserController)
Regla: si el torneo está desactivado ⇒ responder 404.

Puntos clave:
- `GET /api/tournaments/{id}` ⇒ 404
- `GET /api/tournaments/public` y `GET /latest` ⇒ no listar desactivados
- `POST /register`, `POST /start`, `PUT /api/tournaments/{id}`, `POST fixture`, report results, etc. ⇒ 404
- User endpoints:
  - `GET /api/users/tournaments` (suscritos): no incluir desactivados
  - `GET /api/users/tournaments/organized`: no incluir desactivados

---

## 8) Adaptadores de salida (Repositorios)

### TournamentRepositoryPort / TournamentRepository
Debe persistir y rehidratar los nuevos campos.

### FindTournamentsPort (public)
Debe filtrar `ACTIVE`. No alcanza con filtrar en controller porque hay otros caminos que listan torneos.

---

## 9) Notificaciones (opcional pero recomendado)

Agregar `NotificationType.TOURNAMENT_DEACTIVATED_BY_ADMIN`.

Al desactivar:
- `NotificationPort.notifyUsersOfTournament(tournamentId, "...", "...", TOURNAMENT_DEACTIVATED_BY_ADMIN)`

Mensaje recomendado (neutral, sin detalles legales):
- “El torneo fue dado de baja por moderación y no recibirá más actualizaciones.”

---

## 10) Contrato para Frontend (resumen)

### Para usuarios / organizador (no-admin)
- Torneo desactivado = “no existe”:
  - listados: no aparece
  - detalle: 404
  - acciones: 404

### Para admin
- Endpoints admin devuelven estado de moderación y auditoría.

---

## 11) Pruebas rápidas en Postman (para pasar a FE)

### 11.1 Desactivar (admin)
- `POST {{baseUrl}}/api/admin/tournaments/10/deactivate`
- Headers: `Authorization: Bearer <JWT_ADMIN>`
- Body:
```json
{ "reason": "contenido inapropiado" }
```
Esperado: 200 con `moderationStatus=DEACTIVATED`.

### 11.2 Verificación “apagado total” (no-admin)
Con `JWT_USER` o sin auth:
- `GET /api/tournaments/10` ⇒ 404
- `POST /api/tournaments/10/register` ⇒ 404
- `POST /api/tournaments/10/start` ⇒ 404
- `GET /api/tournaments/public` ⇒ no incluye id 10
- `GET /api/users/tournaments?id=...&email=...` ⇒ no incluye id 10

### 11.3 Reactivar (admin)
- `POST {{baseUrl}}/api/admin/tournaments/10/reactivate`
- Headers: `Authorization: Bearer <JWT_ADMIN>`
- Body:
```json
{ "reason": "apelación aprobada" }
```
Esperado: 200 con `moderationStatus=ACTIVE`, y el torneo vuelve a aparecer según su `TournamentStatus`.

---

## 12) Notas importantes con tu implementación actual

- `CancelTournamentUseCase` hoy borra equipos/inscripciones. **No usar** para moderación.
- `SecurityConfig` ya protege `/api/admin/**` con `hasRole("ADMIN")`. Es un buen lugar para montar estos endpoints.

