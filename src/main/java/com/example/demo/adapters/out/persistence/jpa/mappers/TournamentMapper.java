package com.example.demo.adapters.out.persistence.jpa.mappers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

import com.example.demo.adapters.out.persistence.jpa.entities.DisciplineEntity;
import com.example.demo.adapters.out.persistence.jpa.entities.TournamentJpaEntity;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.User;

public class TournamentMapper {
    public static TournamentJpaEntity mapToEntity(Tournament t, Long organizerId) {
        TournamentJpaEntity e = new TournamentJpaEntity();

        e.setId(t.getId());
        e.setName(t.getName());

        e.setDiscipline(t.getDiscipline() != null && t.getDiscipline().getId() != null
                ? new DisciplineEntity(t.getDiscipline().getId(), null, false)
                : null);

        if (t.getFormat() != null && t.getFormat().getId() != null) {
            var formatRef = new com.example.demo.adapters.out.persistence.jpa.entities.FormatEntity();
            formatRef.setId(t.getFormat().getId());
            e.setFormat(formatRef);
        } else {
            e.setFormat(null);
        }

        Long organizer = organizerId != null
                ? organizerId
                : (t.getOrganizer() != null ? t.getOrganizer().getId() : null);
        e.setOrganizerId(organizer);

        e.setCreatedAt(toOdt(t.getCreatedAt()));
        e.setStartAt(toOdt(t.getStartAt()));
        e.setEndAt(toOdt(t.getEndAt()));
        e.setRegistrationDeadline(toOdt(t.getRegistrationDeadline()));

        e.setPrivateTournament(t.isPrivateTournament());
        e.setPassword(t.getPassword());

        e.setMinParticipantsPerTeam(t.getMinParticipantsPerTeam());
        e.setMaxParticipantsPerTeam(t.getMaxParticipantsPerTeam());

        e.setMinParticipantsTournament(t.getMinParticipantsPerTournament());
        e.setMaxParticipantsTournament(t.getMaxParticipantsPerTournament());

        e.setPrize(t.getPrize());
        e.setRegistrationCost(BigDecimal.valueOf(t.getRegistrationCost()));
        e.setStatus(t.getStatus() != null ? t.getStatus().name() : null);
        e.setTeamsInscribed(t.getTeamsInscribed());
        e.setIsDoubleRound(t.getIsDoubleRound());
        e.setDetalles(t.getDetalles());
        e.setImageUrl(t.getImageUrl());

        return e;
    }

    public static Tournament mapToDomain(TournamentJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Tournament tournament = new Tournament();
        tournament.setId(entity.getId());
        tournament.setTeams(new ArrayList<>());
        tournament.setDiscipline(
                entity.getDiscipline() != null ? DisciplineMapper.toDomain(entity.getDiscipline()) : null);
        tournament.setFormat(entity.getFormat() != null ? FormatMapper.toDomain(entity.getFormat()) : null);
        tournament.setName(entity.getName());
        tournament.setCreatedAt(fromOdt(entity.getCreatedAt()));
        tournament.setEndAt(fromOdt(entity.getEndAt()));
        tournament.setStartAt(fromOdt(entity.getStartAt()));
        tournament.setPrivateTournament(entity.isPrivateTournament());
        tournament.setPassword(entity.getPassword());
        tournament.setMinParticipantsPerTeam(entity.getMinParticipantsPerTeam());
        tournament.setMaxParticipantsPerTeam(entity.getMaxParticipantsPerTeam());
        tournament.setRegistrationDeadline(fromOdt(entity.getRegistrationDeadline()));
        tournament.setPrize(entity.getPrize());
        tournament.setRegistrationCost(entity.getRegistrationCost() != null
                ? entity.getRegistrationCost().doubleValue()
                : 0D);
        if (entity.getOrganizerId() != null) {
            User organizer = new User();
            organizer.setId(entity.getOrganizerId());
            tournament.setOrganizer(organizer);
        } else {
            tournament.setOrganizer(null);
        }
        tournament.setMinParticipantsPerTournament(entity.getMinParticipantsTournament());
        tournament.setMaxParticipantsPerTournament(entity.getMaxParticipantsTournament());
        if (entity.getStatus() != null) {
            tournament.setStatus(TournamentStatus.valueOf(entity.getStatus()));
        }
        tournament.setTeamsInscribed(
                entity.getTeamsInscribed() != null ? entity.getTeamsInscribed() : 0);
        tournament.setIsDoubleRound(entity.getIsDoubleRound());
        tournament.setDetalles(entity.getDetalles());
        tournament.setImageUrl(entity.getImageUrl());

        return tournament;
    }

    private static OffsetDateTime toOdt(Date d) {
        return (d == null) ? null : d.toInstant().atOffset(ZoneOffset.UTC);
    }

    private static Date fromOdt(OffsetDateTime odt) {
        return (odt == null) ? null : Date.from(odt.toInstant());
    }
}

