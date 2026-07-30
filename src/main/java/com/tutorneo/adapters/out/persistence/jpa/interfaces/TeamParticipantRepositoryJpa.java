package com.tutorneo.adapters.out.persistence.jpa.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutorneo.adapters.out.persistence.jpa.entities.TeamParticipantEntity;
import com.tutorneo.adapters.out.persistence.jpa.entities.TeamParticipantKey;

@Repository
public interface TeamParticipantRepositoryJpa extends JpaRepository<TeamParticipantEntity, TeamParticipantKey> {

    boolean existsByParticipantIdAndTeamIdIn(Long participantId, java.util.List<Long> teamIds);

    void deleteByTeamIdIn(java.util.List<Long> teamIds);
}
