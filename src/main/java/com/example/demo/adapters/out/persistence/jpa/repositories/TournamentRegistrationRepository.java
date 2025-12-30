package com.example.demo.adapters.out.persistence.jpa.repositories;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

import com.example.demo.adapters.out.persistence.jpa.entities.TournamentParticipantEntity;
import com.example.demo.adapters.out.persistence.jpa.interfaces.TournamentParticipantRepositoryJpa;
import com.example.demo.core.ports.out.TournamentRegistrationPort;

@Component
public class TournamentRegistrationRepository implements TournamentRegistrationPort {

    private final TournamentParticipantRepositoryJpa tournamentParticipantRepositoryJpa;

    public TournamentRegistrationRepository(TournamentParticipantRepositoryJpa tournamentParticipantRepositoryJpa) {
        this.tournamentParticipantRepositoryJpa = tournamentParticipantRepositoryJpa;
    }

    @Override
    public void register(Long tournamentId, Long userId) {
        TournamentParticipantEntity entity = new TournamentParticipantEntity();
        entity.setTournamentId(tournamentId);
        entity.setUserId(userId);
        entity.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        tournamentParticipantRepositoryJpa.save(entity);
    }

    @Override
    public boolean exists(Long tournamentId, Long userId) {
        return tournamentParticipantRepositoryJpa.existsByTournamentIdAndUserId(tournamentId, userId);
    }
}

