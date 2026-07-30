package com.tutorneo.adapters.in.api.mappers;

import com.tutorneo.adapters.in.api.dto.AdminUserDto;
import com.tutorneo.core.domain.models.AdminUserSummary;

public class AdminUserMapperDtos {
    public static AdminUserDto toAdminDto(AdminUserSummary user) {
        return new AdminUserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getLastName(),
                user.getCreatedAt(),
                user.getDeletedAt(),
                user.getDeletedBy(),
                user.getDeleteReason(),
                user.getTournamentsOrganizedCount(),
                user.getTotalParticipations(),
                user.getRoles());
    }
}
