package com.tutorneo.adapters.out.persistence.jpa.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutorneo.adapters.out.persistence.jpa.entities.TournamentMatchEntity;

@Repository
public interface TournamentMatchRepositoryJpa extends JpaRepository<TournamentMatchEntity, Long> {

    boolean existsByTournamentId(Long tournamentId);

    List<TournamentMatchEntity> findByTournamentIdOrderByRoundAscMatchNumberAsc(Long tournamentId);

    TournamentMatchEntity findByTournamentIdAndRoundAndMatchNumber(Long tournamentId, int round, int matchNumber);

    long countByTournamentIdAndRound(Long tournamentId, int round);
}
