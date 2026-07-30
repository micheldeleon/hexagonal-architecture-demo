package com.tutorneo.core.ports.in;

public interface ReportLeagueMatchResultPort {
    void reportResult(Long tournamentId, Long matchId, Integer scoreHome, Integer scoreAway);
}
