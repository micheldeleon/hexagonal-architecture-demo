package com.example.demo.core.application.usecase;

import java.util.Date;
import java.util.List;

import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.RaceResult;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentMatch;
import com.example.demo.core.domain.models.TournamentModerationStatus;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.Formats.RaceFormat;
import com.example.demo.core.ports.in.FinalizeTournamentPort;
import com.example.demo.core.ports.out.FixturePersistencePort;
import com.example.demo.core.ports.out.NotificationPort;
import com.example.demo.core.ports.out.RaceResultPersistencePort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;

/**
 * UseCase para finalizar un torneo.
 * 
 * Valida que:
 * - El torneo existe y está en estado INICIADO
 * - Para torneos de eliminación/liga: todos los partidos del fixture tienen resultado
 * - Para torneos de carrera: los resultados han sido establecidos
 */
public class FinalizeTournamentUseCase implements FinalizeTournamentPort {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final FixturePersistencePort fixturePersistencePort;
    private final RaceResultPersistencePort raceResultPersistencePort;
    private final NotificationPort notificationPort;

    public FinalizeTournamentUseCase(
            TournamentRepositoryPort tournamentRepositoryPort,
            FixturePersistencePort fixturePersistencePort,
            RaceResultPersistencePort raceResultPersistencePort,
            NotificationPort notificationPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.fixturePersistencePort = fixturePersistencePort;
        this.raceResultPersistencePort = raceResultPersistencePort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void finalizeTournament(Long tournamentId) {
        if (tournamentId == null) {
            throw new IllegalArgumentException("tournamentId es requerido");
        }

        Tournament tournament = tournamentRepositoryPort.findById(tournamentId);
        if (tournament == null) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }
        if (tournament.getModerationStatus() == TournamentModerationStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }

        // Validar que el torneo está en estado INICIADO
        if (tournament.getStatus() != TournamentStatus.INICIADO) {
            throw new IllegalStateException("El torneo no está en estado INICIADO");
        }

        // Si es formato de carrera, validar que existan resultados
        if (tournament.getFormat() instanceof RaceFormat) {
            validateRaceResults(tournamentId);
        } else {
            // Para otros formatos (eliminación, liga, etc), validar que todos los partidos tengan resultado
            validateAllMatchesHaveResults(tournamentId);
        }

        // Actualizar estado del torneo
        tournament.setStatus(TournamentStatus.FINALIZADO);
        tournament.setEndAt(new Date());
        tournamentRepositoryPort.save(tournament, tournament.getOrganizer().getId());

        // Notificar a todos los participantes del torneo
        try {
            notificationPort.notifyUsersOfTournament(
                    tournamentId,
                    "Torneo finalizado",
                    "El torneo " + tournament.getName() + " ha finalizado. Gracias por participar!",
                    NotificationType.TOURNAMENT_FINALIZED);
        } catch (Exception e) {
            System.err.println("Error enviando notificación de finalización: " + e.getMessage());
        }
    }

    /**
     * Valida que todos los partidos del fixture tengan resultado (FINISHED)
     */
    private void validateAllMatchesHaveResults(Long tournamentId) {
        List<TournamentMatch> matches = fixturePersistencePort.findByTournament(tournamentId);
        
        if (matches == null || matches.isEmpty()) {
            throw new IllegalStateException("No hay partidos registrados en el torneo");
        }

        for (TournamentMatch match : matches) {
            if (!"FINISHED".equals(match.getStatus())) {
                throw new IllegalStateException(
                        String.format("El partido %d de la ronda %d no tiene resultado. Estado: %s",
                                match.getMatchNumber(), match.getRound(), match.getStatus()));
            }
            
            // Validar que los scores estén establecidos
            if (match.getScoreHome() == null || match.getScoreAway() == null) {
                throw new IllegalStateException(
                        String.format("El partido %d de la ronda %d no tiene los puntajes establecidos",
                                match.getMatchNumber(), match.getRound()));
            }
        }
    }

    /**
     * Valida que los resultados de la carrera hayan sido establecidos
     */
    private void validateRaceResults(Long tournamentId) {
        List<RaceResult> results = raceResultPersistencePort.findByTournament(tournamentId);
        
        if (results == null || results.isEmpty()) {
            throw new IllegalStateException("No hay resultados de carrera registrados en el torneo");
        }

        // Validar que todos los resultados tengan posición asignada
        for (RaceResult result : results) {
            if (result.getPosition() == null) {
                throw new IllegalStateException(
                        String.format("El equipo %d no tiene posición asignada en los resultados",
                                result.getTeamId()));
            }
        }

    }
}
