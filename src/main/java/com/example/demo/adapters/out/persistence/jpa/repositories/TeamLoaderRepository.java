package com.example.demo.adapters.out.persistence.jpa.repositories;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.demo.adapters.out.persistence.jpa.entities.ParticipantEntity;
import com.example.demo.adapters.out.persistence.jpa.entities.TeamEntity;
import com.example.demo.adapters.out.persistence.jpa.entities.TeamParticipantEntity;
import com.example.demo.adapters.out.persistence.jpa.entities.TournamentTeamEntity;
import com.example.demo.adapters.out.persistence.jpa.interfaces.ParticipantRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.interfaces.TeamParticipantRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.interfaces.TeamRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.interfaces.TournamentTeamRepositoryJpa;
import com.example.demo.core.domain.models.Participant;
import com.example.demo.core.domain.models.Team;
import com.example.demo.core.ports.out.TeamLoaderPort;

@Component
public class TeamLoaderRepository implements TeamLoaderPort {

    private final TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa;
    private final TeamRepositoryJpa teamRepositoryJpa;
    private final TeamParticipantRepositoryJpa teamParticipantRepositoryJpa;
    private final ParticipantRepositoryJpa participantRepositoryJpa;

    public TeamLoaderRepository(
            TournamentTeamRepositoryJpa tournamentTeamRepositoryJpa,
            TeamRepositoryJpa teamRepositoryJpa,
            TeamParticipantRepositoryJpa teamParticipantRepositoryJpa,
            ParticipantRepositoryJpa participantRepositoryJpa) {
        this.tournamentTeamRepositoryJpa = tournamentTeamRepositoryJpa;
        this.teamRepositoryJpa = teamRepositoryJpa;
        this.teamParticipantRepositoryJpa = teamParticipantRepositoryJpa;
        this.participantRepositoryJpa = participantRepositoryJpa;
    }

