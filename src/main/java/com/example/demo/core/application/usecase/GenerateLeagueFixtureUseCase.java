package com.example.demo.core.application.usecase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.example.demo.core.domain.models.Format;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentModerationStatus;
import com.example.demo.core.domain.models.TournamentMatch;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.Formats.LeagueFormat;
import com.example.demo.core.ports.in.GenerateLeagueFixturePort;
import com.example.demo.core.ports.out.FixturePersistencePort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.NotificationPort;

public class GenerateLeagueFixtureUseCase implements GenerateLeagueFixturePort {

    private static final String STATUS_PENDING = "PENDING";

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final FixturePersistencePort fixturePersistencePort;
    private final NotificationPort notificationPort;

    public GenerateLeagueFixtureUseCase(TournamentRepositoryPort tournamentRepositoryPort,
            FixturePersistencePort fixturePersistencePort,
            NotificationPort notificationPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.fixturePersistencePort = fixturePersistencePort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void generate(Long tournamentId, boolean doubleRound) {
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

        if (tournament.getStatus() != TournamentStatus.INICIADO) {
            throw new IllegalStateException("El torneo debe estar en estado INICIADO para generar el fixture");
        }

        Format format = tournament.getFormat();
        if (!(format instanceof LeagueFormat)) {
            throw new IllegalStateException("El formato del torneo no es liga");
        }

        if (fixturePersistencePort.hasMatches(tournamentId)) {
            throw new IllegalStateException("El fixture ya fue generado");
        }

        List<Long> teamIds = new ArrayList<>(fixturePersistencePort.getTeamIdsForTournament(tournamentId));
        if (teamIds.size() < 2) {
            throw new IllegalStateException("Se requieren al menos 2 equipos para generar el fixture");
        }

        if (teamIds.size() % 2 != 0) {
            teamIds.add(null); // bye
        }

        List<TournamentMatch> matches = buildRoundRobin(tournamentId, teamIds, doubleRound);

        // Si había bye y se generaron partidos con null, igual no se guardan porque la
        // construcción evita partidos con bye en home/away al omitirlos.
        fixturePersistencePort.saveMatches(matches);

        // 🔔 Notificar fixture generado
        try {
            notificationPort.notifyUsersOfTournament(
                tournamentId,
                "Fixture Generado",
                "El fixture del torneo '" + tournament.getName() + "' ha sido generado. ¡Revisa tu calendario!",
                NotificationType.MATCH_SCHEDULED
            );
        } catch (Exception e) {
            System.err.println("Error enviando notificación de fixture: " + e.getMessage());
        }
    }

    private List<TournamentMatch> buildRoundRobin(Long tournamentId, List<Long> teamIds, boolean doubleRound) {
        List<TournamentMatch> matches = new ArrayList<>();
        int teamCount = teamIds.size();
        int rounds = teamCount - 1;
        Date now = new Date();

        List<Long> rotating = new ArrayList<>(teamIds);
        Long fixed = rotating.remove(0); // fijamos el primero, rotamos el resto

        for (int round = 0; round < rounds; round++) {
            int roundNumber = round + 1;
            // armar emparejamientos de la ronda
            List<Long> pairings = new ArrayList<>();
            pairings.add(fixed);
            pairings.add(rotating.get(0));

            for (int i = 1; i < rotating.size() / 2 + 1; i++) {
                pairings.add(rotating.get(i));
                pairings.add(rotating.get(rotating.size() - i));
            }

            // Crear partidos para la ronda
            int matchNumber = 1;
            for (int i = 0; i < pairings.size(); i += 2) {
                Long home = pairings.get(i);
                Long away = pairings.get(i + 1);
                if (home == null || away == null) {
                    continue; // bye, se salta
                }
                matches.add(new TournamentMatch(
                        null,
                        tournamentId,
                        roundNumber,
                        matchNumber++,
                        home,
                        away,
                        null, // scheduledAt
                        STATUS_PENDING,
                        null,
                        null,
                        null, // winnerTeamId
                        now,
                        now));
            }

            // rotar equipos (método círculo)
            Collections.rotate(rotating, 1);
        }

        if (doubleRound) {
            int baseRoundCount = rounds;
            List<TournamentMatch> returnLeg = new ArrayList<>();
            for (TournamentMatch match : matches) {
                returnLeg.add(new TournamentMatch(
                        null,
                        tournamentId,
                        baseRoundCount + match.getRound(),
                        match.getMatchNumber(),
                        match.getAwayTeamId(), // swap
                        match.getHomeTeamId(),
                        null,
                        STATUS_PENDING,
                        null,
                        null,
                        null,
                        now,
                        now));
            }
            matches.addAll(returnLeg);
        }

        // Reasignar matchNumber por ronda para mantener orden continuo si dobleRound
        matches = matches.stream()
                .sorted((a, b) -> {
                    int cmp = Integer.compare(a.getRound(), b.getRound());
                    if (cmp != 0) return cmp;
                    return Integer.compare(a.getMatchNumber(), b.getMatchNumber());
                })
                .toList();

        // Normalizar matchNumber dentro de cada ronda
        List<TournamentMatch> normalized = new ArrayList<>();
        int currentRound = -1;
        int counter = 0;
        for (TournamentMatch m : matches) {
            if (m.getRound() != currentRound) {
                currentRound = m.getRound();
                counter = 1;
            }
            TournamentMatch copy = new TournamentMatch(
                    null,
                    m.getTournamentId(),
                    m.getRound(),
                    counter++,
                    m.getHomeTeamId(),
                    m.getAwayTeamId(),
                    null,
                    STATUS_PENDING,
                    null,
                    null,
                    null,
                    m.getCreatedAt(),
                    m.getUpdatedAt());
            normalized.add(copy);
        }

        return normalized;
    }
}
