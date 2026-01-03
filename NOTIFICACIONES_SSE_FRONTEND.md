# 🔔 Implementación Frontend - Notificaciones en Tiempo Real (SSE)

## 📋 Resumen de la Implementación Backend

Se implementó **Server-Sent Events (SSE)** para notificaciones en tiempo real:

- ✅ **Endpoint SSE**: `GET /api/notifications/stream` (requiere autenticación JWT)
- ✅ **Emisión automática**: Cada vez que se crea una notificación, se envía instantáneamente al usuario conectado
- ✅ **Fallback**: Si el usuario no está conectado, la notificación se guarda en BD para recuperarla después
- ✅ **Timeout**: 30 minutos de inactividad
- ✅ **Reconexión automática**: El cliente EventSource reconecta automáticamente

---

## 🎯 Implementación en React + TypeScript

### 1. Hook personalizado para SSE

```typescript
// hooks/useNotificationStream.ts
import { useEffect, useRef, useState } from 'react';

interface Notification {
  id: number;
  type: string;
  title: string;
  message: string;
  relatedEntityId: number | null;
  isRead: boolean;
  createdAt: string;
}

interface UseNotificationStreamOptions {
  onNotification?: (notification: Notification) => void;
  onConnected?: () => void;
  onError?: (error: Event) => void;
}

export const useNotificationStream = (
  token: string | null,
  options?: UseNotificationStreamOptions
) => {
  const [isConnected, setIsConnected] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const eventSourceRef = useRef<EventSource | null>(null);

  useEffect(() => {
    // No conectar si no hay token
    if (!token) {
      return;
    }

    // Crear conexión SSE
    const eventSource = new EventSource(
      `http://localhost:8080/api/notifications/stream`,
      {
        // IMPORTANTE: EventSource no soporta headers personalizados directamente
        // Alternativas:
        // 1. Pasar el token como query param (menos seguro)
        // 2. Usar cookies (más seguro, requiere cambios en backend)
        // 3. Usar una librería como eventsource-polyfill
      }
    );

    eventSourceRef.current = eventSource;

    // Evento de conexión establecida
    eventSource.addEventListener('connected', (event) => {
      console.log('SSE Connected:', event.data);
      setIsConnected(true);
      setError(null);
      options?.onConnected?.();
    });

    // Evento de nueva notificación
    eventSource.addEventListener('notification', (event) => {
      try {
        const notification: Notification = JSON.parse(event.data);
        console.log('New notification received:', notification);
        options?.onNotification?.(notification);
      } catch (err) {
        console.error('Error parsing notification:', err);
      }
    });

    // Manejo de errores
    eventSource.onerror = (event) => {
      console.error('SSE Error:', event);
      setIsConnected(false);
      setError('Error en la conexión. Reintentando...');
      options?.onError?.(event);
    };

    // Cleanup al desmontar
    return () => {
      console.log('Closing SSE connection');
      eventSource.close();
      setIsConnected(false);
    };
  }, [token]);

  return {
    isConnected,
    error,
    disconnect: () => {
      eventSourceRef.current?.close();
      setIsConnected(false);
    },
  };
};
```

---

### 2. Solución para Headers JWT con EventSource

EventSource no soporta headers personalizados. **Solución recomendada**: usar `event-source-polyfill`

```bash
npm install event-source-polyfill
npm install --save-dev @types/event-source-polyfill
```

**Hook mejorado con JWT:**

```typescript
// hooks/useNotificationStream.ts (versión con JWT)
import { useEffect, useRef, useState } from 'react';
import { EventSourcePolyfill } from 'event-source-polyfill';

// ... (interfaces igual que antes)

