package com.example.demo.core.ports.in;

public interface LeaveTournamentPort {

    LeaveTournamentResult leave(Long tournamentId, String userEmail, Long teamId, String reason);

    enum LeaveMode {
        INDIVIDUAL,
        TEAM
    }

    record LeaveTournamentResult(
            Long tournamentId,
            LeaveMode mode,
            Long teamId) {
    }
}

