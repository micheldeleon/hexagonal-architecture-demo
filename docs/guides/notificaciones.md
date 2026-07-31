# Sistema de Notificaciones - Implementación Completa

## 📋 Resumen

Se ha implementado un sistema completo de notificaciones para la aplicación de torneos. Los usuarios ahora recibirán notificaciones cuando:
- Un torneo en el que están inscritos es cancelado
- Su equipo es eliminado de un torneo
- Y otros eventos importantes (extensible)

## 🗄️ Paso 1: Crear la tabla de notificaciones

**IMPORTANTE**: Antes de ejecutar la aplicación, debes ejecutar el siguiente SQL en tu base de datos:

```sql
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    related_entity_id BIGINT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
);
```

El archivo SQL está disponible en: `create_notifications_table.sql`

## 📡 Endpoints Disponibles

### 1. Obtener todas las notificaciones del usuario autenticado
```
GET /api/notifications
```
**Respuesta:**
```json
{
  "notifications": [
    {
      "id": 1,
      "type": "TOURNAMENT_CANCELED",
      "title": "Torneo Cancelado",
      "message": "El torneo 'Copa de Verano 2026' ha sido cancelado por el organizador.",
      "relatedEntityId": 5,
      "isRead": false,
      "createdAt": "2026-01-01T10:30:00",
      "readAt": null
    }
  ],
  "totalCount": 10,
  "unreadCount": 3
}
```

### 2. Obtener solo notificaciones no leídas
```
GET /api/notifications/unread
```

### 3. Obtener contador de notificaciones no leídas
```
GET /api/notifications/unread/count
```
**Respuesta:**
```json
3
```

### 4. Marcar una notificación como leída
```
PUT /api/notifications/{notificationId}/read
```

### 5. Marcar todas las notificaciones como leídas
```
PUT /api/notifications/read-all
```

## 🔔 Tipos de Notificaciones

El sistema soporta los siguientes tipos de notificaciones:

- `TOURNAMENT_CANCELED`: Torneo cancelado
- `TEAM_REMOVED`: Equipo eliminado del torneo
- `TOURNAMENT_STARTED`: Torneo iniciado
- `MATCH_SCHEDULED`: Partido programado
- `MATCH_RESULT`: Resultado de partido
- `REGISTRATION_CONFIRMED`: Inscripción confirmada
- `TOURNAMENT_FULL`: Torneo lleno
- `TOURNAMENT_REMINDER`: Recordatorio de torneo
- `GENERAL`: Notificación general

## 🔄 Eventos que Generan Notificaciones

### Automáticas (ya implementadas):

1. **Cancelación de Torneo** (`CancelTournamentUseCase`)
   - Notifica a todos los participantes del torneo
   - Tipo: `TOURNAMENT_CANCELED`

2. **Eliminación de Equipo** (`RemoveTeamFromTournamentUseCase`)
   - Notifica a todos los miembros del equipo eliminado
   - Tipo: `TEAM_REMOVED`

## 🏗️ Arquitectura

### Archivos Creados:

#### Dominio
- `NotificationType.java` - Enum con tipos de notificación
- `Notification.java` - Modelo de dominio actualizado

#### Entidades JPA
- `NotificationEntity.java` - Entidad de base de datos

#### Puertos
- `NotificationPort.java` - Puerto de salida para persistencia
- `GetUserNotificationsPort.java` - Puerto de entrada para consultas
- `MarkNotificationAsReadPort.java` - Puerto de entrada para marcar leídas

#### Casos de Uso
- `GetUserNotificationsUseCase.java` - Obtener notificaciones
- `MarkNotificationAsReadUseCase.java` - Marcar como leídas

#### Repositorio
- `NotificationRepository.java` - Implementación de persistencia

#### API
- `NotificationController.java` - Endpoints REST
- `NotificationDTO.java` - DTO de respuesta
- `NotificationResponse.java` - Wrapper de respuesta con metadata

#### Configuración
- `ApplicationConfig.java` - Beans configurados

### Archivos Modificados:

- `CancelTournamentUseCase.java` - Integración con notificaciones
- `RemoveTeamFromTournamentUseCase.java` - Integración con notificaciones
- `ApplicationConfig.java` - Nuevos beans configurados

## 🔌 Cómo Agregar Notificaciones a Otros Casos de Uso

Para agregar notificaciones a otros eventos, sigue este patrón:

```java
public class MiCasoDeUso {
    private final NotificationPort notificationPort;
    
    public MiCasoDeUso(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }
    
    public void miMetodo() {
        // Tu lógica de negocio...
        
        // Enviar notificación
        notificationPort.notifyUsersOfTournament(
            tournamentId,
            "Título de la Notificación",
            "Mensaje detallado",
            NotificationType.TOURNAMENT_STARTED
        );
        
        // O notificar a un equipo específico:
        notificationPort.notifyTeamMembers(
            teamId,
            "Título",
            "Mensaje",
            NotificationType.MATCH_SCHEDULED
        );
    }
}
```

No olvides agregar el `NotificationPort` al constructor del bean en `ApplicationConfig.java`.

## ✅ Testing

Para probar el sistema:

1. Ejecuta el SQL de migración
2. Reinicia la aplicación
3. Autentícate con un usuario
4. Inscribe un equipo en un torneo
5. Como organizador, cancela el torneo o elimina un equipo
6. Verifica las notificaciones en `/api/notifications`

## 🚀 Próximas Mejoras Sugeridas

1. **WebSockets**: Notificaciones en tiempo real
2. **Email**: Envío de notificaciones por correo
3. **Push Notifications**: Notificaciones móviles
4. **Preferencias**: Permitir a usuarios configurar qué notificaciones recibir
5. **Paginación**: Agregar paginación a la lista de notificaciones
6. **Filtros**: Filtrar notificaciones por tipo o fecha

## 📝 Notas Importantes

- Todas las notificaciones se envían de forma transaccional
- Si hay un error al enviar notificaciones, no falla la operación principal
- Las notificaciones se guardan con referencia al ID de la entidad relacionada
- El campo `relatedEntityId` permite navegar al torneo/equipo/partido relacionado
