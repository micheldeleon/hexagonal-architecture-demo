package com.example.demo.adapters.in.api.dto;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminUserDto {
    private Long id;
    private String email;
    private String fullName;
    private OffsetDateTime createdAt;
    private Instant deletedAt;
    private Long deletedBy;
    private String deleteReason;
    private Long tournamentsOrganizedCount;
    private Long totalParticipations;
    private List<String> roles;

    public AdminUserDto(
            Long id,
            String email,
            String name,
            String lastName,
            OffsetDateTime createdAt,
            Instant deletedAt,
            Long deletedBy,
            String deleteReason,
            Long tournamentsOrganizedCount,
            Long totalParticipations,
            List<String> roles) {
        this.id = id;
        this.email = email;
        this.fullName = name + " " + lastName;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
        this.deleteReason = deleteReason;
        this.tournamentsOrganizedCount = tournamentsOrganizedCount;
        this.totalParticipations = totalParticipations;
        this.roles = roles;
    }
}
