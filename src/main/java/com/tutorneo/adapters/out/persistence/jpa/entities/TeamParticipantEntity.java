package com.tutorneo.adapters.out.persistence.jpa.entities;

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
@Table(name = "team_participants", schema = "public")
@IdClass(TeamParticipantKey.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamParticipantEntity {

    @Id
    @Column(name = "team_id")
    private Long teamId;

    @Id
    @Column(name = "participant_id")
    private Long participantId;
}
