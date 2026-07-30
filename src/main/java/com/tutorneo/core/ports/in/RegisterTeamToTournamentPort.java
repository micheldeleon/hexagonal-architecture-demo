package com.tutorneo.core.ports.in;

import java.util.List;

import com.tutorneo.adapters.in.api.dto.ParticipantRequest;

public interface RegisterTeamToTournamentPort {
    void registerTeam(Long tournamentId, Long userId, String teamName, List<ParticipantRequest> participants);
}
