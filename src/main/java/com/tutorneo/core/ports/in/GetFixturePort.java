package com.tutorneo.core.ports.in;

import java.util.List;

import com.tutorneo.core.domain.models.TournamentMatch;

public interface GetFixturePort {
    List<TournamentMatch> getFixture(Long tournamentId);
}
