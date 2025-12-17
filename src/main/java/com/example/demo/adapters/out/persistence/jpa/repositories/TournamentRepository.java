package com.example.demo.adapters.out.persistence.jpa.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.adapters.out.persistence.jpa.entities.TournamentJpaEntity;
import com.example.demo.adapters.out.persistence.jpa.interfaces.DisciplineRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.interfaces.FormatRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.interfaces.TournamentRepositoryJpa;
import com.example.demo.adapters.out.persistence.jpa.mappers.TournamentMapper;
import com.example.demo.core.domain.models.Team;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.ports.out.TeamLoaderPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;

@Component
public class TournamentRepository implements TournamentRepositoryPort {

    private final TournamentRepositoryJpa tournamentRepositoryJpa;
    private final FormatRepositoryJpa formatRepositoryJpa;
    private final DisciplineRepositoryJpa disciplineRepositoryJpa;
    private final TeamLoaderPort teamLoaderPort;

    public TournamentRepository(TournamentRepositoryJpa tournamentRepositoryJpa,
            FormatRepositoryJpa formatRepositoryJpa,
            DisciplineRepositoryJpa disciplineRepositoryJpa,
            TeamLoaderPort teamLoaderPort) {
        this.tournamentRepositoryJpa = tournamentRepositoryJpa;
        this.formatRepositoryJpa = formatRepositoryJpa;
        this.disciplineRepositoryJpa = disciplineRepositoryJpa;
        this.teamLoaderPort = teamLoaderPort;
    }

    @Override
    @Transactional
    public Tournament save(Tournament entity, Long organizerId) {
        TournamentJpaEntity e = TournamentMapper.mapToEntity(entity, organizerId);
        e = tournamentRepositoryJpa.save(e);
        if (e.getFormat() != null && e.getFormat().getId() != null) {
            formatRepositoryJpa.findById(e.getFormat().getId())
                    .ifPresent(e::setFormat);
        }
        if (e.getDiscipline() != null && e.getDiscipline().getId() != null) {
            disciplineRepositoryJpa.findById(e.getDiscipline().getId())
                    .ifPresent(e::setDiscipline);
        }
        e.setOrganizerId(organizerId);
        return TournamentMapper.mapToDomain(e);
    }

    @Override
    public Tournament findById(Long id) {
        return tournamentRepositoryJpa.findById(id)
                .map(TournamentMapper::mapToDomain)
                .orElse(null);
    }

    @Override
    public List<Tournament> findAll() {
        List<Tournament> tournaments = new ArrayList<>();
        for (TournamentJpaEntity entity : tournamentRepositoryJpa.findAll()) {
            tournaments.add(TournamentMapper.mapToDomain(entity));
        }
        return tournaments;
    }
    
    @Override
    public Tournament findByIdWithTeams(Long id) {
        Tournament tournament = findById(id);
        if (tournament != null && tournament.getId() != null) {
            List<Team> teams = teamLoaderPort.loadTeamsByTournament(tournament.getId());
            tournament.setTeams(teams);
        }
        return tournament;
    }
    
    @Override
    public List<Tournament> findAllWithTeams() {
        List<Tournament> tournaments = findAll();
        if (tournaments.isEmpty()) {
            return tournaments;
        }
        
        // Obtener todos los IDs de torneos
        List<Long> tournamentIds = tournaments.stream()
                .map(Tournament::getId)
                .filter(id -> id != null)
                .collect(java.util.stream.Collectors.toList());
        
        // Cargar todos los equipos en batch
        java.util.Map<Long, List<Team>> teamsByTournament = 
                teamLoaderPort.loadTeamsForMultipleTournaments(tournamentIds);
        
        // Asignar equipos a cada torneo
        for (Tournament tournament : tournaments) {
            if (tournament.getId() != null) {
                List<Team> teams = teamsByTournament.getOrDefault(tournament.getId(), new ArrayList<>());
                tournament.setTeams(teams);
            }
        }
        
        return tournaments;
    }

    @Override
    public Tournament update(Tournament tournament) {
        TournamentJpaEntity entity = TournamentMapper.mapToEntity(tournament, tournament.getOrganizer().getId());
        entity = tournamentRepositoryJpa.save(entity);
        return TournamentMapper.mapToDomain(entity);
    }

}
