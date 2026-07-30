package com.tutorneo.core.ports.in;

public interface GenerateLeagueFixturePort {
    void generate(Long tournamentId, boolean doubleRound);
}
