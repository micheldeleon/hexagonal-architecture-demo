package com.example.demo.adapters.in.api.mappers;

import java.util.ArrayList;

import com.example.demo.adapters.in.api.dto.CreateTournamentRequest;
import com.example.demo.adapters.in.api.dto.TournamentResponse;
import com.example.demo.core.domain.models.Discipline;
import com.example.demo.core.domain.models.SimpleFormat;
import com.example.demo.core.domain.models.Tournament;

// Centraliza la conversión entre el JSON de la API y tu modelo de dominio.
public class TournamentMapper {

    public static Tournament toDomain(CreateTournamentRequest r) {

        Discipline d = new Discipline(r.disciplineId(), false, null, null);
        SimpleFormat f = new SimpleFormat(r.formatId(), null, false);

        return new Tournament(
                null, // id
                new ArrayList<>(), // teams
                // r.format(), // format
                0, // teamsInscribed
                d, // discipline
                f, // format
                r.name(), // name
                null, // createdAt (lo seteás después)
                r.endAt(), // endAt
                r.startAt(), // startAt
                r.privateTournament(), // privateTournament
                r.password(), // password
                r.minParticipantsPerTeam(),// minParticipantsPerTeam
                r.maxParticipantsPerTeam(),// maxParticipantsPerTeam
                r.registrationDeadline(), // registrationDeadline
                r.prize(), // prize
                r.registrationCost(), // registrationCost
                null, // organizer (por ahora)
                r.minParticipantsPerTournament(), // minParticipantsPerTournament
                r.maxParticipantsPerTournament(), // maxParticipantsPerTournament
                null, // status
                r.isDoubleRound(), // isDoubleRound
                r.detalles() // detalles
        );
    }

    public static TournamentResponse toResponse(Tournament t) {
        return new TournamentResponse(
                t.getId(),
                t.getFormat(),
                t.getDiscipline(),
                t.getName(),
                t.getIsDoubleRound(),
                t.getCreatedAt(),
                t.getStartAt(),
                t.getEndAt(),
                t.getRegistrationDeadline(),
                t.isPrivateTournament(),
                t.getPrize(),
                t.getRegistrationCost(),
                t.getMinParticipantsPerTeam(),
                t.getMaxParticipantsPerTeam(),
                t.getMinParticipantsPerTournament(),
                t.getMaxParticipantsPerTournament(),
                t.getTeamsInscribed(),
                t.getOrganizer().getId(),
                t.getStatus().toString(),
                t.getPassword(),
                t.getDetalles(),
                t.getTeams()
        );
    }
}
