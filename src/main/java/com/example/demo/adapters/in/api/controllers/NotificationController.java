package com.example.demo.adapters.in.api.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adapters.in.api.dto.NotificationDTO;
import com.example.demo.adapters.in.api.dto.NotificationResponse;
import com.example.demo.core.domain.models.Notification;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.GetUserNotificationsPort;
import com.example.demo.core.ports.in.MarkNotificationAsReadPort;
import com.example.demo.core.ports.out.UserRepositoryPort;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final GetUserNotificationsPort getUserNotificationsPort;
    private final MarkNotificationAsReadPort markNotificationAsReadPort;
    private final UserRepositoryPort userRepositoryPort;

    public NotificationController(
            GetUserNotificationsPort getUserNotificationsPort,
            MarkNotificationAsReadPort markNotificationAsReadPort,
            UserRepositoryPort userRepositoryPort) {
        this.getUserNotificationsPort = getUserNotificationsPort;
        this.markNotificationAsReadPort = markNotificationAsReadPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @GetMapping
    public ResponseEntity<NotificationResponse> getAllNotifications(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<Notification> notifications = getUserNotificationsPort.getUserNotifications(user.getId());
        int unreadCount = getUserNotificationsPort.getUnreadCount(user.getId());

        List<NotificationDTO> dtos = notifications.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        NotificationResponse response = new NotificationResponse(dtos, dtos.size(), unreadCount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread")
    public ResponseEntity<NotificationResponse> getUnreadNotifications(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<Notification> notifications = getUserNotificationsPort.getUnreadNotifications(user.getId());

        List<NotificationDTO> dtos = notifications.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        NotificationResponse response = new NotificationResponse(dtos, dtos.size(), dtos.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Integer> getUnreadCount(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        int count = getUserNotificationsPort.getUnreadCount(user.getId());
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long notificationId, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        markNotificationAsReadPort.markAsRead(user.getId(), notificationId);
        return ResponseEntity.ok("Notificación marcada como leída");
    }

    @PutMapping("/read-all")
    public ResponseEntity<String> markAllAsRead(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        markNotificationAsReadPort.markAllAsRead(user.getId());
        return ResponseEntity.ok("Todas las notificaciones marcadas como leídas");
    }

    private NotificationDTO toDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getRelatedEntityId(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getReadAt());
    }
}
