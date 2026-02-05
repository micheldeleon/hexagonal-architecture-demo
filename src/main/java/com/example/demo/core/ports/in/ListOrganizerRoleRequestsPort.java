package com.example.demo.core.ports.in;

import java.util.List;

import com.example.demo.core.domain.models.OrganizerRoleRequest;
import com.example.demo.core.domain.models.OrganizerRoleRequestStatus;

public interface ListOrganizerRoleRequestsPort {
    List<OrganizerRoleRequest> listByStatus(OrganizerRoleRequestStatus status);
}

