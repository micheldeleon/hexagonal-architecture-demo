package com.tutorneo.core.ports.in;

import java.util.List;

import com.tutorneo.core.domain.models.RaceResult;

public interface GetRaceResultsPort {
    List<RaceResult> list(Long tournamentId);
}
