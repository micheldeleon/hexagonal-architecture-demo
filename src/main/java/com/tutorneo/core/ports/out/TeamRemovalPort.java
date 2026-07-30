package com.tutorneo.core.ports.out;

public interface TeamRemovalPort {
    void removeTeamFromTournament(Long tournamentId, Long teamId);
}
