package com.tutorneo.adapters.out.persistence.jpa.repositories;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tutorneo.adapters.out.persistence.jpa.entities.TeamEntity;
import com.tutorneo.adapters.out.persistence.jpa.entities.RaceResultEntity;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.RaceResultRepositoryJpa;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.TeamRepositoryJpa;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.TournamentTeamRepositoryJpa;
import com.tutorneo.adapters.out.persistence.jpa.mappers.RaceResultMapper;
import com.tutorneo.core.domain.models.RaceResult;
import com.tutorneo.core.ports.out.RaceResultPersistencePort;

@Component
public class RaceResultPersistenceRepository implements RaceResultPersistencePort {

    private final RaceResultRepositoryJpa raceResultRepositoryJpa;
    private final TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa;
    private final TeamRepositoryJpa teamRepositoryJpa;

    public RaceResultPersistenceRepository(RaceResultRepositoryJpa raceResultRepositoryJpa,
            TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa,
            TeamRepositoryJpa teamRepositoryJpa) {
        this.raceResultRepositoryJpa = raceResultRepositoryJpa;
        this.tournamentTeamRepositoryJpa = tournamentTeamRepositoryJpa;
        this.teamRepositoryJpa = teamRepositoryJpa;
    }

    @Override
    public List<Long> getTeamIdsForTournament(Long tournamentId) {
        return tournamentTeamRepositoryJpa.findByTournamentId(tournamentId)
                .stream()
                .map(tt -> tt.getTeamId())
                .toList();
    }

    @Override
    @Transactional
    public void replaceResults(Long tournamentId, List<RaceResult> results) {
        raceResultRepositoryJpa.deleteByTournamentId(tournamentId);
        List<RaceResultEntity> entities = results.stream()
                .map(r -> {
                    RaceResultEntity e = RaceResultMapper.toEntity(r);
                    if (e.getCreatedAt() == null) {
                        e.setCreatedAt(nowUtc());
                    }
                    if (e.getUpdatedAt() == null) {
                        e.setUpdatedAt(nowUtc());
                    }
                    return e;
                })
                .collect(Collectors.toList());
        raceResultRepositoryJpa.saveAll(entities);
    }

    @Override
    public List<RaceResult> findByTournament(Long tournamentId) {
        List<RaceResult> results = raceResultRepositoryJpa.findByTournamentIdOrderByPositionAsc(tournamentId)
                .stream()
                .map(RaceResultMapper::toDomain)
                .toList();

        if (results.isEmpty()) {
            return results;
        }

        Map<Long, String> teamNames = teamRepositoryJpa.findAllById(
                results.stream().map(RaceResult::getTeamId).toList())
                .stream()
                .collect(Collectors.toMap(t -> t.getId(), t -> t.getName()));

        for (RaceResult r : results) {
            r.setTeamName(teamNames.get(r.getTeamId()));
        }
        return results;
    }

    @Override
    public List<RaceResult> findRegisteredTeams(Long tournamentId) {
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

        Date now = new Date();
        return teamIds.stream()
                .map(id -> {
                    TeamEntity t = teams.get(id);
                    RaceResult r = new RaceResult();
                    r.setTournamentId(tournamentId);
                    r.setTeamId(id);
                    r.setTeamName(t != null ? t.getName() : null);
                    r.setTimeMillis(0L);
                    r.setPosition(null);
                    r.setCreatedAt(now);
                    r.setUpdatedAt(now);
                    return r;
                })
                .toList();
    }

    private OffsetDateTime nowUtc() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}
