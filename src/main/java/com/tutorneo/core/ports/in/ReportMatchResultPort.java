package com.tutorneo.core.ports.in;

public interface ReportMatchResultPort {
    void reportResult(Long tournamentId, Long matchId, Integer scoreHome, Integer scoreAway, Long winnerTeamId);
}
