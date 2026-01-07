# Sistema de Reputación de Organizadores - Guía para Frontend

## Descripción General

Se ha implementado RF19: Sistema de Reputación de Organizadores. Este sistema permite que los jugadores califiquen a los organizadores de torneos después de participar en ellos, con calificaciones de 1 a 5 estrellas y comentarios opcionales.

## Endpoints Disponibles

### 1. Calificar a un Organizador
**POST** `/api/organizers/{organizerId}/rate`

**Autenticación:** Requerida (JWT token en header Authorization)

**Parámetros de URL:**
- `organizerId` (Long): ID del organizador a calificar

**Request Body:**
```json
{
  "tournamentId": 123,
  "score": 5,
  "comment": "Excelente organización, todo fue muy profesional"
}
```

**Campos del Request:**
- `tournamentId` (Long, requerido): ID del torneo en el que participó el usuario
- `score` (Integer, requerido): Calificación de 1 a 5 estrellas
- `comment` (String, opcional): Comentario sobre la experiencia (máximo 500 caracteres recomendado)

**Response Exitoso (201 Created):**
```json
{
  "message": "Calificación registrada exitosamente"
}
```

**Errores Posibles:**
- `400 Bad Request`: "El score debe estar entre 1 y 5"
- `400 Bad Request`: "El torneo no ha finalizado aún"
- `403 Forbidden`: "No participaste en este torneo"
- `403 Forbidden`: "No puedes calificarte a ti mismo"
- `409 Conflict`: "Ya has calificado a este organizador en este torneo"
- `404 Not Found`: "Torneo no encontrado" / "Usuario no encontrado" / "Organizador no encontrado"

---

### 2. Consultar Reputación de un Organizador
**GET** `/api/organizers/{organizerId}/reputation`

**Autenticación:** NO requerida (endpoint público)

**Parámetros de URL:**
- `organizerId` (Long): ID del organizador

**Response Exitoso (200 OK):**
```json
{
  "organizerId": 45,
  "organizerName": "Juan Pérez",
  "averageScore": 4.6,
  "totalRatings": 25,
  "distribution": {
    "fiveStars": 18,
    "fourStars": 5,
    "threeStars": 1,
    "twoStars": 1,
    "oneStars": 0
  },
  "recentRatings": [
    {
      "userName": "María González",
      "tournamentName": "Torneo Clausura 2026",
      "score": 5,
      "comment": "Excelente organización",
      "createdAt": "2026-01-05T14:30:00"
    },
    {
      "userName": "Carlos Rodríguez",
      "tournamentName": "Copa de Verano",
      "score": 4,
      "comment": "Muy bueno, solo pequeños detalles",
      "createdAt": "2026-01-03T10:15:00"
    }
  ]
}
```

**Campos del Response:**
- `organizerId` (Long): ID del organizador
- `organizerName` (String): Nombre completo del organizador
- `averageScore` (Double): Promedio de todas las calificaciones (0.0 - 5.0)
- `totalRatings` (Integer): Cantidad total de calificaciones recibidas
- `distribution` (Object): Distribución de calificaciones por estrellas
  - `fiveStars` (Integer): Cantidad de calificaciones de 5 estrellas
  - `fourStars` (Integer): Cantidad de calificaciones de 4 estrellas
  - `threeStars` (Integer): Cantidad de calificaciones de 3 estrellas
  - `twoStars` (Integer): Cantidad de calificaciones de 2 estrellas
  - `oneStars` (Integer): Cantidad de calificaciones de 1 estrella
- `recentRatings` (Array): Últimas 5 calificaciones recibidas (ordenadas por fecha DESC)
  - `userName` (String): Nombre del usuario que calificó
  - `tournamentName` (String): Nombre del torneo
  - `score` (Integer): Calificación dada (1-5)
  - `comment` (String): Comentario (puede ser null)
  - `createdAt` (DateTime): Fecha y hora de la calificación

**Errores Posibles:**
- `404 Not Found`: "Organizador no encontrado"

---

## Reglas de Negocio

