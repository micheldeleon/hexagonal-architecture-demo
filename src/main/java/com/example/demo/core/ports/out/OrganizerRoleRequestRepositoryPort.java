package com.example.demo.core.ports.out;

import java.util.List;
import java.util.Optional;

import com.example.demo.core.domain.models.OrganizerRoleRequest;
import com.example.demo.core.domain.models.OrganizerRoleRequestStatus;

public interface OrganizerRoleRequestRepositoryPort {
    OrganizerRoleRequest save(OrganizerRoleRequest request);
    Optional<OrganizerRoleRequest> findById(Long id);
    Optional<OrganizerRoleRequest> findLatestByUserId(Long userId);
    Optional<OrganizerRoleRequest> findPendingByUserId(Long userId);
    List<OrganizerRoleRequest> findByStatus(OrganizerRoleRequestStatus status);
}

