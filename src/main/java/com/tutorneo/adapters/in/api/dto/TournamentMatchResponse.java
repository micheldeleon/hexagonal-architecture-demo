package com.tutorneo.adapters.in.api.dto;

import java.util.Date;

public record TournamentMatchResponse(
        Long id,
        int round,
        int matchNumber,
        TeamSummaryDto homeTeam,
        TeamSummaryDto awayTeam,
        TeamSummaryDto winnerTeam,
        String status,
        Integer scoreHome,
        Integer scoreAway,
        Date scheduledAt) {
}
