package com.example.demo.core.ports.in;

import com.example.demo.core.domain.models.OrganizerRoleRequest;

public interface ReviewOrganizerRoleRequestPort {
    OrganizerRoleRequest approve(Long requestId, Long adminUserId, String note);
    OrganizerRoleRequest reject(Long requestId, Long adminUserId, String reason);
}