    @Override
    public List<Team> loadTeamsByTournament(Long tournamentId) {
        if (tournamentId == null) {
            return new ArrayList<>();
        }

        // 1. Obtener IDs de equipos del torneo
        List<Long> teamIds = tournamentTeamRepositoryJpa.findByTournamentId(tournamentId)
                .stream()
                .map(TournamentTeamEntity::getTeamId)
                .collect(Collectors.toList());

        if (teamIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Cargar entidades de equipos
        Map<Long, TeamEntity> teamEntities = teamRepositoryJpa.findAllById(teamIds)
                .stream()
                .collect(Collectors.toMap(TeamEntity::getId, t -> t));

        // 3. Cargar relaciones equipo-participante
        List<TeamParticipantEntity> teamParticipants = teamParticipantRepositoryJpa
                .findAll()
                .stream()
                .filter(tp -> teamIds.contains(tp.getTeamId()))
                .collect(Collectors.toList());

        // 4. Obtener IDs de participantes
        List<Long> participantIds = teamParticipants.stream()
                .map(TeamParticipantEntity::getParticipantId)
                .distinct()
                .collect(Collectors.toList());

        // 5. Cargar entidades de participantes
        Map<Long, ParticipantEntity> participantEntities = new HashMap<>();
        if (!participantIds.isEmpty()) {
            participantEntities = participantRepositoryJpa.findAllById(participantIds)
                    .stream()
                    .collect(Collectors.toMap(ParticipantEntity::getId, p -> p));
        }

        // 6. Agrupar participantes por equipo
        Map<Long, List<Participant>> participantsByTeam = new HashMap<>();
        for (TeamParticipantEntity tp : teamParticipants) {
            ParticipantEntity pEntity = participantEntities.get(tp.getParticipantId());
            if (pEntity != null) {
                Participant participant = new Participant(
                        pEntity.getId(),
                        pEntity.getNationalId(),
                        pEntity.getFullName());
                participantsByTeam
                        .computeIfAbsent(tp.getTeamId(), k -> new ArrayList<>())
                        .add(participant);
            }
        }

        // 7. Construir objetos Team del dominio
        List<Team> teams = new ArrayList<>();
        for (Long teamId : teamIds) {
            TeamEntity teamEntity = teamEntities.get(teamId);
            if (teamEntity != null) {
                List<Participant> participants = participantsByTeam.getOrDefault(teamId, new ArrayList<>());
                LocalDateTime createdAt = teamEntity.getCreatedAt() != null
                        ? teamEntity.getCreatedAt().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                        : null;

                Team team = new Team(
                        teamEntity.getId(),
                        teamEntity.getName(),
                        participants,
                        null, // creator - no necesario para isParticipant
                        createdAt);
                teams.add(team);
            }
        }

        return teams;
    }

    @Override
    public Map<Long, List<Team>> loadTeamsForMultipleTournaments(List<Long> tournamentIds) {
        Map<Long, List<Team>> result = new HashMap<>();
        
        if (tournamentIds == null || tournamentIds.isEmpty()) {
            return result;
        }

        // 1. Obtener todas las relaciones torneo-equipo para todos los torneos en una sola consulta
        List<TournamentTeamEntity> allTournamentTeams = tournamentTeamRepositoryJpa
                .findAll()
                .stream()
                .filter(tt -> tournamentIds.contains(tt.getTournamentId()))
                .collect(Collectors.toList());

        if (allTournamentTeams.isEmpty()) {
            // Inicializar listas vacías para cada torneo
            for (Long tournamentId : tournamentIds) {
                result.put(tournamentId, new ArrayList<>());
            }
            return result;
        }

        // 2. Obtener todos los IDs de equipos únicos
        List<Long> allTeamIds = allTournamentTeams.stream()
                .map(TournamentTeamEntity::getTeamId)
                .distinct()
                .collect(Collectors.toList());

        // 3. Cargar todas las entidades de equipos en una sola consulta
        Map<Long, TeamEntity> teamEntities = teamRepositoryJpa.findAllById(allTeamIds)
                .stream()
                .collect(Collectors.toMap(TeamEntity::getId, t -> t));

        // 4. Cargar todas las relaciones equipo-participante en una sola consulta
        List<TeamParticipantEntity> allTeamParticipants = teamParticipantRepositoryJpa
                .findAll()
                .stream()
                .filter(tp -> allTeamIds.contains(tp.getTeamId()))
                .collect(Collectors.toList());

        // 5. Obtener todos los IDs de participantes únicos
        List<Long> allParticipantIds = allTeamParticipants.stream()
                .map(TeamParticipantEntity::getParticipantId)
                .distinct()
                .collect(Collectors.toList());

        // 6. Cargar todas las entidades de participantes en una sola consulta
        Map<Long, ParticipantEntity> participantEntities = new HashMap<>();
        if (!allParticipantIds.isEmpty()) {
            participantEntities = participantRepositoryJpa.findAllById(allParticipantIds)
                    .stream()
                    .collect(Collectors.toMap(ParticipantEntity::getId, p -> p));
        }

        // 7. Agrupar participantes por equipo
        Map<Long, List<Participant>> participantsByTeam = new HashMap<>();
        for (TeamParticipantEntity tp : allTeamParticipants) {
            ParticipantEntity pEntity = participantEntities.get(tp.getParticipantId());
            if (pEntity != null) {
                Participant participant = new Participant(
                        pEntity.getId(),
                        pEntity.getNationalId(),
                        pEntity.getFullName());
                participantsByTeam
                        .computeIfAbsent(tp.getTeamId(), k -> new ArrayList<>())
                        .add(participant);
            }
        }

        // 8. Construir objetos Team y agrupar por torneo
        Map<Long, List<Team>> teamsByTournament = new HashMap<>();
        for (TournamentTeamEntity tt : allTournamentTeams) {
            TeamEntity teamEntity = teamEntities.get(tt.getTeamId());
            if (teamEntity != null) {
                List<Participant> participants = participantsByTeam.getOrDefault(tt.getTeamId(), new ArrayList<>());
                LocalDateTime createdAt = teamEntity.getCreatedAt() != null
                        ? teamEntity.getCreatedAt().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                        : null;

                Team team = new Team(
                        teamEntity.getId(),
                        teamEntity.getName(),
                        participants,
                        null,
                        createdAt);
                
                teamsByTournament
                        .computeIfAbsent(tt.getTournamentId(), k -> new ArrayList<>())
                        .add(team);
            }
        }

        // 9. Asegurar que todos los torneos tengan una lista (aunque sea vacía)
        for (Long tournamentId : tournamentIds) {
            result.put(tournamentId, teamsByTournament.getOrDefault(tournamentId, new ArrayList<>()));
        }

        return result;
    }
}
