package com.example.demo.adapters.out.persistence.jpa.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.adapters.out.persistence.jpa.entities.TournamentParticipantEntity;

@Repository
public interface TournamentParticipantRepositoryJpa extends JpaRepository<TournamentParticipantEntity, Long> {

    boolean existsByTournamentIdAndUserId(Long tournamentId, Long userId);
}

