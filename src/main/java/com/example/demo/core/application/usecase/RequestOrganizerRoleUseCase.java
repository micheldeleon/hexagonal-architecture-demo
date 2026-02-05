package com.example.demo.core.application.usecase;

import java.time.OffsetDateTime;

import com.example.demo.core.domain.models.OrganizerRoleRequest;
import com.example.demo.core.domain.models.OrganizerRoleRequestStatus;
import com.example.demo.core.ports.in.RequestOrganizerRolePort;
import com.example.demo.core.ports.out.OrganizerRoleRequestRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;

public class RequestOrganizerRoleUseCase implements RequestOrganizerRolePort {

    private final OrganizerRoleRequestRepositoryPort requestRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public RequestOrganizerRoleUseCase(
            OrganizerRoleRequestRepositoryPort requestRepositoryPort,
            UserRepositoryPort userRepositoryPort) {
        this.requestRepositoryPort = requestRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public OrganizerRoleRequest request(Long userId, String message) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }

        if (userRepositoryPort.hasRole(userId, "ROLE_ORGANIZER")) {
            throw new IllegalStateException("User already has organizer role");
        }

        var pending = requestRepositoryPort.findPendingByUserId(userId);
        if (pending.isPresent()) {
            return pending.get();
        }

        String normalizedMessage = (message == null || message.trim().isEmpty()) ? null : message.trim();
        OrganizerRoleRequest req = new OrganizerRoleRequest(
                null,
                userId,
                OrganizerRoleRequestStatus.PENDING,
                normalizedMessage,
                OffsetDateTime.now(),
                null,
                null,
                null,
                null);

        return requestRepositoryPort.save(req);
    }
}

