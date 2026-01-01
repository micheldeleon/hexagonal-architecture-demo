package com.example.demo.adapters.in.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private List<NotificationDTO> notifications;
    private int totalCount;
    private int unreadCount;
}
