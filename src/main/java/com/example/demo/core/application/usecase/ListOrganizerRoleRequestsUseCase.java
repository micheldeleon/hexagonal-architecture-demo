package com.example.demo.core.application.usecase;

import java.util.List;

import com.example.demo.core.domain.models.OrganizerRoleRequest;
import com.example.demo.core.domain.models.OrganizerRoleRequestStatus;
import com.example.demo.core.ports.in.ListOrganizerRoleRequestsPort;
import com.example.demo.core.ports.out.OrganizerRoleRequestRepositoryPort;

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

