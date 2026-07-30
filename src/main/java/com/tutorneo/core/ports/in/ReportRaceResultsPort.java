package com.tutorneo.core.ports.in;

import java.util.List;

import com.tutorneo.core.domain.models.RaceResult;

public interface ReportRaceResultsPort {
    void report(Long tournamentId, String organizerEmail, List<RaceResult> results);
}
