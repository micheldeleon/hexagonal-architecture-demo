package com.example.demo.core.domain.models;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserSummary {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private OffsetDateTime createdAt;
    private Instant deletedAt;
    private Long deletedBy;
    private String deleteReason;
    private List<String> roles;
    private Long tournamentsOrganizedCount;
    private Long totalParticipations;
}

