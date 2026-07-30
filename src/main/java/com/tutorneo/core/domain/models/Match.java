package com.tutorneo.core.domain.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match { // Duelo
    private Long id;
    private int homeTeamScore; // resultadoEquipoLocal
    private int awayTeamScore; // resultadoEquipoVisita
    private Round round;
    private LocalDateTime scheduledAt;
    private String status;
    private Team homeTeam;
    private Team awayTeam;
}
