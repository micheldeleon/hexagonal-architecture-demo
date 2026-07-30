package com.tutorneo.core.ports.in;

import java.util.List;

import com.tutorneo.core.domain.models.OrganizerRoleRequest;
import com.tutorneo.core.domain.models.OrganizerRoleRequestStatus;

public interface ListOrganizerRoleRequestsPort {
    List<OrganizerRoleRequest> listByStatus(OrganizerRoleRequestStatus status);
}
