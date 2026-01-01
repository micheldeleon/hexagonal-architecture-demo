package com.example.demo.adapters.in.api.dto;

import java.util.Date;

import com.example.demo.core.domain.models.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private Long relatedEntityId;
    private boolean isRead;
    private Date createdAt;
    private Date readAt;
}
