package com.example.demo.core.application.usecase;

import java.util.List;

import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentModerationStatus;

import com.example.demo.core.ports.in.GetAllTournamentsPort;

import com.example.demo.core.ports.out.TournamentRepositoryPort;

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
