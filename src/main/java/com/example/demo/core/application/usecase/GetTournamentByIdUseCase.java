package com.example.demo.core.application.usecase;


import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentModerationStatus;
import com.example.demo.core.ports.in.GetTournamentByIdPort;

import com.example.demo.core.ports.out.TournamentRepositoryPort;

public class GetTournamentByIdUseCase implements GetTournamentByIdPort{
    private final TournamentRepositoryPort tournamentRepository;

    public GetTournamentByIdUseCase(TournamentRepositoryPort tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Tournament getTournamentById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id es requerido");
        }
        Tournament t = tournamentRepository.findByIdWithTeams(id);
        if (t == null || t.getModerationStatus() == TournamentModerationStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }
        return t;
    }

    
}
