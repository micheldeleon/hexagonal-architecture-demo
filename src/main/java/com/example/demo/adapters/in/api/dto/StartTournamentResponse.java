package com.example.demo.adapters.in.api.dto;

import java.util.Date;

import com.example.demo.core.domain.models.TournamentStatus;

public record StartTournamentResponse(
        Long tournamentId,
        TournamentStatus status,
        Date startedAt,
        String startedBy,
        int participantsCount,
        String message) {
}
