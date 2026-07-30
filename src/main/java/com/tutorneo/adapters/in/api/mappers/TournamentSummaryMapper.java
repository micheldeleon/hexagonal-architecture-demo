package com.tutorneo.adapters.in.api.mappers;

import com.tutorneo.adapters.in.api.dto.TournamentSummaryResponse;
import com.tutorneo.core.domain.models.Tournament;

public class TournamentSummaryMapper {

    private TournamentSummaryMapper() {
    }

    public static TournamentSummaryResponse toResponse(Tournament tournament) {
        if (tournament == null) {
            return null;
        }
        return new TournamentSummaryResponse(
                tournament.getId(),
                tournament.getName(),
                tournament.getDiscipline() != null ? tournament.getDiscipline().getId() : null,
                tournament.getStartAt(),
                tournament.getEndAt(),
                tournament.getRegistrationDeadline(),
                tournament.isPrivateTournament(),
                tournament.getPrize(),
                tournament.getRegistrationCost(),
                tournament.getStatus());
    }
}
