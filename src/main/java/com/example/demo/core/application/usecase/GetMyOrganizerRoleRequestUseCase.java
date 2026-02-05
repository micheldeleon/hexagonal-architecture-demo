package com.example.demo.core.application.usecase;

import java.util.Optional;

import com.example.demo.core.domain.models.OrganizerRoleRequest;
import com.example.demo.core.ports.in.GetMyOrganizerRoleRequestPort;
import com.example.demo.core.ports.out.OrganizerRoleRequestRepositoryPort;

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

