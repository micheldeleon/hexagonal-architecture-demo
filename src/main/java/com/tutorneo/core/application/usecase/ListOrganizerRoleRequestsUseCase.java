package com.tutorneo.core.application.usecase;

import java.util.List;

import com.tutorneo.core.domain.models.OrganizerRoleRequest;
import com.tutorneo.core.domain.models.OrganizerRoleRequestStatus;
import com.tutorneo.core.ports.in.ListOrganizerRoleRequestsPort;
import com.tutorneo.core.ports.out.OrganizerRoleRequestRepositoryPort;

public class ListOrganizerRoleRequestsUseCase implements ListOrganizerRoleRequestsPort {

    private final OrganizerRoleRequestRepositoryPort requestRepositoryPort;

    public ListOrganizerRoleRequestsUseCase(OrganizerRoleRequestRepositoryPort requestRepositoryPort) {
        this.requestRepositoryPort = requestRepositoryPort;
    }

    @Override
    public List<OrganizerRoleRequest> listByStatus(OrganizerRoleRequestStatus status) {
        OrganizerRoleRequestStatus finalStatus = status == null ? OrganizerRoleRequestStatus.PENDING : status;
        return requestRepositoryPort.findByStatus(finalStatus);
    }
}

