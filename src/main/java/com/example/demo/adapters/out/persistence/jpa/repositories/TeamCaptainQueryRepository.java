package com.example.demo.adapters.out.persistence.jpa.repositories;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.demo.adapters.out.persistence.jpa.entities.TeamEntity;
import com.example.demo.adapters.out.persistence.jpa.interfaces.TeamRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.interfaces.TournamentTeamRepositoryJpa;
import com.example.demo.core.ports.out.TeamCaptainQueryPort;

@Component
public class TeamCaptainQueryRepository implements TeamCaptainQueryPort {

    private final TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa;
    private final TeamRepositoryJpa teamRepositoryJpa;

    public TeamCaptainQueryRepository(
            TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa,
            TeamRepositoryJpa teamRepositoryJpa) {
        this.tournamentTeamRepositoryJpa = tournamentTeamRepositoryJpa;
        this.teamRepositoryJpa = teamRepositoryJpa;
    }

    @Override
    public List<Long> findCaptainTeamIdsInTournament(Long tournamentId, Long captainUserId) {
        if (tournamentId == null || captainUserId == null) {
            return List.of();
        }

        List<Long> teamIds = tournamentTeamRepositoryJpa.findByTournamentId(tournamentId)
                .stream()
                .map(tt -> tt.getTeamId())
                .toList();
        if (teamIds.isEmpty()) {
            return List.of();
        }

        Map<Long, TeamEntity> teamsById = teamRepositoryJpa.findAllById(teamIds)
                .stream()
                .collect(Collectors.toMap(TeamEntity::getId, t -> t));

        return teamIds.stream()
                .filter(teamId -> {
                    TeamEntity team = teamsById.get(teamId);
                    return team != null && captainUserId.equals(team.getCreatorId());
                })
                .toList();
    }
}

