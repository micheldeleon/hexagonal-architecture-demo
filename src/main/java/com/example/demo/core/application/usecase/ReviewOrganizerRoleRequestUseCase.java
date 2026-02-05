package com.example.demo.core.application.usecase;

import java.time.OffsetDateTime;

import com.example.demo.core.domain.models.OrganizerRoleRequest;
import com.example.demo.core.domain.models.OrganizerRoleRequestStatus;
import com.example.demo.core.ports.in.ReviewOrganizerRoleRequestPort;
import com.example.demo.core.ports.out.OrganizerRoleRequestRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;

public class ReviewOrganizerRoleRequestUseCase implements ReviewOrganizerRoleRequestPort {

    private final OrganizerRoleRequestRepositoryPort requestRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public ReviewOrganizerRoleRequestUseCase(
            OrganizerRoleRequestRepositoryPort requestRepositoryPort,
            UserRepositoryPort userRepositoryPort) {
        this.requestRepositoryPort = requestRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public OrganizerRoleRequest approve(Long requestId, Long adminUserId, String note) {
        OrganizerRoleRequest req = requestRepositoryPort.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (req.getStatus() != OrganizerRoleRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be approved");
        }

        req.setStatus(OrganizerRoleRequestStatus.APPROVED);
        req.setReviewedAt(OffsetDateTime.now());
        req.setReviewedBy(adminUserId);
        req.setReviewNote((note == null || note.trim().isEmpty()) ? null : note.trim());
        req.setRejectReason(null);

        OrganizerRoleRequest saved = requestRepositoryPort.save(req);
        userRepositoryPort.addRole(req.getUserId(), "ROLE_ORGANIZER");
        return saved;
    }

    @Override
    public OrganizerRoleRequest reject(Long requestId, Long adminUserId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reject reason is required");
        }

        OrganizerRoleRequest req = requestRepositoryPort.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (req.getStatus() != OrganizerRoleRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be rejected");
        }

        req.setStatus(OrganizerRoleRequestStatus.REJECTED);
        req.setReviewedAt(OffsetDateTime.now());
        req.setReviewedBy(adminUserId);
        req.setReviewNote(null);
        req.setRejectReason(reason.trim());

        return requestRepositoryPort.save(req);
    }
}

