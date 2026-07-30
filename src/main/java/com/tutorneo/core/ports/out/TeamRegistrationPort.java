package com.tutorneo.core.ports.out;

import java.util.List;

import com.tutorneo.adapters.in.api.dto.ParticipantRequest;

public interface TeamRegistrationPort {
    void registerTeam(Long tournamentId, Long creatorUserId, String teamName, Long disciplineId,
            List<ParticipantRequest> participants);
}
