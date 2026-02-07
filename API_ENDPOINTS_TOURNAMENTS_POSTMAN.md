# Informe de Endpoints (Auth + Torneos + Moderación Admin)

Proyecto: `BackEnd` (Spring Boot)  
Objetivo: referencia rápida para Postman / Frontend (incluye “eliminar” legal = **desactivar por admin** y **reactivar**).

> Convención: usar `{{baseUrl}}` (ej: `http://localhost:8080`) y `Authorization: Bearer {{jwt}}` cuando aplique.

---

## 1) Auth / Login

### Login (usuario/password)
- **POST** `{{baseUrl}}/login`
- **Auth**: público
- **Body**: depende del filtro actual (si usan username/password estándar).
- **Response**: JWT en response (según implementación de `JwtAuthenticationFilter`).

### Login (Google)
- **POST** `{{baseUrl}}/login/google`
- **Auth**: público
- **Body/Response**: JWT (según `GoogleLoginController`).

---

## 2) Torneos (público)

### Listado público con filtros
- **GET** `{{baseUrl}}/api/tournaments/public`
- **Auth**: público
- **Query params (opcionales)**:
  - `status`: `ABIERTO|INICIADO|FINALIZADO|CANCELADO`
  - `disciplineId`: number
  - `name`: string (contiene)
  - `startFrom`: `YYYY-MM-DD`
  - `startTo`: `YYYY-MM-DD`
  - `withPrize`: `true|false`
  - `withRegistrationCost`: `true|false`
- **Notas**:
  - Torneos **desactivados por admin** NO aparecen.

### Listado por status
- **GET** `{{baseUrl}}/api/tournaments/status?status=ABIERTO`
- **Auth**: público
- **Notas**:
  - Torneos **desactivados por admin** NO aparecen.

### Listado “all”
- **GET** `{{baseUrl}}/api/tournaments/all`
- **Auth**: público
- **Notas**:
  - Torneos **desactivados por admin** NO aparecen.

### Últimos 3
- **GET** `{{baseUrl}}/api/tournaments/latest`
- **Auth**: público

### Detalle torneo
- **GET** `{{baseUrl}}/api/tournaments/{id}`
- **Auth**: público
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ devuelve **404** (para cualquiera que no sea admin).

### Fixture (solo lectura)
- **GET** `{{baseUrl}}/api/tournaments/{id}/fixture`
- **Auth**: público
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ **404**.

### Standings (liga)
- **GET** `{{baseUrl}}/api/tournaments/{id}/standings`
- **Auth**: público
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ **404**.

### Resultados carrera (lectura)
- **GET** `{{baseUrl}}/api/tournaments/{id}/race/results`
- **Auth**: público
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ **404**.

---

## 3) Torneos (organizador / usuario)

### Crear torneo
- **POST** `{{baseUrl}}/api/tournaments/organizer/{organizerId}`
- **Auth**: requiere sesión válida (según reglas actuales; típicamente organizer)
- **Body**: `CreateTournamentRequest`

### Actualizar torneo
- **PUT** `{{baseUrl}}/api/tournaments/{id}`
- **Auth**: authenticated
- **Body**: `UpdateTournamentRequest`
- **Notas**:
  - Solo cuando está `ABIERTO` (regla de dominio).
  - Si el torneo está **desactivado por admin** ⇒ **404**.

### Subir imagen del torneo
- **POST** `{{baseUrl}}/api/tournaments/{id}/image`
- **Auth**: authenticated (solo organizador del torneo)
- **Form-data**:
  - `image`: archivo (`image/*`)
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ **404** (por la guarda en update).

### Inscripción “simple”
- **POST** `{{baseUrl}}/api/tournaments/{id}/register`
- **Auth**: `ROLE_USER`
- **Body**:
```json
{ "userId": 123 }
```
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ **404**.

### Inscripción runner (carrera)
- **POST** `{{baseUrl}}/api/tournaments/{id}/register/runner`
- **Auth**: `ROLE_USER`
- **Body**: opcional (puede venir vacío)
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ **404**.

### Inscripción equipo
- **POST** `{{baseUrl}}/api/tournaments/{id}/register/team`
- **Auth**: público (según config actual)
- **Body**: `RegisterTeamRequest`
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ **404**.

