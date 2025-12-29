package com.example.demo.adapters.out.persistence.jpa.entities;

//Mapeo 1:1 a la tabla public.tournaments.

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.example.demo.adapters.out.persistence.jpa.entities.FormatEntity;

@Entity
@Table(name = "tournaments", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TournamentJpaEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "discipline_id", nullable = false)
  private DisciplineEntity discipline;

  @ManyToOne
  @JoinColumn(name = "format_id")
  private FormatEntity format;

  @Column(name = "organizer_id", nullable = false)
  private Long organizerId;

  @Column(nullable = false)
  private String name;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "start_at")
  private OffsetDateTime startAt;

  @Column(name = "end_at")
  private OffsetDateTime endAt;

  @Column(name = "registration_deadline")
  private OffsetDateTime registrationDeadline;

  @Column(name = "private_tournament", nullable = false)
  private boolean privateTournament;

  private String password;

  @Column(name = "min_participants_per_team", nullable = false)
  private Integer minParticipantsPerTeam;

  @Column(name = "max_participants_per_team", nullable = false)
  private Integer maxParticipantsPerTeam;

  @Column(name = "min_participants_tournament")
  private Integer minParticipantsTournament;

  @Column(name = "max_participants_tournament")
  private Integer maxParticipantsTournament;

  private String prize;

  @Column(name = "registration_cost", nullable = false, precision = 10, scale = 2)
  private BigDecimal registrationCost;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "teams_inscribed", nullable = false)
  private Integer teamsInscribed;

  @Column(name = "is_double_round")
  private Boolean isDoubleRound;

  @Column(name = "detalles", columnDefinition = "TEXT")
  private String detalles;
}
