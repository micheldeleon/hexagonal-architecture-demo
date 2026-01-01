package com.example.demo.core.domain.models;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Long id;
    private Long userId;
    private NotificationType type;
    private String title;
    private String message;
    private Long relatedEntityId; // ID del torneo, equipo, etc.
    private boolean isRead;
    private Date createdAt;
    private Date readAt;

    public Notification(Long userId, NotificationType type, String title, String message, Long relatedEntityId) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.relatedEntityId = relatedEntityId;
        this.isRead = false;
        this.createdAt = new Date();
    }
}

