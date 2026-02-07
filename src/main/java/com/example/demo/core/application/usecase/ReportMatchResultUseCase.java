package com.example.demo.core.application.usecase;

import java.util.Date;

import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentMatch;
import com.example.demo.core.domain.models.TournamentModerationStatus;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.Formats.EliminationFormat;
import com.example.demo.core.ports.in.ReportMatchResultPort;
import com.example.demo.core.ports.out.FixturePersistencePort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.NotificationPort;

public class ReportMatchResultUseCase implements ReportMatchResultPort {

    private static final String STATUS_FINISHED = "FINISHED";
    private static final String STATUS_PENDING = "PENDING";

    private final FixturePersistencePort fixturePersistencePort;
    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final NotificationPort notificationPort;

    public ReportMatchResultUseCase(FixturePersistencePort fixturePersistencePort,
            TournamentRepositoryPort tournamentRepositoryPort,
            NotificationPort notificationPort) {
        this.fixturePersistencePort = fixturePersistencePort;
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void reportResult(Long tournamentId, Long matchId, Integer scoreHome, Integer scoreAway, Long winnerTeamId) {
        if (tournamentId == null || matchId == null || winnerTeamId == null) {
            throw new IllegalArgumentException("tournamentId, matchId y winnerTeamId son requeridos");
        }

        TournamentMatch match = fixturePersistencePort.getMatch(matchId);
        if (match == null || !tournamentId.equals(match.getTournamentId())) {
            throw new IllegalArgumentException("Partido no encontrado para el torneo");
        }

        Tournament tournament = tournamentRepositoryPort.findById(tournamentId);
        if (tournament == null) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }
        if (tournament.getModerationStatus() == TournamentModerationStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }
        if (!(tournament.getFormat() instanceof EliminationFormat)) {
            throw new IllegalStateException("El formato del torneo no es eliminatorio");
        }
        if (tournament.getStatus() != TournamentStatus.INICIADO) {
            throw new IllegalStateException("El torneo no está en estado INICIADO");
        }

        // Validar que el ganador sea uno de los equipos del partido
        if (!winnerTeamId.equals(match.getHomeTeamId()) && !winnerTeamId.equals(match.getAwayTeamId())) {
            throw new IllegalArgumentException("El ganador no corresponde a los equipos del partido");
        }

        // No permitimos empates; si se pasan scores, deben reflejar ganador
        if (scoreHome != null && scoreAway != null) {
            if (scoreHome.equals(scoreAway)) {
                throw new IllegalArgumentException("No se permiten empates en eliminatorio");
            }
            if (winnerTeamId.equals(match.getHomeTeamId()) && scoreHome < scoreAway) {
                throw new IllegalArgumentException("El ganador no coincide con los puntajes");
            }
            if (winnerTeamId.equals(match.getAwayTeamId()) && scoreAway < scoreHome) {
                throw new IllegalArgumentException("El ganador no coincide con los puntajes");
            }
        }

        Long previousWinner = match.getWinnerTeamId();

        Date now = new Date();
        match.setScoreHome(scoreHome);
        match.setScoreAway(scoreAway);
        match.setWinnerTeamId(winnerTeamId);
        match.setStatus(STATUS_FINISHED);
        match.setUpdatedAt(now);
        fixturePersistencePort.saveMatch(match);

        // 🔔 Notificar resultado del partido a ambos equipos
        try {
            if (match.getHomeTeamId() != null) {
                notificationPort.notifyTeamMembers(
                    match.getHomeTeamId(),
                    "Resultado de Partido",
                    "El resultado de tu partido ha sido registrado.",
                    NotificationType.MATCH_RESULT
                );
            }
            if (match.getAwayTeamId() != null) {
                notificationPort.notifyTeamMembers(
                    match.getAwayTeamId(),
                    "Resultado de Partido",
                    "El resultado de tu partido ha sido registrado.",
                    NotificationType.MATCH_RESULT
                );
            }
        } catch (Exception e) {
            System.err.println("Error enviando notificación de resultado: " + e.getMessage());
        }

        // Avanzar al siguiente partido en la ronda y ajustar si cambió el ganador.
        advanceToNextRound(match, previousWinner, winnerTeamId, now);
    }

    private void advanceToNextRound(TournamentMatch match, Long previousWinner, Long winnerTeamId, Date now) {
        long matchesThisRound = fixturePersistencePort.countByTournamentAndRound(match.getTournamentId(), match.getRound());
        if (matchesThisRound <= 1) {
            return; // Final: no siguiente ronda
        }

        int nextRound = match.getRound() + 1;
        int nextMatchNumber = (match.getMatchNumber() + 1) / 2;
        boolean winnerGoesAsHome = (match.getMatchNumber() % 2 == 1); // impar -> home, par -> away

        TournamentMatch nextMatch = fixturePersistencePort.findByTournamentRoundAndNumber(
                match.getTournamentId(), nextRound, nextMatchNumber);

        if (nextMatch == null) {
            nextMatch = new TournamentMatch();
            nextMatch.setTournamentId(match.getTournamentId());
            nextMatch.setRound(nextRound);
            nextMatch.setMatchNumber(nextMatchNumber);
            nextMatch.setStatus(STATUS_PENDING);
            nextMatch.setCreatedAt(now);
        }

        boolean winnerChanged = previousWinner != null && !previousWinner.equals(winnerTeamId);

        // Si el ganador cambia y el siguiente partido estaba finalizado, lo reabrimos para recalcular.
        if (winnerChanged && STATUS_FINISHED.equals(nextMatch.getStatus())) {
            nextMatch.setStatus(STATUS_PENDING);
            nextMatch.setScoreHome(null);
            nextMatch.setScoreAway(null);
            nextMatch.setWinnerTeamId(null);
        }

        if (winnerGoesAsHome) {
            nextMatch.setHomeTeamId(winnerTeamId);
        } else {
            nextMatch.setAwayTeamId(winnerTeamId);
        }

        nextMatch.setUpdatedAt(now);
        fixturePersistencePort.saveMatch(nextMatch);
    }
}
