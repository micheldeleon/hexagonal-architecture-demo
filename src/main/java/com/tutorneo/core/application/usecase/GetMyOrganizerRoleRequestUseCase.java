package com.tutorneo.core.application.usecase;

import java.util.Optional;

import com.tutorneo.core.domain.models.OrganizerRoleRequest;
import com.tutorneo.core.ports.in.GetMyOrganizerRoleRequestPort;
import com.tutorneo.core.ports.out.OrganizerRoleRequestRepositoryPort;

public class GetMyOrganizerRoleRequestUseCase implements GetMyOrganizerRoleRequestPort {

    private final OrganizerRoleRequestRepositoryPort requestRepositoryPort;

    public GetMyOrganizerRoleRequestUseCase(OrganizerRoleRequestRepositoryPort requestRepositoryPort) {
        this.requestRepositoryPort = requestRepositoryPort;
    }

    @Override
    public Optional<OrganizerRoleRequest> getLatest(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
        return requestRepositoryPort.findLatestByUserId(userId);
    }
}

