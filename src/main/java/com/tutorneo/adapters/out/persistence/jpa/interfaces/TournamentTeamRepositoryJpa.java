package com.tutorneo.adapters.out.persistence.jpa.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutorneo.adapters.out.persistence.jpa.entities.TournamentTeamEntity;
import com.tutorneo.adapters.out.persistence.jpa.entities.TournamentTeamKey;

@Repository
public interface TournamentTeamRepositoryJpa extends JpaRepository<TournamentTeamEntity, TournamentTeamKey> {

    java.util.List<TournamentTeamEntity> findByTournamentId(Long tournamentId);

    void deleteByTournamentId(Long tournamentId);
}
