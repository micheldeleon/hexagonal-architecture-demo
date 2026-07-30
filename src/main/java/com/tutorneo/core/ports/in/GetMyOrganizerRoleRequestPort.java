package com.tutorneo.core.ports.in;

import java.util.Optional;

import com.tutorneo.core.domain.models.OrganizerRoleRequest;

public interface GetMyOrganizerRoleRequestPort {
    Optional<OrganizerRoleRequest> getLatest(Long userId);
}

