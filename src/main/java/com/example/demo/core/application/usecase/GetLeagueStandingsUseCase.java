package com.example.demo.core.application.usecase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentMatch;
import com.example.demo.core.domain.models.TournamentModerationStatus;
import com.example.demo.core.domain.models.Formats.LeagueFormat;
import com.example.demo.core.ports.in.GetLeagueStandingsPort;
import com.example.demo.core.ports.in.models.LeagueStanding;
import com.example.demo.core.ports.out.FixturePersistencePort;
import com.example.demo.core.ports.out.TeamQueryPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;

public class GetLeagueStandingsUseCase implements GetLeagueStandingsPort {

    private static final String STATUS_FINISHED = "FINISHED";

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final FixturePersistencePort fixturePersistencePort;
    private final TeamQueryPort teamQueryPort;

    public GetLeagueStandingsUseCase(TournamentRepositoryPort tournamentRepositoryPort,
            FixturePersistencePort fixturePersistencePort,
            TeamQueryPort teamQueryPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.fixturePersistencePort = fixturePersistencePort;
        this.teamQueryPort = teamQueryPort;
    }

    @Override
    public List<LeagueStanding> list(Long tournamentId) {
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
        if (!(tournament.getFormat() instanceof LeagueFormat leagueFormat)) {
            throw new IllegalStateException("El formato del torneo no es liga");
        }

        Map<Long, LeagueAccumulator> table = new HashMap<>();
        // Inicializar con equipos inscriptos
        for (TeamQueryPort.TeamSummary t : teamQueryPort.findTeamsByTournament(tournamentId)) {
            table.put(t.id(), new LeagueAccumulator(t.id(), t.name()));
        }

        // Procesar partidos finalizados
        for (TournamentMatch match : fixturePersistencePort.findByTournament(tournamentId)) {
            if (match == null || match.getStatus() == null || !STATUS_FINISHED.equals(match.getStatus())) {
                continue;
            }
            if (match.getScoreHome() == null || match.getScoreAway() == null) {
                continue;
            }
            Long homeId = match.getHomeTeamId();
            Long awayId = match.getAwayTeamId();
            if (homeId == null || awayId == null) {
                continue;
            }

            LeagueAccumulator home = table.computeIfAbsent(homeId, id -> new LeagueAccumulator(id, null));
            LeagueAccumulator away = table.computeIfAbsent(awayId, id -> new LeagueAccumulator(id, null));

            home.played++;
            away.played++;
            home.goalsFor += match.getScoreHome();
            home.goalsAgainst += match.getScoreAway();
            away.goalsFor += match.getScoreAway();
            away.goalsAgainst += match.getScoreHome();

            if (match.getScoreHome() > match.getScoreAway()) {
                home.won++;
                away.lost++;
                home.points += leagueFormat.getWinPoints();
                away.points += leagueFormat.getLossPoints();
            } else if (match.getScoreHome() < match.getScoreAway()) {
                away.won++;
                home.lost++;
                away.points += leagueFormat.getWinPoints();
                home.points += leagueFormat.getLossPoints();
            } else {
                home.draw++;
                away.draw++;
                home.points += leagueFormat.getDrawPoints();
                away.points += leagueFormat.getDrawPoints();
            }
        }

        return table.values().stream()
                .map(LeagueAccumulator::toStanding)
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.points(), a.points());
                    if (cmp != 0) return cmp;
                    cmp = Integer.compare(b.goalDifference(), a.goalDifference());
                    if (cmp != 0) return cmp;
                    cmp = Integer.compare(b.goalsFor(), a.goalsFor());
                    if (cmp != 0) return cmp;
                    return Long.compare(a.teamId(), b.teamId());
                })
                .toList();
    }

    private static class LeagueAccumulator {
        Long teamId;
        String teamName;
        int played;
        int won;
        int draw;
        int lost;
        int goalsFor;
        int goalsAgainst;
        int points;

        LeagueAccumulator(Long teamId, String teamName) {
            this.teamId = teamId;
            this.teamName = teamName;
        }

        LeagueStanding toStanding() {
            int gd = goalsFor - goalsAgainst;
            return new LeagueStanding(teamId, teamName, played, won, draw, lost, goalsFor, goalsAgainst, gd, points);
        }
    }
}
