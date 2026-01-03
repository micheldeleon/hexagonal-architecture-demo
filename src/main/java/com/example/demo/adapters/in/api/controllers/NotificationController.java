package com.example.demo.adapters.in.api.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.adapters.in.api.dto.CreateNotificationRequest;
import com.example.demo.adapters.in.api.dto.NotificationDTO;
import com.example.demo.adapters.in.api.dto.NotificationResponse;
import com.example.demo.core.application.service.NotificationSseService;
import com.example.demo.core.domain.models.Notification;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.CreateNotificationPort;
import com.example.demo.core.ports.in.GetUserNotificationsPort;
import com.example.demo.core.ports.in.MarkNotificationAsReadPort;
import com.example.demo.core.ports.out.UserRepositoryPort;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final GetUserNotificationsPort getUserNotificationsPort;
    private final MarkNotificationAsReadPort markNotificationAsReadPort;
    private final CreateNotificationPort createNotificationPort;
    private final UserRepositoryPort userRepositoryPort;
    private final NotificationSseService notificationSseService;

    public NotificationController(
            GetUserNotificationsPort getUserNotificationsPort,
            MarkNotificationAsReadPort markNotificationAsReadPort,
            CreateNotificationPort createNotificationPort,
            UserRepositoryPort userRepositoryPort,
            NotificationSseService notificationSseService) {
        this.getUserNotificationsPort = getUserNotificationsPort;
        this.markNotificationAsReadPort = markNotificationAsReadPort;
        this.createNotificationPort = createNotificationPort;
        this.userRepositoryPort = userRepositoryPort;
        this.notificationSseService = notificationSseService;
    }

    /**
     * Endpoint SSE para recibir notificaciones en tiempo real.
     * El cliente se conecta a este endpoint y mantiene la conexión abierta.
     * Cada vez que se crea una notificación para el usuario, se envía automáticamente.
     * 
     * IMPORTANTE: Sin @Transactional para evitar connection leaks.
     * Obtenemos el userId ANTES de retornar el emitter para cerrar la transacción.
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(Authentication authentication) {
        String email = authentication.getName();
        
        // Obtener userId y cerrar transacción ANTES de crear el emitter
        Long userId = userRepositoryPort.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        // Ahora crear el emitter sin mantener conexión DB abierta
        return notificationSseService.createEmitter(userId);
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

    @PostMapping("/create")
    public ResponseEntity<?> createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {
        try {
            Notification notification = createNotificationPort.createNotification(
                    request.getUserId(),
                    request.getType(),
                    request.getTitle(),
                    request.getMessage(),
                    request.getRelatedEntityId()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(notification));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
