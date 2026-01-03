package com.example.demo.core.application.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.core.domain.models.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servicio para gestionar conexiones Server-Sent Events (SSE) de notificaciones en tiempo real.
 * Mantiene un registro de emitters por usuario y envía notificaciones cuando se crean.
 */
@Service
public class NotificationSseService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationSseService.class);
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30 minutos

    // Map: userId -> SseEmitter
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public NotificationSseService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Crea y registra un nuevo emitter SSE para un usuario.
     * 
     * @param userId ID del usuario que se conecta
     * @return SseEmitter configurado para el usuario
     */
    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        // Configurar callbacks para limpieza
        emitter.onCompletion(() -> {
            logger.info("SSE connection completed for user {}", userId);
            removeEmitter(userId);
        });

        emitter.onTimeout(() -> {
            logger.info("SSE connection timeout for user {}", userId);
            removeEmitter(userId);
        });

        emitter.onError((e) -> {
            logger.error("SSE connection error for user {}: {}", userId, e.getMessage());
            removeEmitter(userId);
        });

        // Registrar el emitter
        emitters.put(userId, emitter);
        logger.info("New SSE connection established for user {}. Total connections: {}", userId, emitters.size());

        // Enviar evento inicial de conexión
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("{\"message\":\"Connected to notification stream\"}"));
        } catch (IOException e) {
            logger.error("Error sending initial connection event to user {}", userId, e);
            removeEmitter(userId);
        }

        return emitter;
    }

    /**
     * Envía una notificación a un usuario específico si está conectado.
     * 
     * @param userId ID del usuario destinatario
     * @param notification Notificación a enviar
     */
    public void sendNotificationToUser(Long userId, Notification notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                String jsonData = objectMapper.writeValueAsString(Map.of(
                        "id", notification.getId(),
                        "type", notification.getType(),
                        "title", notification.getTitle(),
                        "message", notification.getMessage(),
                        "relatedEntityId", notification.getRelatedEntityId(),
                        "isRead", notification.isRead(),
                        "createdAt", notification.getCreatedAt()));

                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(jsonData));

                logger.info("Notification sent to user {} via SSE", userId);
            } catch (IOException e) {
                logger.error("Error sending notification to user {} via SSE", userId, e);
                removeEmitter(userId);
            }
        } else {
            logger.debug("No active SSE connection for user {}. Notification will be retrieved via polling.", userId);
        }
    }

    /**
     * Elimina el emitter de un usuario.
     * 
     * @param userId ID del usuario
     */
    private void removeEmitter(Long userId) {
        SseEmitter emitter = emitters.remove(userId);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception e) {
                logger.debug("Error completing emitter for user {}", userId);
            }
            logger.info("SSE connection removed for user {}. Remaining connections: {}", userId, emitters.size());
        }
    }

    /**
     * Envía un evento de heartbeat/keepalive a todas las conexiones activas.
     * Útil para mantener las conexiones vivas en redes con timeouts agresivos.
     */
    public void sendHeartbeatToAll() {
        logger.debug("Sending heartbeat to {} active connections", emitters.size());
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("heartbeat")
                        .data("{\"timestamp\":" + System.currentTimeMillis() + "}"));
            } catch (IOException e) {
                logger.debug("Error sending heartbeat to user {}: {}", userId, e.getMessage());
                removeEmitter(userId);
            }
        });
    }

    /**
     * Obtiene el número de conexiones activas.
     * 
     * @return Número de usuarios conectados
     */
    public int getActiveConnectionsCount() {
        return emitters.size();
    }
}
