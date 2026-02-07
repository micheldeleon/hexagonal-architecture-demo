package com.example.demo.core.application.usecase;

import java.util.List;

import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentModerationStatus;
import com.example.demo.core.ports.in.GetTournamentPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;

public class GetTournamentUseCase implements GetTournamentPort {

    private final TournamentRepositoryPort tournamentRepositoryPort;

    public GetTournamentUseCase(TournamentRepositoryPort tournamentRepositoryPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
    }

    @Override
    public List<Tournament> getSubscribedTournaments(String nationalId) {
        // Usar findAllWithTeams porque necesitamos los equipos para isParticipantByNationalId()
        List<Tournament> tournaments = tournamentRepositoryPort.findAllWithTeams();
        if (tournaments == null) {
            throw new IllegalArgumentException("No hay torneos");
        }
        return tournaments
                .stream()
                .filter(t -> t != null && t.getModerationStatus() != TournamentModerationStatus.DEACTIVATED)
                .filter(t -> t.isParticipantByNationalId(nationalId))
                .toList();
    }
}