export const useNotificationStream = (
  token: string | null,
  options?: UseNotificationStreamOptions
) => {
  const [isConnected, setIsConnected] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const eventSourceRef = useRef<EventSourcePolyfill | null>(null);

  useEffect(() => {
    if (!token) {
      return;
    }

    // Crear conexión SSE con Authorization header
    const eventSource = new EventSourcePolyfill(
      'http://localhost:8080/api/notifications/stream',
      {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
        heartbeatTimeout: 60000, // 60 segundos
      }
    );

    eventSourceRef.current = eventSource;

    eventSource.addEventListener('connected', (event: any) => {
      console.log('SSE Connected:', event.data);
      setIsConnected(true);
      setError(null);
      options?.onConnected?.();
    });

    eventSource.addEventListener('notification', (event: any) => {
      try {
        const notification: Notification = JSON.parse(event.data);
        console.log('New notification received:', notification);
        options?.onNotification?.(notification);
      } catch (err) {
        console.error('Error parsing notification:', err);
      }
    });

    eventSource.onerror = (event: any) => {
      console.error('SSE Error:', event);
      setIsConnected(false);
      setError('Error en la conexión. Reintentando...');
      options?.onError?.(event);
    };

    return () => {
      console.log('Closing SSE connection');
      eventSource.close();
      setIsConnected(false);
    };
  }, [token]);

  return { isConnected, error };
};
```

---

### 3. Contexto de Notificaciones

```typescript
// context/NotificationContext.tsx
import React, { createContext, useContext, useState, useCallback } from 'react';
import { useNotificationStream } from '../hooks/useNotificationStream';

interface Notification {
  id: number;
  type: string;
  title: string;
  message: string;
  relatedEntityId: number | null;
  isRead: boolean;
  createdAt: string;
}

interface NotificationContextType {
  notifications: Notification[];
  unreadCount: number;
  addNotification: (notification: Notification) => void;
  markAsRead: (id: number) => void;
  isConnected: boolean;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

export const NotificationProvider: React.FC<{ 
  children: React.ReactNode;
  token: string | null;
}> = ({ children, token }) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);

  // Callback cuando llega una notificación en tiempo real
  const handleNewNotification = useCallback((notification: Notification) => {
    // Agregar al inicio de la lista
    setNotifications(prev => [notification, ...prev]);
    
    // Incrementar contador si no está leída
    if (!notification.isRead) {
      setUnreadCount(prev => prev + 1);
    }

    // Mostrar notificación del navegador (opcional)
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification(notification.title, {
        body: notification.message,
        icon: '/logo.png', // Tu logo
        tag: `notification-${notification.id}`,
      });
    }

    // Reproducir sonido (opcional)
    const audio = new Audio('/notification-sound.mp3');
    audio.play().catch(err => console.log('Error playing sound:', err));
  }, []);

  // Conectar al stream SSE
  const { isConnected } = useNotificationStream(token, {
    onNotification: handleNewNotification,
    onConnected: () => {
      console.log('🔔 Conectado al stream de notificaciones');
    },
    onError: (error) => {
      console.error('❌ Error en el stream:', error);
    },
  });

  const addNotification = useCallback((notification: Notification) => {
    setNotifications(prev => [notification, ...prev]);
    if (!notification.isRead) {
      setUnreadCount(prev => prev + 1);
    }
  }, []);

  const markAsRead = useCallback((id: number) => {
    setNotifications(prev =>
      prev.map(n => (n.id === id ? { ...n, isRead: true } : n))
    );
    setUnreadCount(prev => Math.max(0, prev - 1));
  }, []);

  return (
    <NotificationContext.Provider
      value={{
        notifications,
        unreadCount,
        addNotification,
        markAsRead,
        isConnected,
      }}
    >
      {children}
    </NotificationContext.Provider>
  );
};

export const useNotifications = () => {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error('useNotifications must be used within NotificationProvider');
  }
  return context;
};
```

---

### 4. Componente de Badge de Notificaciones

```typescript
// components/NotificationBadge.tsx
import React from 'react';
import { useNotifications } from '../context/NotificationContext';

