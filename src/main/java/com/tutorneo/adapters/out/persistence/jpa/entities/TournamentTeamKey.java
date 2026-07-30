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
public class TournamentTeamKey implements Serializable {
    private Long tournamentId;
    private Long teamId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TournamentTeamKey that = (TournamentTeamKey) o;
        return Objects.equals(tournamentId, that.tournamentId) && Objects.equals(teamId, that.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tournamentId, teamId);
    }
}
