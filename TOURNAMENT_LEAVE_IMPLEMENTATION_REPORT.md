# Implementación: Salir de Torneo (Participantes) — Resumen + Postman

Fecha: 2026-02-08  
Proyecto: `BackEnd` (Spring Boot)  

Esta entrega agrega la funcionalidad de **salir de un torneo** para participantes, respetando:
- **No permitido** si el torneo está `INICIADO` (solo `ABIERTO`).
- **No permitido** si faltan **< 24 horas** para `startAt`.
- **No permitido** si `registrationDeadline` ya pasó.
- **No permitido** si el usuario autenticado es el **organizador** del torneo.
- Si el torneo está **desactivado por admin** ⇒ se trata como inexistente (**404**).

---

## Qué se implementó (cambios principales)

### API (adapter in)
- Se agregó endpoint:
  - **POST** `/api/tournaments/{id}/leave`
  - Usa `Authentication.getName()` como fuente de identidad (no se acepta `userId` del cliente).
  - Request opcional: `LeaveTournamentRequest { teamId?, reason? }`.
  - Response 200 incluye `mode` (`INDIVIDUAL` o `TEAM`) y `teamId` si aplica.

Archivos:
- `src/main/java/com/example/demo/adapters/in/api/controllers/TournamentController.java`
- `src/main/java/com/example/demo/adapters/in/api/dto/LeaveTournamentRequest.java`

### Core (puertos + caso de uso)
- Puerto de entrada:
  - `LeaveTournamentPort` con resultado tipado (`LeaveMode`, `teamId` opcional).
- Caso de uso:
  - `LeaveTournamentUseCase` (transaccional).
  - Determina si la inscripción es:
    - **Individual** (`tournament_participants`) ⇒ sale solo ese usuario.
    - **Capitán de equipo** (creator del team) ⇒ se retira el **equipo completo** del torneo.

Archivos:
- `src/main/java/com/example/demo/core/ports/in/LeaveTournamentPort.java`
- `src/main/java/com/example/demo/core/application/usecase/LeaveTournamentUseCase.java`

### Persistencia / puertos out
- Se extendió `TournamentRegistrationPort` para permitir baja individual:
  - `unregister(tournamentId, userId)` (devuelve cantidad eliminada).
- Se agregó puerto out para resolver equipos del capitán en un torneo:
  - `TeamCaptainQueryPort` + adapter `TeamCaptainQueryRepository`.

Archivos:
- `src/main/java/com/example/demo/core/ports/out/TournamentRegistrationPort.java`
- `src/main/java/com/example/demo/adapters/out/persistence/jpa/repositories/TournamentRegistrationRepository.java`
- `src/main/java/com/example/demo/adapters/out/persistence/jpa/interfaces/TournamentParticipantRepositoryJpa.java`
- `src/main/java/com/example/demo/core/ports/out/TeamCaptainQueryPort.java`
- `src/main/java/com/example/demo/adapters/out/persistence/jpa/repositories/TeamCaptainQueryRepository.java`

### Notificaciones
- Nuevos `NotificationType`:
  - `TOURNAMENT_LEFT`
  - `TEAM_WITHDRAWN`
- El caso de uso genera notificación al usuario y al organizador (best-effort).

Archivo:
- `src/main/java/com/example/demo/core/domain/models/NotificationType.java`

### Wiring
- Bean en `ApplicationConfig` para `LeaveTournamentPort`.

Archivo:
- `src/main/java/com/example/demo/config/ApplicationConfig.java`

### Tests
- Se agregó test WebMvc para el nuevo endpoint (delegación a puerto).

Archivo:
- `src/test/java/com/example/demo/adapters/in/api/controllers/TournamentControllerWebMvcTest.java`

---

## Comportamiento (errores esperados)

- **401**: si no hay auth en `/leave`.
- **403**: si el usuario autenticado es el organizador del torneo.
- **404**: si el torneo no existe o está desactivado por admin.
- **400**: si:
  - torneo no está `ABIERTO`,
  - faltan < 24h para `startAt`,
  - `registrationDeadline` ya pasó,
  - o el usuario no está inscripto.

---

## Endpoints mínimos para probar en Postman (flujo completo)

> Usar `{{baseUrl}}` y setear `{{jwt_user}}` / `{{jwt_org}}` según corresponda.

### 1) Login (obtener JWT)
- **POST** `{{baseUrl}}/login`  
  o  
- **POST** `{{baseUrl}}/login/google`

Guardar el token del usuario participante en `{{jwt_user}}` y el del organizador en `{{jwt_org}}`.

### 2) Crear torneo (organizador)
- **POST** `{{baseUrl}}/api/tournaments/organizer/{organizerId}`
- Header: `Authorization: Bearer {{jwt_org}}`
- Body (ejemplo: startAt debe ser > 24h desde ahora):
```json
{
  "disciplineId": 1,
  "formatId": 2,
  "name": "Torneo prueba",
  "startAt": "2026-02-10T12:00:00.000Z",
  "endAt": "2026-02-11T12:00:00.000Z",
  "registrationDeadline": "2026-02-09T12:00:00.000Z",
  "privateTournament": false,
  "password": null,
  "minParticipantsPerTeam": 1,
  "maxParticipantsPerTeam": 2,
  "minParticipantsPerTournament": 0,
  "maxParticipantsPerTournament": 0,
  "prize": null,
  "registrationCost": 0,
  "isDoubleRound": false,
  "detalles": "Prueba",
  "imageUrl": null
}
```

### 3A) Inscripción individual
- **POST** `{{baseUrl}}/api/tournaments/{tournamentId}/register`
- Header: `Authorization: Bearer {{jwt_user}}`
- Body:
```json
{ "userId": 123 }
```

### 4A) Salir (individual)
- **POST** `{{baseUrl}}/api/tournaments/{tournamentId}/leave`
- Header: `Authorization: Bearer {{jwt_user}}`
- Body opcional:
```json
{ "reason": "No puedo participar" }
```

### 3B) Inscripción por equipo (capitán)
- **POST** `{{baseUrl}}/api/tournaments/{tournamentId}/register/team`
- Body:
```json
{
  "userId": 123,
  "teamName": "Equipo X",
  "participants": [
    { "fullName": "A", "nationalId": "12345678" }
  ]
}
```
> Nota: para que el capitán pueda salir luego, `userId` debe corresponder al usuario de `{{jwt_user}}`.

### 4B) Salir (capitán retira equipo completo)
- **POST** `{{baseUrl}}/api/tournaments/{tournamentId}/leave`
- Header: `Authorization: Bearer {{jwt_user}}`
- Body opcional:
```json
{ "reason": "No llegamos" }
```

---

## Documentación actualizada
- `API_ENDPOINTS_TOURNAMENTS_POSTMAN.md` incluye `/leave`.
- `TOURNAMENT_LEAVE_FLOW.md` describe el diseño y consideraciones.

