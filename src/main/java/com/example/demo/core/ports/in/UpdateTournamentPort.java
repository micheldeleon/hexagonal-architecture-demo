package com.example.demo.core.ports.in;

import com.example.demo.core.domain.models.Tournament;

public interface UpdateTournamentPort {
    Tournament update(Tournament tournament);
}
