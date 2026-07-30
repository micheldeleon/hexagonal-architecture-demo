package com.tutorneo.adapters.out.persistence.jpa.entities;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tournament_teams", schema = "public")
@IdClass(TournamentTeamKey.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TournamentTeamEntity {

    @Id
    @Column(name = "tournament_id")
    private Long tournamentId;

    @Id
    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
