package com.tutorneo.core.application.usecase;

import java.util.List;

import com.tutorneo.core.domain.models.Tournament;
import com.tutorneo.core.domain.models.TournamentModerationStatus;
import com.tutorneo.core.ports.in.GetLatestTournamentsPort;
import com.tutorneo.core.ports.out.TournamentRepositoryPort;

public class GetLatestTournamentsUseCase implements GetLatestTournamentsPort {

    private final TournamentRepositoryPort tournamentRepositoryPort;

    public GetLatestTournamentsUseCase(TournamentRepositoryPort tournamentRepositoryPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
    }

    @Override
    public List<Tournament> getLatest3() {
        return tournamentRepositoryPort.findLatest3().stream()
                .filter(t -> t != null && t.getModerationStatus() != TournamentModerationStatus.DEACTIVATED)
                .toList();
    }
}
