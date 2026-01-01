package com.example.demo.core.application.usecase;

import java.util.List;

import com.example.demo.core.domain.models.Notification;
import com.example.demo.core.ports.in.GetUserNotificationsPort;
import com.example.demo.core.ports.out.NotificationPort;

public class GetUserNotificationsUseCase implements GetUserNotificationsPort {

    private final NotificationPort notificationPort;

    public GetUserNotificationsUseCase(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    @Override
    public List<Notification> getUserNotifications(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario es requerido");
        }
        return notificationPort.findByUserId(userId);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario es requerido");
        }
        return notificationPort.findUnreadByUserId(userId);
    }

    @Override
    public int getUnreadCount(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario es requerido");
        }
        return notificationPort.countUnreadByUserId(userId);
    }
}
