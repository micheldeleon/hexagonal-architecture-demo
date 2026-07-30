package com.tutorneo.adapters.out.persistence.jpa.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutorneo.adapters.out.persistence.jpa.entities.RaceResultEntity;

@Repository
public interface RaceResultRepositoryJpa extends JpaRepository<RaceResultEntity, Long> {

    void deleteByTournamentId(Long tournamentId);

    List<RaceResultEntity> findByTournamentIdOrderByPositionAsc(Long tournamentId);
}
