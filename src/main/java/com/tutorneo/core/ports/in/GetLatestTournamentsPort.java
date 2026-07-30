package com.tutorneo.core.ports.in;

import java.util.List;

import com.tutorneo.core.domain.models.Tournament;

public interface GetLatestTournamentsPort {
    List<Tournament> getLatest3();
}
