package com.example.demo.adapters.in.api.dto;

import com.example.demo.core.domain.models.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {
    @NotNull
    private Long userId;
    
    @NotNull
    private NotificationType type;
    
    @NotBlank
    private String title;
    
    private String message;
    
    private Long relatedEntityId;
}
