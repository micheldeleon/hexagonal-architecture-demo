package com.example.demo.core.ports.in;

import java.util.Date;

import com.example.demo.core.domain.models.TournamentModerationStatus;

public interface AdminReactivateTournamentPort {

    ReactivateResult reactivate(Long tournamentId, String adminEmail, String reason);

    record ReactivateResult(
            Long tournamentId,
            TournamentModerationStatus moderationStatus,
            Date moderatedAt,
            Long moderatedByAdminId,
            String reason) {
    }
}

