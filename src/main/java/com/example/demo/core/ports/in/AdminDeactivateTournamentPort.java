package com.example.demo.core.ports.in;

import java.util.Date;

import com.example.demo.core.domain.models.TournamentModerationStatus;

public interface AdminDeactivateTournamentPort {

    DeactivateResult deactivate(Long tournamentId, String adminEmail, String reason);

    record DeactivateResult(
            Long tournamentId,
            TournamentModerationStatus moderationStatus,
            Date moderatedAt,
            Long moderatedByAdminId,
            String reason) {
    }
}

