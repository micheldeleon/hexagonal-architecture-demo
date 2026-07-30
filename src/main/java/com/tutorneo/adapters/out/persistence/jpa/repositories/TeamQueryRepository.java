package com.tutorneo.adapters.out.persistence.jpa.repositories;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tutorneo.adapters.out.persistence.jpa.entities.TeamEntity;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.TeamRepositoryJpa;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.TournamentTeamRepositoryJpa;
import com.tutorneo.core.ports.out.TeamQueryPort;

@Component
public class TeamQueryRepository implements TeamQueryPort {

    private final TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa;
    private final TeamRepositoryJpa teamRepositoryJpa;

    public TeamQueryRepository(TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa,
            TeamRepositoryJpa teamRepositoryJpa) {
        this.tournamentTeamRepositoryJpa = tournamentTeamRepositoryJpa;
        this.teamRepositoryJpa = teamRepositoryJpa;
    }

    @Override
    public List<TeamSummary> findTeamsByTournament(Long tournamentId) {
        List<Long> teamIds = tournamentTeamRepositoryJpa.findByTournamentId(tournamentId)
                .stream()
                .map(tt -> tt.getTeamId())
                .toList();
        if (teamIds.isEmpty()) {
            return List.of();
        }

        Map<Long, TeamEntity> teams = teamRepositoryJpa.findAllById(teamIds)
                .stream()
                .collect(Collectors.toMap(t -> t.getId(), t -> t));

        return teamIds.stream()
                .map(id -> {
                    TeamEntity t = teams.get(id);
                    String name = t != null ? t.getName() : null;
                    return new TeamSummary(id, name);
                })
                .toList();
    }
}
