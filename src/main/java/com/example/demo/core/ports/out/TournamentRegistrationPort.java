package com.example.demo.core.ports.out;

public interface TournamentRegistrationPort {
    void register(Long tournamentId, Long userId);

    boolean exists(Long tournamentId, Long userId);

    long unregister(Long tournamentId, Long userId);
}
