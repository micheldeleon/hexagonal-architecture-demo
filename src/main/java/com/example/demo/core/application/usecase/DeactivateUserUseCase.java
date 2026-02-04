package com.example.demo.core.application.usecase;

import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.DeactivateUserPort;
import com.example.demo.core.ports.out.UserRepositoryPort;

public class DeactivateUserUseCase implements DeactivateUserPort {

    private final UserRepositoryPort userRepositoryPort;

    public DeactivateUserUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void deactivate(Long targetUserId, Long adminUserId, String reason) {
        if (targetUserId == null) {
            throw new IllegalArgumentException("Target user id is required");
        }
        if (adminUserId == null) {
            throw new IllegalArgumentException("Admin user id is required");
        }
        if (targetUserId.equals(adminUserId)) {
            throw new IllegalArgumentException("Admins cannot deactivate their own account");
        }

        User target = userRepositoryPort.findByIdIncludingDeleted(targetUserId);
        if (target.isDeleted()) {
            throw new IllegalStateException("User already deactivated");
        }

        String normalizedReason = (reason == null || reason.trim().isEmpty()) ? null : reason.trim();
        userRepositoryPort.deactivate(targetUserId, adminUserId, normalizedReason);
    }
}

