package com.tutorneo.core.ports.in;

import com.tutorneo.core.domain.models.Notification;
import com.tutorneo.core.domain.models.NotificationType;

public interface CreateNotificationPort {
    Notification createNotification(Long userId, NotificationType type, String title, String message, Long relatedEntityId);
}
