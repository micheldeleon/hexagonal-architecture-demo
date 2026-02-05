package com.example.demo.adapters.in.api.dto;

import java.time.OffsetDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrganizerRoleRequestDto {
    private Long id;
    private Long userId;
    private String status;
    private String message;
    private OffsetDateTime createdAt;
    private OffsetDateTime reviewedAt;
    private Long reviewedBy;
    private String reviewNote;
    private String rejectReason;
}