### Cancelar torneo (organizador) — destructivo
- **POST** `{{baseUrl}}/api/tournaments/{id}/cancel`
- **Auth**: authenticated (valida organizador en el use case)
- **Notas**:
  - Cancela y hoy hace limpieza (elimina equipos/links) — NO es el “eliminar por admin”.
  - Si el torneo está **desactivado por admin** ⇒ **404**.

### Iniciar torneo
- **POST** `{{baseUrl}}/api/tournaments/{id}/start`
- **Auth**: `ROLE_ORGANIZER`
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ **404**.

### Finalizar torneo
- **POST** `{{baseUrl}}/api/tournaments/{id}/finalize`
- **Auth**: (en config no está explicitado; el controller no exige auth)
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ **404**.

### Remover equipo del torneo
- **POST** `{{baseUrl}}/api/tournaments/{tournamentId}/remove-team`
- **Auth**: público (según config actual; el use case valida organizadorId del body)
- **Body**:
```json
{ "organizerId": 1, "teamId": 5, "comment": "motivo" }
```
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ **404**.

---

## 4) Fixture y resultados (organizador)

### Generar fixture eliminatorio
- **POST** `{{baseUrl}}/api/tournaments/{id}/fixture/elimination`
- **Auth**: `ROLE_ORGANIZER`
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ **404**.

### Generar fixture liga
- **POST** `{{baseUrl}}/api/tournaments/{id}/fixture/league?doubleRound=false`
- **Auth**: `ROLE_ORGANIZER`
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ **404**.

### Reportar resultado (eliminatorio)
- **POST** `{{baseUrl}}/api/tournaments/{tournamentId}/matches/{matchId}/result`
- **Auth**: `ROLE_ORGANIZER`
- **Body**:
```json
{ "scoreHome": 1, "scoreAway": 0, "winnerTeamId": 22 }
```
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ **404**.

### Reportar resultado (liga)
- **POST** `{{baseUrl}}/api/tournaments/{tournamentId}/matches/{matchId}/league-result`
- **Auth**: `ROLE_ORGANIZER`
- **Body**:
```json
{ "scoreHome": 2, "scoreAway": 2 }
```
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ **404**.

### Reportar resultados carrera
- **POST** `{{baseUrl}}/api/tournaments/{id}/race/results`
- **Auth**: `ROLE_ORGANIZER`
- **Body** (lista):
```json
[
  { "teamId": 1, "timeMillis": 123456 },
  { "teamId": 2, "timeMillis": 130000 }
]
```
- **Notas**:
  - Si el torneo está **desactivado por admin** ⇒ **404**.

---

## 5) “Eliminar” legal por moderación (ADMIN)

> Esta es la funcionalidad pedida: el admin puede “eliminar” sin borrar datos ⇒ **desactivar**.

### Desactivar torneo (soft delete / moderación)
- **POST** `{{baseUrl}}/api/admin/tournaments/{id}/deactivate`
- **Auth**: `ROLE_ADMIN`
- **Body**:
```json
{ "reason": "spam / contenido inapropiado / etc" }
```
- **Response 200**:
```json
{
  "tournamentId": 10,
  "moderationStatus": "DEACTIVATED",
  "moderatedAt": "2026-02-07T00:00:00.000+00:00",
  "moderatedByAdminId": 99,
  "reason": "spam"
}
```

### Reactivar torneo (moderación)
- **POST** `{{baseUrl}}/api/admin/tournaments/{id}/reactivate`
- **Auth**: `ROLE_ADMIN`
- **Body**:
```json
{ "reason": "apelación aprobada" }
```
- **Response 200**: mismo formato, `moderationStatus: "ACTIVE"`.

---

## 6) Comportamiento esperado cuando un torneo está desactivado

Para cualquier NO-admin (incluye organizador):
- No aparece en listados públicos.
- Cualquier endpoint que intente operar o consultar por `{id}` devuelve **404**.

Para admin:
- Puede desactivar/reactivar vía `/api/admin/tournaments/...`.

---

## 7) Nota de despliegue (DB)

Ejecutar el script:
- `add_tournament_moderation_fields.sql`

Esto agrega:
- `moderation_status` + auditoría y backfill a `ACTIVE`.

