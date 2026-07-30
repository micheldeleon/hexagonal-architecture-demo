package com.tutorneo.core.application.usecase;

import com.tutorneo.core.domain.models.Notification;
import com.tutorneo.core.ports.in.MarkNotificationAsReadPort;
import com.tutorneo.core.ports.out.NotificationPort;

public class MarkNotificationAsReadUseCase implements MarkNotificationAsReadPort {

    private final NotificationPort notificationPort;

    public MarkNotificationAsReadUseCase(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    @Override
    public void markAsRead(Long userId, Long notificationId) {
        if (userId == null || notificationId == null) {
            throw new IllegalArgumentException("El ID de usuario y notificación son requeridos");
        }

        Notification notification = notificationPort.findById(notificationId);
        if (notification == null) {
            throw new IllegalArgumentException("Notificación no encontrada");
        }

        if (!notification.getUserId().equals(userId)) {
            throw new SecurityException("No tienes permiso para marcar esta notificación");
        }

        notificationPort.markAsRead(notificationId);
    }

    @Override
    public void markAllAsRead(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario es requerido");
        }
        notificationPort.markAllAsRead(userId);
    }
}
