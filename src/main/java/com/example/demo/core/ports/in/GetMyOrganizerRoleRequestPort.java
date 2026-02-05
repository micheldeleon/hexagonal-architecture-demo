package com.example.demo.core.ports.in;

import java.util.Optional;

import com.example.demo.core.domain.models.OrganizerRoleRequest;

public interface GetMyOrganizerRoleRequestPort {
    Optional<OrganizerRoleRequest> getLatest(Long userId);
}

