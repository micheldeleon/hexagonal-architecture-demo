package com.tutorneo.core.ports.in;

import com.tutorneo.core.domain.models.Tournament;

public interface UpdateTournamentPort {
    Tournament update(Tournament tournament);
}
