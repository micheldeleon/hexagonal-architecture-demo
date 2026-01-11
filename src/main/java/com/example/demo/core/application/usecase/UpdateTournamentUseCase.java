package com.example.demo.core.application.usecase;

import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.ports.in.UpdateTournamentPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;

public class UpdateTournamentUseCase implements UpdateTournamentPort {

    private final TournamentRepositoryPort tournamentRepository;

    public UpdateTournamentUseCase(TournamentRepositoryPort tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Tournament update(Tournament tournament) {
        if (tournament == null || tournament.getId() == null) {
            throw new IllegalArgumentException("Tournament must not be null and must have an ID");
        }
        return tournamentRepository.update(tournament);
    }
}
