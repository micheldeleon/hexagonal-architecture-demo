package com.example.demo.core.ports.in;

import java.util.List;

import com.example.demo.core.domain.models.Tournament;

public interface GetLatestTournamentsPort {
    List<Tournament> getLatest3();
}
