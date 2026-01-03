package com.example.demo.core.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler que envía heartbeats periódicos a todas las conexiones SSE activas.
 * Esto previene que las conexiones se cierren por inactividad.
 */
@Component
public class NotificationHeartbeatScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationHeartbeatScheduler.class);
    
    private final NotificationSseService notificationSseService;

    public NotificationHeartbeatScheduler(NotificationSseService notificationSseService) {
        this.notificationSseService = notificationSseService;
    }

    /**
     * Envía un heartbeat cada 45 segundos a todas las conexiones SSE activas.
     * Esto mantiene las conexiones vivas y previene timeouts del cliente.
     */
    @Scheduled(fixedRate = 45000) // 45 segundos
    public void sendHeartbeat() {
        int activeConnections = notificationSseService.getActiveConnectionsCount();
        if (activeConnections > 0) {
            logger.debug("Sending heartbeat to {} active SSE connections", activeConnections);
            notificationSseService.sendHeartbeatToAll();
        }
    }
}