### Restricciones para Calificar:
1. ✅ El usuario debe estar autenticado
2. ✅ El score debe ser entre 1 y 5 (inclusive)
3. ✅ El torneo debe existir y estar en estado FINALIZADO
4. ✅ El usuario debe haber participado en el torneo
5. ✅ El usuario no puede calificarse a sí mismo
6. ✅ Un usuario solo puede calificar UNA VEZ a un organizador por cada torneo
7. ✅ El comentario es opcional

### Consulta de Reputación:
- Es un endpoint público (no requiere autenticación)
- Muestra estadísticas agregadas de todas las calificaciones
- Incluye las últimas 5 calificaciones con comentarios

---

## Casos de Uso para UI

### 1. Pantalla de Perfil de Organizador
**Ubicación:** Vista pública del perfil de un organizador

**Elementos a mostrar:**
- Promedio de calificación con estrellas (ej: ⭐⭐⭐⭐⭐ 4.6/5.0)
- Cantidad total de calificaciones (ej: "Basado en 25 calificaciones")
- Gráfico de barras con distribución de calificaciones
- Lista de calificaciones recientes con:
  - Nombre del usuario
  - Nombre del torneo
  - Estrellas
  - Comentario
  - Fecha

**Ejemplo de visualización:**
```
┌─────────────────────────────────────────┐
│ Reputación del Organizador              │
│                                         │
│ ⭐⭐⭐⭐⭐ 4.6 / 5.0                      │
│ Basado en 25 calificaciones             │
│                                         │
│ Distribución:                           │
│ 5⭐ ████████████████████ 18             │
│ 4⭐ ██████ 5                            │
│ 3⭐ █ 1                                 │
│ 2⭐ █ 1                                 │
│ 1⭐ 0                                   │
│                                         │
│ Calificaciones Recientes:               │
│ ───────────────────────────────────────│
│ María González ⭐⭐⭐⭐⭐                 │
│ Torneo Clausura 2026                    │
│ "Excelente organización..."             │
│ 5 de enero, 2026                        │
└─────────────────────────────────────────┘
```

---

### 2. Modal/Formulario para Calificar
**Ubicación:** Después de finalizar un torneo, en la lista de torneos del usuario

**Trigger:** 
- Mostrar botón "Calificar Organizador" solo en torneos con estado FINALIZADO
- El botón debe estar deshabilitado si el usuario ya calificó

**Elementos del formulario:**
- Selector de estrellas (1-5) - REQUERIDO
- Campo de texto para comentario - OPCIONAL
- Botón "Enviar Calificación"
- Botón "Cancelar"

**Validaciones en Frontend:**
- Score: requerido, entre 1 y 5
- Comentario: opcional, máximo 500 caracteres

**Flujo:**
1. Usuario hace clic en "Calificar Organizador"
2. Se abre modal/formulario
3. Usuario selecciona estrellas (mínimo 1, máximo 5)
4. Usuario puede agregar comentario opcional
5. Al enviar, hacer POST a `/api/organizers/{organizerId}/rate`
6. Si es exitoso (201): mostrar mensaje de éxito y cerrar modal
7. Si hay error: mostrar mensaje de error específico

**Ejemplo de UI:**
```
┌─────────────────────────────────────────┐
│ Calificar a Juan Pérez                  │
│                                         │
│ Torneo: Copa de Verano 2026             │
│                                         │
│ ¿Cómo calificarías al organizador?      │
│ ☆☆☆☆☆  (Selecciona de 1 a 5 estrellas)│
│                                         │
│ Comentario (opcional):                  │
│ ┌─────────────────────────────────────┐│
│ │                                     ││
│ │                                     ││
│ └─────────────────────────────────────┘│
│ 0 / 500 caracteres                      │
│                                         │
│ [Cancelar]           [Enviar ⭐]       │
└─────────────────────────────────────────┘
```

---

### 3. Indicador en Lista de Torneos
**Ubicación:** Lista de torneos del usuario (Mis Torneos)

