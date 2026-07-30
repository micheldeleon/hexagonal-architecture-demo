package com.tutorneo.adapters.out.persistence.jpa.repositories;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tutorneo.adapters.out.persistence.jpa.entities.TournamentTeamEntity;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.TeamParticipantRepositoryJpa;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.TeamRepositoryJpa;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.TournamentTeamRepositoryJpa;
import com.tutorneo.core.ports.out.TournamentCleanupPort;

@Component
public class TournamentCleanupRepository implements TournamentCleanupPort {

    private final TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa;
    private final TeamParticipantRepositoryJpa teamParticipantRepositoryJpa;
    private final TeamRepositoryJpa teamRepositoryJpa;

    public TournamentCleanupRepository(
            TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa,
            TeamParticipantRepositoryJpa teamParticipantRepositoryJpa,
            TeamRepositoryJpa teamRepositoryJpa) {
        this.tournamentTeamRepositoryJpa = tournamentTeamRepositoryJpa;
        this.teamParticipantRepositoryJpa = teamParticipantRepositoryJpa;
        this.teamRepositoryJpa = teamRepositoryJpa;
    }

    @Override
    @Transactional
    public void removeTeamsAndRegistrations(Long tournamentId) {
        List<TournamentTeamEntity> links = tournamentTeamRepositoryJpa.findByTournamentId(tournamentId);
        if (links.isEmpty()) {
            return;
        }

        List<Long> teamIds = links.stream()
                .map(TournamentTeamEntity::getTeamId)
                .toList();

        teamParticipantRepositoryJpa.deleteByTeamIdIn(teamIds);
        tournamentTeamRepositoryJpa.deleteAll(links);
        teamRepositoryJpa.deleteAllById(teamIds);
    }
}
