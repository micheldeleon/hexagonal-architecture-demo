package com.example.demo.core.application.usecase;

import com.example.demo.core.application.service.NotificationSseService;
import com.example.demo.core.domain.models.Notification;
import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.ports.in.CreateNotificationPort;
import com.example.demo.core.ports.out.NotificationPort;

public class CreateNotificationUseCase implements CreateNotificationPort {

    private final NotificationPort notificationPort;
    private final NotificationSseService notificationSseService;

    public CreateNotificationUseCase(NotificationPort notificationPort, NotificationSseService notificationSseService) {
        this.notificationPort = notificationPort;
        this.notificationSseService = notificationSseService;
    }

    @Override
    public Notification createNotification(Long userId, NotificationType type, String title, String message, Long relatedEntityId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario es requerido");
        }
        if (type == null) {
            throw new IllegalArgumentException("El tipo de notificación es requerido");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("El título es requerido");
        }

        Notification notification = new Notification(userId, type, title, message, relatedEntityId);
        Notification savedNotification = notificationPort.save(notification);
        
        // Enviar notificación en tiempo real si el usuario está conectado
        notificationSseService.sendNotificationToUser(userId, savedNotification);
        
        return savedNotification;
    }
}