**Para cada torneo FINALIZADO:**
- Mostrar estado de calificación:
  - ✅ "Ya calificaste" (verde) - si ya existe calificación
  - ⭐ "Calificar organizador" (amarillo/botón) - si aún no calificó

---

## Ejemplos de Código

### Llamada para Calificar (React/TypeScript):
```typescript
const rateOrganizer = async (organizerId: number, tournamentId: number, score: number, comment?: string) => {
  try {
    const response = await fetch(`/api/organizers/${organizerId}/rate`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        tournamentId,
        score,
        comment
      })
    });

    if (!response.ok) {
      const error = await response.text();
      throw new Error(error);
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error al calificar:', error);
    throw error;
  }
};
```

### Llamada para Obtener Reputación (React/TypeScript):
```typescript
const getOrganizerReputation = async (organizerId: number) => {
  try {
    const response = await fetch(`/api/organizers/${organizerId}/reputation`);
    
    if (!response.ok) {
      throw new Error('Error al obtener reputación');
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error al obtener reputación:', error);
    throw error;
  }
};
```

---

## Consideraciones Importantes

### 1. Manejo de Estados
- Deshabilitar botón de envío mientras se procesa la solicitud
- Mostrar indicador de carga durante la petición
- Limpiar formulario después de calificación exitosa

### 2. Mensajes de Usuario
- **Éxito:** "¡Gracias por tu calificación! Tu opinión ayuda a mejorar la comunidad."
- **Error 409 (Duplicado):** "Ya has calificado a este organizador en este torneo."
- **Error 403 (No participó):** "Solo los participantes pueden calificar este torneo."
- **Error 400 (Torneo no finalizado):** "Solo puedes calificar después de que finalice el torneo."

### 3. Permisos y Visibilidad
- La reputación de un organizador es PÚBLICA (cualquiera puede verla)
- Solo usuarios autenticados que participaron en un torneo FINALIZADO pueden calificar
- No mostrar la opción de calificar si el usuario no participó o el torneo no finalizó

### 4. Experiencia de Usuario
- Considerar agregar tooltip/ayuda explicando el sistema de calificación
- Mostrar confirmación antes de enviar ("¿Estás seguro de tu calificación? No podrás cambiarla")
- En el perfil del organizador, destacar si tiene excelente reputación (>4.5 estrellas)

### 5. Casos Edge
- Si un organizador no tiene calificaciones: mostrar "Sin calificaciones aún"
- Si `averageScore` es 0.0: mostrar "N/A" en lugar de 0.0
- Manejar comentarios nulos en la lista de calificaciones recientes

---

## Flujo Completo de Usuario

1. Usuario participa en un torneo
2. El torneo finaliza (estado cambia a FINALIZADO)
3. En "Mis Torneos", el usuario ve el torneo con botón "Calificar Organizador"
4. Usuario hace clic y se abre el modal de calificación
5. Usuario selecciona estrellas y opcionalmente escribe comentario
6. Usuario envía la calificación
7. Sistema valida y guarda la calificación
8. Usuario ve mensaje de éxito
9. El botón cambia a "Ya calificaste" (deshabilitado)
10. La calificación aparece en el perfil público del organizador

---

## Testing Recomendado

### Casos de Prueba:
1. ✅ Calificar con 5 estrellas y comentario
2. ✅ Calificar con 1 estrella sin comentario
3. ✅ Intentar calificar sin autenticación (debe fallar)
4. ✅ Intentar calificar torneo no finalizado (debe fallar)
5. ✅ Intentar calificar dos veces el mismo torneo (debe fallar)
6. ✅ Ver reputación de organizador sin calificaciones
7. ✅ Ver reputación de organizador con múltiples calificaciones
8. ✅ Validar que solo aparezca botón en torneos donde participó el usuario

---

## Notas Finales

- El sistema usa constraint de base de datos para prevenir calificaciones duplicadas
- Las calificaciones son permanentes (no se pueden editar ni eliminar)
- El promedio se calcula automáticamente en el backend
- Las fechas están en formato ISO 8601
- Considerar implementar paginación si un organizador tiene muchas calificaciones
