package com.example.demo.core.ports.in;

import com.example.demo.core.domain.models.Notification;
import com.example.demo.core.domain.models.NotificationType;

public interface CreateNotificationPort {
    Notification createNotification(Long userId, NotificationType type, String title, String message, Long relatedEntityId);
}
