package com.example.demo.adapters.in.api.dto;

import java.util.Date;
import java.util.List;

import com.example.demo.core.domain.models.Discipline;
import com.example.demo.core.domain.models.Format;
import com.example.demo.core.domain.models.Team;

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
    String detalles,
    List<Team> teams
) {}
