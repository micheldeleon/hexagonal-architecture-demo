package com.tutorneo.core.ports.in;

import java.util.List;

import com.tutorneo.core.ports.in.models.LeagueStanding;

public interface GetLeagueStandingsPort {
    List<LeagueStanding> list(Long tournamentId);
}
