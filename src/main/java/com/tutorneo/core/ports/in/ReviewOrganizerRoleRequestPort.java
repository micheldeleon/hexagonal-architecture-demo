package com.tutorneo.core.ports.in;

import com.tutorneo.core.domain.models.OrganizerRoleRequest;

public interface ReviewOrganizerRoleRequestPort {
    OrganizerRoleRequest approve(Long requestId, Long adminUserId, String note);
    OrganizerRoleRequest reject(Long requestId, Long adminUserId, String reason);
}

