package com.tutorneo.adapters.out.persistence.jpa.entities;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamParticipantKey implements Serializable {
    private Long teamId;
    private Long participantId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamParticipantKey that = (TeamParticipantKey) o;
        return Objects.equals(teamId, that.teamId) && Objects.equals(participantId, that.participantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, participantId);
    }
}
