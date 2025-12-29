package com.example.demo.adapters.in.api.dto;

import java.util.Date;

import com.example.demo.core.domain.models.Discipline;
import com.example.demo.core.domain.models.Format;

public record TournamentResponse(
    Long id,
    Format format,
    Discipline discipline,
    String name,
    boolean isDoubleRound,
    Date createdAt,
    Date startAt,
    Date endAt,
    Date registrationDeadline,
    boolean privateTournament,
    String prize,
    double registrationCost,
    int minParticipantsPerTeam,
    int maxParticipantsPerTeam,
    int minParticipantsPerTournament,
    int maxParticipantsPerTournament,
    int teamsInscribed,
    Long organizerId,
    String status,
    String password,
    String detalles
) {}
