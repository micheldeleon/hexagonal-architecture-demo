package com.example.demo.core.ports.out;

public interface TeamRemovalPort {
    void removeTeamFromTournament(Long tournamentId, Long teamId);
}
