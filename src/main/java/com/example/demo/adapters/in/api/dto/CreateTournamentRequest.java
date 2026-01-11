
//(lo que llega en el POST para crear un torneo)

package com.example.demo.adapters.in.api.dto;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateTournamentRequest(
    @NotNull Long disciplineId,
    @NotNull Long formatId,
    @NotBlank String name,
    // @NotNull Format format,
    @NotNull Date startAt,
    @NotNull Date endAt,
    Date registrationDeadline,
    boolean privateTournament,
    String password,
    @Positive int minParticipantsPerTeam,
    @Positive int maxParticipantsPerTeam,
    @PositiveOrZero int minParticipantsPerTournament,
    @PositiveOrZero int maxParticipantsPerTournament,
    String prize,
    @PositiveOrZero double registrationCost,
    Boolean isDoubleRound, // Para formato liga: indica si es a doble ronda (ida y vuelta)
    String detalles, // Información detallada del torneo
    String imageUrl // URL de la imagen del torneo
) {}
