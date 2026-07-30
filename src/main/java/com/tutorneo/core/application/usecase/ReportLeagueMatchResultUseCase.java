package com.tutorneo.core.application.usecase;

import java.util.Date;

import com.tutorneo.core.domain.models.Tournament;
import com.tutorneo.core.domain.models.TournamentMatch;
import com.tutorneo.core.domain.models.TournamentModerationStatus;
import com.tutorneo.core.domain.models.TournamentStatus;
import com.tutorneo.core.domain.models.NotificationType;
import com.tutorneo.core.domain.models.Formats.LeagueFormat;
import com.tutorneo.core.ports.in.ReportLeagueMatchResultPort;
import com.tutorneo.core.ports.out.FixturePersistencePort;
import com.tutorneo.core.ports.out.TournamentRepositoryPort;
import com.tutorneo.core.ports.out.NotificationPort;

public class ReportLeagueMatchResultUseCase implements ReportLeagueMatchResultPort {

    private static final String STATUS_FINISHED = "FINISHED";

    private final FixturePersistencePort fixturePersistencePort;
    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final NotificationPort notificationPort;

    public ReportLeagueMatchResultUseCase(FixturePersistencePort fixturePersistencePort,
            TournamentRepositoryPort tournamentRepositoryPort,
            NotificationPort notificationPort) {
        this.fixturePersistencePort = fixturePersistencePort;
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void reportResult(Long tournamentId, Long matchId, Integer scoreHome, Integer scoreAway) {
        if (tournamentId == null || matchId == null) {
            throw new IllegalArgumentException("tournamentId y matchId son requeridos");
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
        if (!(tournament.getFormat() instanceof LeagueFormat)) {
            throw new IllegalStateException("El formato del torneo no es liga");
        }
        if (tournament.getStatus() != TournamentStatus.INICIADO) {
            throw new IllegalStateException("El torneo no está en estado INICIADO");
        }

        if (scoreHome == null || scoreAway == null) {
            throw new IllegalArgumentException("scoreHome y scoreAway son requeridos");
        }

        Date now = new Date();
        match.setScoreHome(scoreHome);
        match.setScoreAway(scoreAway);
        match.setStatus(STATUS_FINISHED);
        match.setUpdatedAt(now);

        if (!scoreHome.equals(scoreAway)) {
            Long winner = scoreHome > scoreAway ? match.getHomeTeamId() : match.getAwayTeamId();
            match.setWinnerTeamId(winner);
        } else {
            match.setWinnerTeamId(null); // empate permitido
        }

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
    }
}
