package com.tutorneo.core.ports.in;

import com.tutorneo.core.domain.models.OrganizerRoleRequest;

public interface RequestOrganizerRolePort {
    OrganizerRoleRequest request(Long userId, String message);
}

