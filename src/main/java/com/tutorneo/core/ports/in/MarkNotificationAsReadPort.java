package com.tutorneo.core.ports.in;

public interface MarkNotificationAsReadPort {
    void markAsRead(Long userId, Long notificationId);
    void markAllAsRead(Long userId);
}
