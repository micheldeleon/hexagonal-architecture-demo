package com.example.demo.core.application.usecase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.example.demo.core.domain.models.Format;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentMatch;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.Formats.EliminationFormat;
import com.example.demo.core.ports.in.GenerateEliminationFixturePort;
import com.example.demo.core.ports.in.GetFixturePort;
import com.example.demo.core.ports.out.FixturePersistencePort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.NotificationPort;

public class GenerateEliminationFixtureUseCase implements GenerateEliminationFixturePort, GetFixturePort {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_FINISHED = "FINISHED";

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final FixturePersistencePort fixturePersistencePort;
    private final NotificationPort notificationPort;

    public GenerateEliminationFixtureUseCase(TournamentRepositoryPort tournamentRepositoryPort,
            FixturePersistencePort fixturePersistencePort,
            NotificationPort notificationPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.fixturePersistencePort = fixturePersistencePort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void generate(Long tournamentId) {
        if (tournamentId == null) {
            throw new IllegalArgumentException("tournamentId es requerido");
        }

        Tournament tournament = tournamentRepositoryPort.findById(tournamentId);
        if (tournament == null) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }

        if (tournament.getStatus() != TournamentStatus.INICIADO) {
            throw new IllegalStateException("El torneo debe estar en estado INICIADO para generar el fixture");
        }

        // 🔔 Notificar que el torneo ha iniciado
        try {
            notificationPort.notifyUsersOfTournament(
                tournamentId,
                "Torneo Iniciado",
                "¡El torneo '" + tournament.getName() + "' ha comenzado! La bracket de eliminación está siendo generada.",
                NotificationType.TOURNAMENT_STARTED
            );
        } catch (Exception e) {
            System.err.println("Error enviando notificación de inicio: " + e.getMessage());
        }

        Format format = tournament.getFormat();
        if (!(format instanceof EliminationFormat)) {
            throw new IllegalStateException("El formato del torneo no es eliminatorio");
        }

        if (fixturePersistencePort.hasMatches(tournamentId)) {
            throw new IllegalStateException("El fixture ya fue generado");
        }

        List<Long> teamIds = fixturePersistencePort.getTeamIdsForTournament(tournamentId);
        if (teamIds == null || teamIds.size() < 2) {
            throw new IllegalStateException("Se requieren al menos 2 equipos para generar el fixture");
        }

        Collections.shuffle(teamIds);
        int totalSlots = nextPowerOfTwo(teamIds.size());
        while (teamIds.size() < totalSlots) {
            teamIds.add(null); // bye
        }

        //Metodo para construir el fixture en formato eliminatorio
        //buildBracket(...) arma la ronda 1 emparejando de dos en dos y marca byes; devuelve solo la primera ronda (las siguientes se crearán al reportar resultados).
        List<TournamentMatch> matches = buildBracket(tournamentId, teamIds);
        // Guardamos los partidos generados
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

    @Override
    public List<TournamentMatch> getFixture(Long tournamentId) {
        return fixturePersistencePort.findByTournament(tournamentId);
    }

    private List<TournamentMatch> buildBracket(Long tournamentId, List<Long> initialSlots) {
        List<TournamentMatch> matches = new ArrayList<>();
        List<Long> currentSlots = new ArrayList<>(initialSlots);
        Date now = new Date();
        int round = 1;

        while (currentSlots.size() > 1) {
            List<Long> nextSlots = new ArrayList<>();
            int matchesInRound = currentSlots.size() / 2;
            for (int m = 0; m < matchesInRound; m++) {
                Long home = currentSlots.get(m * 2);
                Long away = currentSlots.get(m * 2 + 1);

                String status = STATUS_PENDING;
                Long winner = null;

                // Bye en la primera ronda (home o away null y el otro presente)
                if (round == 1 && ((home != null && away == null) || (home == null && away != null))) {
                    winner = home != null ? home : away;
                    status = STATUS_FINISHED;
                }

                TournamentMatch match = new TournamentMatch(
                        null,
                        tournamentId,
                        round,
                        m + 1,
                        home,
                        away,
                        null,
                        status,
                        null,
                        null,
                        winner,
                        now,
                        now);
                matches.add(match);

                nextSlots.add(winner); // puede ser null si no hay ganador definido todavía
            }
            currentSlots = nextSlots;
            round++;
        }

        return matches;
    }

    private int nextPowerOfTwo(int n) {
        int pow = 1;
        while (pow < n) {
            pow <<= 1;
        }
        return pow;
    }
}
