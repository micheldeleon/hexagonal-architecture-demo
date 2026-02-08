# Endpoints de Contacto (Mailjet) - Postman

Objetivo: permitir que el frontend use un formulario de contacto para enviar un mail a **gestiontorneosuy@gmail.com**.

> Convencion: usar `{{baseUrl}}` (ej: `http://localhost:8080`).

---

## Configuracion (Mailjet)

El backend usa el port `EmailSenderPort` con adapter Mailjet cuando `MAILJET_ENABLED=true`.

Variables en `.env` / entorno:

- `MAILJET_ENABLED=true`
- `MAILJET_APIKEY=...` *(o `MAILJET_APIKET=...` si ya lo venias usando)*
- `MAILJET_SECRETKEY=...`
- `CONTACT_TO_EMAIL=gestiontorneosuy@gmail.com` *(opcional; por defecto ya es ese)*

Notas:
- Si `MAILJET_ENABLED` esta en `false` o no existe, el backend usa `NoopEmailAdapter` y **no envia** el email (solo loguea un warning). El endpoint responde igual.

---

## 1) Enviar mensaje de contacto

- **POST** `{{baseUrl}}/api/contact`
- **Auth**: publico (no requiere JWT)
- **Headers**:
  - `Content-Type: application/json`
- **Body**:
```json
{
  "name": "Juan Perez",
  "email": "juan@example.com",
  "message": "Hola, tengo una consulta sobre un torneo."
}
```

### Response (202)
```json
{ "message": "Mensaje enviado" }
```

### Validaciones
- `name`: obligatorio, max 120
- `email`: obligatorio, formato email, max 254
- `message`: obligatorio, max 4000
