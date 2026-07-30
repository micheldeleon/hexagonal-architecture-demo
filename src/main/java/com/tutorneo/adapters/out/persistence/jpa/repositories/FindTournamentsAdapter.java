package com.tutorneo.adapters.out.persistence.jpa.repositories;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.tutorneo.adapters.out.persistence.jpa.entities.TournamentJpaEntity;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.TournamentRepositoryJpa;
import com.tutorneo.adapters.out.persistence.jpa.mappers.TournamentMapper;
import com.tutorneo.core.domain.models.Tournament;
import com.tutorneo.core.domain.models.TournamentStatus;
import com.tutorneo.core.ports.out.FindTournamentsByStatusPort;
import com.tutorneo.core.ports.out.FindTournamentsPort;

@Component
public class FindTournamentsAdapter implements FindTournamentsPort, FindTournamentsByStatusPort {

    private final TournamentRepositoryJpa tournamentRepositoryJpa;

    public FindTournamentsAdapter(TournamentRepositoryJpa tournamentRepositoryJpa) {
        this.tournamentRepositoryJpa = tournamentRepositoryJpa;
    }

    @Override
    public List<Tournament> findByStatus(TournamentStatus status) {
        String statusValue = status != null ? status.name() : null;
        List<TournamentJpaEntity> entities = tournamentRepositoryJpa.findByStatus(statusValue);
        return entities.stream()
                .map(TournamentMapper::mapToDomain)
                .toList();
    }

    @Override
    public List<Tournament> findByFilters(
            TournamentStatus status,
            Long disciplineId,
            String nameContains,
            Date startFrom,
            Date startTo,
            Boolean withPrize,
            Boolean withRegistrationCost) {

        OffsetDateTime startFromOdt = toOdt(startFrom);
        OffsetDateTime startToOdt = toOdt(startTo);
        String statusValue = status != null ? status.name() : null;

        String namePattern = (nameContains == null || nameContains.isBlank())
                ? ""
                : "%" + nameContains.toLowerCase() + "%";

        List<TournamentJpaEntity> entities = tournamentRepositoryJpa.findByFilters(
                statusValue,
                disciplineId,
                namePattern,
                startFromOdt,
                startToOdt,
                withPrize,
                withRegistrationCost);

        return entities.stream()
                .map(TournamentMapper::mapToDomain)
                .toList();
    }

    private OffsetDateTime toOdt(Date date) {
        return date == null ? null : date.toInstant().atOffset(ZoneOffset.UTC);
    }
}
