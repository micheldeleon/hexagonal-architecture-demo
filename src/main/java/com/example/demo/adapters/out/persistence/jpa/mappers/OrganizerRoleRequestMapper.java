package com.example.demo.adapters.out.persistence.jpa.mappers;

import com.example.demo.adapters.out.persistence.jpa.entities.OrganizerRoleRequestEntity;
import com.example.demo.core.domain.models.OrganizerRoleRequest;
import com.example.demo.core.domain.models.OrganizerRoleRequestStatus;

public class OrganizerRoleRequestMapper {
    public static OrganizerRoleRequest toDomain(OrganizerRoleRequestEntity entity) {
        if (entity == null) {
            return null;
        }
        OrganizerRoleRequest domain = new OrganizerRoleRequest();
        domain.setId(entity.getId());
        domain.setUserId(entity.getUserId());
        domain.setStatus(OrganizerRoleRequestStatus.valueOf(entity.getStatus()));
        domain.setMessage(entity.getMessage());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setReviewedAt(entity.getReviewedAt());
        domain.setReviewedBy(entity.getReviewedBy());
        domain.setReviewNote(entity.getReviewNote());
        domain.setRejectReason(entity.getRejectReason());
        return domain;
    }

    public static OrganizerRoleRequestEntity toEntity(OrganizerRoleRequest domain) {
        OrganizerRoleRequestEntity entity = new OrganizerRoleRequestEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setStatus(domain.getStatus().name());
        entity.setMessage(domain.getMessage());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setReviewedAt(domain.getReviewedAt());
        entity.setReviewedBy(domain.getReviewedBy());
        entity.setReviewNote(domain.getReviewNote());
        entity.setRejectReason(domain.getRejectReason());
        return entity;
    }
}

