package com.example.demo.core.domain.models;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerRoleRequest {
    private Long id;
    private Long userId;
    private OrganizerRoleRequestStatus status;
    private String message;
    private OffsetDateTime createdAt;
    private OffsetDateTime reviewedAt;
    private Long reviewedBy;
    private String reviewNote;
    private String rejectReason;
}

