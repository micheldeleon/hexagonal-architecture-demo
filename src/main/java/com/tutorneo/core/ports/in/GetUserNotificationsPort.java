package com.tutorneo.core.ports.in;

import com.tutorneo.core.domain.models.Notification;
import java.util.List;

public interface GetUserNotificationsPort {
    List<Notification> getUserNotifications(Long userId);
    List<Notification> getUnreadNotifications(Long userId);
    int getUnreadCount(Long userId);
}
