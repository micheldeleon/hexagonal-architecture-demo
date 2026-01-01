package com.example.demo.core.ports.out;

import com.example.demo.core.domain.models.Notification;
import com.example.demo.core.domain.models.NotificationType;
import java.util.List;

public interface NotificationPort {
    Notification save(Notification notification);
    List<Notification> findByUserId(Long userId);
    List<Notification> findUnreadByUserId(Long userId);
    Notification findById(Long id);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    int countUnreadByUserId(Long userId);
    void notifyUsersOfTournament(Long tournamentId, String title, String message, NotificationType type);
    void notifyTeamMembers(Long teamId, String title, String message, NotificationType type);
}
