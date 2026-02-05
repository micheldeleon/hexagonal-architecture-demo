package com.example.demo.core.ports.in;

import com.example.demo.core.domain.models.OrganizerRoleRequest;

public interface RequestOrganizerRolePort {
    OrganizerRoleRequest request(Long userId, String message);
}

