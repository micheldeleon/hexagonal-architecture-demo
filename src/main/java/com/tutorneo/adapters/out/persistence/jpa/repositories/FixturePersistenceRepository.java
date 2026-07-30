package com.tutorneo.adapters.out.persistence.jpa.repositories;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tutorneo.adapters.out.persistence.jpa.entities.TournamentMatchEntity;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.TournamentMatchRepositoryJpa;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.TournamentTeamRepositoryJpa;
import com.tutorneo.adapters.out.persistence.jpa.mappers.TournamentMatchMapper;
import com.tutorneo.core.domain.models.TournamentMatch;
import com.tutorneo.core.ports.out.FixturePersistencePort;

@Component
public class FixturePersistenceRepository implements FixturePersistencePort {

    private final TournamentMatchRepositoryJpa tournamentMatchRepositoryJpa;
    private final TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa;

    public FixturePersistenceRepository(TournamentMatchRepositoryJpa tournamentMatchRepositoryJpa,
            TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa) {
        this.tournamentMatchRepositoryJpa = tournamentMatchRepositoryJpa;
        this.tournamentTeamRepositoryJpa = tournamentTeamRepositoryJpa;
    }

    @Override
    public boolean hasMatches(Long tournamentId) {
        return tournamentMatchRepositoryJpa.existsByTournamentId(tournamentId);
    }

    @Override
    public List<Long> getTeamIdsForTournament(Long tournamentId) {
        return tournamentTeamRepositoryJpa.findByTournamentId(tournamentId)
                .stream()
                .map(tt -> tt.getTeamId())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveMatches(List<TournamentMatch> matches) {
        List<TournamentMatchEntity> entities = matches.stream()
                .map(TournamentMatchMapper::toEntity)
                .collect(Collectors.toList());
        tournamentMatchRepositoryJpa.saveAll(entities);
    }

    @Override
    public List<TournamentMatch> findByTournament(Long tournamentId) {
        return tournamentMatchRepositoryJpa.findByTournamentIdOrderByRoundAscMatchNumberAsc(tournamentId)
                .stream()
                .map(TournamentMatchMapper::toDomain)
                .toList();
    }

    @Override
    public TournamentMatch getMatch(Long matchId) {
        return tournamentMatchRepositoryJpa.findById(matchId)
                .map(TournamentMatchMapper::toDomain)
                .orElse(null);
    }

    @Override
    public void saveMatch(TournamentMatch match) {
        TournamentMatchEntity entity = TournamentMatchMapper.toEntity(match);
        tournamentMatchRepositoryJpa.save(entity);
    }

    @Override
    public TournamentMatch findByTournamentRoundAndNumber(Long tournamentId, int round, int matchNumber) {
        TournamentMatchEntity entity = tournamentMatchRepositoryJpa
                .findByTournamentIdAndRoundAndMatchNumber(tournamentId, round, matchNumber);
        return entity != null ? TournamentMatchMapper.toDomain(entity) : null;
    }

    @Override
    public int countByTournamentAndRound(Long tournamentId, int round) {
        return (int) tournamentMatchRepositoryJpa.countByTournamentIdAndRound(tournamentId, round);
    }
}
