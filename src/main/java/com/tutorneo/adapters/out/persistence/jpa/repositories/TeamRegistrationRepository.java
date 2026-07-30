package com.tutorneo.adapters.out.persistence.jpa.repositories;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tutorneo.adapters.in.api.dto.ParticipantRequest;
import com.tutorneo.adapters.out.persistence.jpa.entities.ParticipantEntity;
import com.tutorneo.adapters.out.persistence.jpa.entities.TeamEntity;
import com.tutorneo.adapters.out.persistence.jpa.entities.TeamParticipantEntity;
import com.tutorneo.adapters.out.persistence.jpa.entities.TournamentTeamEntity;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.ParticipantRepositoryJpa;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.TeamParticipantRepositoryJpa;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.TeamRepositoryJpa;
import com.tutorneo.adapters.out.persistence.jpa.interfaces.TournamentTeamRepositoryJpa;
import com.tutorneo.core.ports.out.TeamRegistrationPort;

@Component
public class TeamRegistrationRepository implements TeamRegistrationPort {

    private final TeamRepositoryJpa teamRepositoryJpa;
    private final ParticipantRepositoryJpa participantRepositoryJpa;
    private final TeamParticipantRepositoryJpa teamParticipantRepositoryJpa;
    private final TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa;

    public TeamRegistrationRepository(TeamRepositoryJpa teamRepositoryJpa,
            ParticipantRepositoryJpa participantRepositoryJpa,
            TeamParticipantRepositoryJpa teamParticipantRepositoryJpa,
            TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa) {
        this.teamRepositoryJpa = teamRepositoryJpa;
        this.participantRepositoryJpa = participantRepositoryJpa;
        this.teamParticipantRepositoryJpa = teamParticipantRepositoryJpa;
        this.tournamentTeamRepositoryJpa = tournamentTeamRepositoryJpa;
    }

    @Override
    @Transactional
    public void registerTeam(Long tournamentId, Long creatorUserId, String teamName, Long disciplineId,
            List<ParticipantRequest> participants) {

        // Equipos ya inscritos en el torneo (para validar duplicados de nationalId)
        List<Long> teamIdsInTournament = tournamentTeamRepositoryJpa.findByTournamentId(tournamentId)
                .stream()
                .map(tt -> tt.getTeamId())
                .collect(Collectors.toList());

        // Validar duplicados en la misma solicitud
        Set<String> nationalIdsInRequest = participants.stream()
                .map(ParticipantRequest::nationalId)
                .collect(Collectors.toSet());
        if (nationalIdsInRequest.size() != participants.size()) {
            throw new IllegalArgumentException("Hay participantes con nationalId repetido en el equipo");
        }

        TeamEntity team = new TeamEntity();
        team.setName(teamName);
        team.setCreatorId(creatorUserId);
        team.setDisciplineId(disciplineId);
        team.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        team = teamRepositoryJpa.save(team);

        List<TeamParticipantEntity> teamParticipantEntities = new ArrayList<>();
        for (ParticipantRequest participant : participants) {
            Optional<ParticipantEntity> existing = participantRepositoryJpa.findByNationalId(participant.nationalId());

            if (existing.isPresent() && !teamIdsInTournament.isEmpty()) {
                boolean alreadyInTournament = teamParticipantRepositoryJpa
                        .existsByParticipantIdAndTeamIdIn(existing.get().getId(), teamIdsInTournament);
                if (alreadyInTournament) {
                    throw new IllegalStateException("El participante con nationalId " + participant.nationalId()
                            + " ya está inscrito en este torneo");
                }
            }

            ParticipantEntity pEntity = existing.orElseGet(() -> {
                ParticipantEntity p = new ParticipantEntity();
                p.setFullName(participant.fullName());
                p.setNationalId(participant.nationalId());
                return p;
            });

            pEntity = participantRepositoryJpa.save(pEntity);

            TeamParticipantEntity tp = new TeamParticipantEntity(team.getId(), pEntity.getId());
            teamParticipantEntities.add(tp);
        }
        teamParticipantRepositoryJpa.saveAll(teamParticipantEntities);

        TournamentTeamEntity tt = new TournamentTeamEntity();
        tt.setTournamentId(tournamentId);
        tt.setTeamId(team.getId());
        tt.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        tournamentTeamRepositoryJpa.save(tt);
    }
}
