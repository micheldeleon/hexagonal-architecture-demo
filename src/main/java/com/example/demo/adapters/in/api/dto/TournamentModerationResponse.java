package com.example.demo.adapters.in.api.dto;

import java.util.Date;

import com.example.demo.core.domain.models.TournamentModerationStatus;

public record TournamentModerationResponse(
        Long tournamentId,
        TournamentModerationStatus moderationStatus,
        Date moderatedAt,
        Long moderatedByAdminId,
        String reason) {
}

