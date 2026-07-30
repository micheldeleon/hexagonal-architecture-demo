package com.tutorneo.core.domain.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentMatch {
    private Long id;
    private Long tournamentId;
    private int round;
    private int matchNumber;
    private Long homeTeamId;
    private Long awayTeamId;
    private Date scheduledAt;
    private String status; // PENDING, IN_PROGRESS, FINISHED
    private Integer scoreHome;
    private Integer scoreAway;
    private Long winnerTeamId;
    private Date createdAt;
    private Date updatedAt;
}