export const NotificationBadge: React.FC = () => {
  const { unreadCount, isConnected } = useNotifications();

  return (
    <div className="relative">
      <button className="relative p-2 rounded-full hover:bg-gray-100">
        {/* Icono de campana */}
        <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
          <path d="M10 2a6 6 0 00-6 6v3.586l-.707.707A1 1 0 004 14h12a1 1 0 00.707-1.707L16 11.586V8a6 6 0 00-6-6zM10 18a3 3 0 01-3-3h6a3 3 0 01-3 3z" />
        </svg>

        {/* Badge con contador */}
        {unreadCount > 0 && (
          <span className="absolute top-0 right-0 inline-flex items-center justify-center px-2 py-1 text-xs font-bold leading-none text-white transform translate-x-1/2 -translate-y-1/2 bg-red-600 rounded-full">
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        )}

        {/* Indicador de conexión */}
        <span
          className={`absolute bottom-0 right-0 w-3 h-3 rounded-full border-2 border-white ${
            isConnected ? 'bg-green-500' : 'bg-gray-400'
          }`}
          title={isConnected ? 'Conectado' : 'Desconectado'}
        />
      </button>
    </div>
  );
};
```

---

### 5. Uso en App Principal

```typescript
// App.tsx
import React from 'react';
import { NotificationProvider } from './context/NotificationContext';
import { NotificationBadge } from './components/NotificationBadge';

function App() {
  const [token, setToken] = React.useState<string | null>(
    localStorage.getItem('token')
  );

  // Pedir permisos de notificaciones del navegador
  React.useEffect(() => {
    if ('Notification' in window && Notification.permission === 'default') {
      Notification.requestPermission();
    }
  }, []);

  return (
    <NotificationProvider token={token}>
      <div className="app">
        <header>
          <nav>
            {/* Tus componentes de navegación */}
            <NotificationBadge />
          </nav>
        </header>

        <main>
          {/* Tu contenido */}
        </main>
      </div>
    </NotificationProvider>
  );
}

export default App;
```

---

## 🧪 Testing Manual

### 1. Crear notificación de prueba

```bash
curl -X POST http://localhost:8080/api/notifications/create \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "type": "TOURNAMENT_CREATED",
    "title": "¡Nuevo torneo creado!",
    "message": "Se ha creado el torneo de fútbol 5",
    "relatedEntityId": 123
  }'
```

**Deberías ver la notificación aparecer instantáneamente en el navegador** 🎉

---

## 📊 Ventajas de SSE vs Polling

| Aspecto | SSE | Polling (30s) |
|---------|-----|---------------|
| **Latencia** | < 1 segundo | 0-30 segundos |
| **Ancho de banda** | Mínimo | Alto (requests constantes) |
| **Carga servidor** | Baja | Alta |
| **Batería móvil** | Eficiente | Consume más |
| **Complejidad** | Media | Baja |

---

## 🚀 Próximos Pasos (Opcional)

### Escalabilidad con Redis

Si planeas tener **múltiples instancias del servidor**, necesitas Redis para sincronizar notificaciones:

```java
// Publicar evento en Redis cuando se crea notificación
redisTemplate.convertAndSend("notifications:" + userId, notification);

// Escuchar eventos en todas las instancias
@RedisListener("notifications:*")
public void handleNotification(Notification notification) {
    notificationSseService.sendNotificationToUser(notification.getUserId(), notification);
}
```

---

## ✅ Checklist de Implementación

- [x] Backend: Servicio SSE creado
- [x] Backend: Endpoint `/stream` configurado
- [x] Backend: Integración con CreateNotificationUseCase
- [x] Backend: Seguridad JWT configurada
- [ ] Frontend: Instalar `event-source-polyfill`
- [ ] Frontend: Crear hook `useNotificationStream`
- [ ] Frontend: Crear contexto `NotificationProvider`
- [ ] Frontend: Implementar componente de badge
- [ ] Frontend: Integrar en App principal
- [ ] Testing: Probar creación de notificaciones
- [ ] Testing: Verificar reconexión automática
- [ ] Testing: Validar en múltiples pestañas

---

## 🐛 Troubleshooting

### "No recibo notificaciones en tiempo real"

1. ✅ Verifica que el usuario esté autenticado
2. ✅ Confirma que el endpoint `/stream` esté conectado (ver consola)
3. ✅ Revisa que el `userId` de la notificación coincida con el usuario conectado
4. ✅ Verifica que no haya errores CORS

### "La conexión se cierra constantemente"

- Aumenta el timeout en `NotificationSseService`
- Implementa heartbeat periódico (cada 30s)
- Verifica que no haya proxies/load balancers que corten conexiones largas

### "No puedo enviar JWT con EventSource"

- Usa `event-source-polyfill` (recomendado)
- Alternativa: pasar token como query param (menos seguro)

---

¡Tu sistema de notificaciones en tiempo real está listo! 🎉
