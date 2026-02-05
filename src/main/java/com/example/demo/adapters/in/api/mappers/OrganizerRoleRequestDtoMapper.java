package com.example.demo.adapters.in.api.mappers;

import com.example.demo.adapters.in.api.dto.OrganizerRoleRequestDto;
import com.example.demo.core.domain.models.OrganizerRoleRequest;

public class OrganizerRoleRequestDtoMapper {
    public static OrganizerRoleRequestDto toDto(OrganizerRoleRequest request) {
        OrganizerRoleRequestDto dto = new OrganizerRoleRequestDto();
        dto.setId(request.getId());
        dto.setUserId(request.getUserId());
        dto.setStatus(request.getStatus().name());
        dto.setMessage(request.getMessage());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setReviewedAt(request.getReviewedAt());
        dto.setReviewedBy(request.getReviewedBy());
        dto.setReviewNote(request.getReviewNote());
        dto.setRejectReason(request.getRejectReason());
        return dto;
    }
}

