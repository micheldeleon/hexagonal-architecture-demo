package com.tutorneo.core.application.usecase;

import java.util.List;

import com.tutorneo.core.domain.models.Tournament;
import com.tutorneo.core.domain.models.TournamentModerationStatus;

import com.tutorneo.core.ports.in.GetAllTournamentsPort;

import com.tutorneo.core.ports.out.TournamentRepositoryPort;

public class GetAllTournamentsUseCase implements GetAllTournamentsPort{
    private final TournamentRepositoryPort tournamentRepository;

    public GetAllTournamentsUseCase(TournamentRepositoryPort tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll().stream()
                .filter(t -> t != null && t.getModerationStatus() != TournamentModerationStatus.DEACTIVATED)
                .toList();
    }

    
}
