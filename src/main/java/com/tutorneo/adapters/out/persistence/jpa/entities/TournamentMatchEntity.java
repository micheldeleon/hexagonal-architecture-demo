package com.tutorneo.adapters.out.persistence.jpa.entities;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tournament_matches", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TournamentMatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tournament_id", nullable = false)
    private Long tournamentId;

    @Column(nullable = false)
    private int round;

    @Column(name = "match_number", nullable = false)
    private int matchNumber;

    @Column(name = "home_team_id")
    private Long homeTeamId;

    @Column(name = "away_team_id")
    private Long awayTeamId;

    @Column(name = "scheduled_at")
    private OffsetDateTime scheduledAt;

    @Column(nullable = false)
    private String status;

    @Column(name = "score_home")
    private Integer scoreHome;

    @Column(name = "score_away")
    private Integer scoreAway;

    @Column(name = "winner_team_id")
    private Long winnerTeamId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
