package com.tutorneo.core.application.usecase;

import com.tutorneo.core.domain.models.User;
import com.tutorneo.core.ports.in.RestoreUserPort;
import com.tutorneo.core.ports.out.UserRepositoryPort;

public class RestoreUserUseCase implements RestoreUserPort {

    private final UserRepositoryPort userRepositoryPort;

    public RestoreUserUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void restore(Long targetUserId, Long adminUserId) {
        if (targetUserId == null) {
            throw new IllegalArgumentException("Target user id is required");
        }
        if (adminUserId == null) {
            throw new IllegalArgumentException("Admin user id is required");
        }

        User target = userRepositoryPort.findByIdIncludingDeleted(targetUserId);
        if (!target.isDeleted()) {
            return;
        }

        userRepositoryPort.restore(targetUserId, adminUserId);
    }
}

