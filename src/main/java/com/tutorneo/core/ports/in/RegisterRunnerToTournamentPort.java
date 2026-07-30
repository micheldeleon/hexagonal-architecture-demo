package com.tutorneo.core.ports.in;

public interface RegisterRunnerToTournamentPort {
    void register(Long tournamentId, String userEmail, com.tutorneo.adapters.in.api.dto.RunnerRegistrationRequest request);
}
