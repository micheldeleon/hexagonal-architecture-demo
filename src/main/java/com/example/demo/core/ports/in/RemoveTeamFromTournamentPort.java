package com.example.demo.core.ports.in;

public interface RemoveTeamFromTournamentPort {
    void removeTeam(Long tournamentId, Long organizerId, Long teamId, String comment);
}
