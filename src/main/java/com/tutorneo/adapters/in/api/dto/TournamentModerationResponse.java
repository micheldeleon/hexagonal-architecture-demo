package com.tutorneo.adapters.in.api.dto;

import java.util.Date;

import com.tutorneo.core.domain.models.TournamentModerationStatus;

public record TournamentModerationResponse(
        Long tournamentId,
        TournamentModerationStatus moderationStatus,
        Date moderatedAt,
        Long moderatedByAdminId,
        String reason) {
}

