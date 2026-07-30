package com.tutorneo.adapters.out.persistence.jpa.repositories;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tutorneo.adapters.out.persistence.jpa.entities.TournamentTeamKey;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.TournamentTeamRepositoryJpa;
import com.tutorneo.core.ports.out.TeamRemovalPort;

@Component
public class TeamRemovalRepository implements TeamRemovalPort {

    private final TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa;

    public TeamRemovalRepository(TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa) {
        this.tournamentTeamRepositoryJpa = tournamentTeamRepositoryJpa;
    }

    @Override
    @Transactional
    public void removeTeamFromTournament(Long tournamentId, Long teamId) {
        TournamentTeamKey key = new TournamentTeamKey(tournamentId, teamId);
        
        if (!tournamentTeamRepositoryJpa.existsById(key)) {
            throw new IllegalArgumentException("La relación torneo-equipo no existe");
        }
        
        tournamentTeamRepositoryJpa.deleteById(key);
    }
}
