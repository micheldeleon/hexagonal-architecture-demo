package com.example.demo.core.application.usecase;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.demo.core.domain.models.RaceResult;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentModerationStatus;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.Formats.RaceFormat;
import com.example.demo.core.ports.in.ReportRaceResultsPort;
import com.example.demo.core.ports.out.RaceResultPersistencePort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;

public class ReportRaceResultsUseCase implements ReportRaceResultsPort {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final RaceResultPersistencePort raceResultPersistencePort;
    private final UserRepositoryPort userRepositoryPort;

    public ReportRaceResultsUseCase(TournamentRepositoryPort tournamentRepositoryPort,
            RaceResultPersistencePort raceResultPersistencePort,
            UserRepositoryPort userRepositoryPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.raceResultPersistencePort = raceResultPersistencePort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void report(Long tournamentId, String organizerEmail, List<RaceResult> results) {
        if (tournamentId == null) {
            throw new IllegalArgumentException("tournamentId es requerido");
        }
        if (results == null || results.isEmpty()) {
            throw new IllegalArgumentException("Se requieren resultados a reportar");
        }
        if (organizerEmail == null || organizerEmail.isBlank()) {
            throw new IllegalArgumentException("organizerEmail es requerido");
        }

        var organizer = userRepositoryPort.findByEmail(organizerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Organizador no encontrado"));

        Tournament tournament = tournamentRepositoryPort.findById(tournamentId);
        if (tournament == null) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }
        if (tournament.getModerationStatus() == TournamentModerationStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Torneo no encontrado");
        }
        if (!(tournament.getFormat() instanceof RaceFormat)) {
            throw new IllegalStateException("El formato del torneo no es carrera");
        }
        if (tournament.getStatus() != TournamentStatus.INICIADO) {
            throw new IllegalStateException("El torneo no esta en estado INICIADO");
        }
        if (tournament.getOrganizer() == null || tournament.getOrganizer().getId() == null
                || !tournament.getOrganizer().getId().equals(organizer.getId())) {
            throw new IllegalStateException("Solo el organizador puede reportar resultados");
        }

        List<Long> teamIdsInTournament = raceResultPersistencePort.getTeamIdsForTournament(tournamentId);
        if (teamIdsInTournament == null || teamIdsInTournament.isEmpty()) {
            throw new IllegalStateException("No hay inscriptos en el torneo");
        }

        Set<Long> providedTeamIds = results.stream()
                .map(RaceResult::getTeamId)
                .collect(Collectors.toSet());
        if (providedTeamIds.contains(null)) {
            throw new IllegalArgumentException("Todos los resultados deben tener teamId");
        }
        if (providedTeamIds.size() != results.size()) {
            throw new IllegalArgumentException("Hay resultados duplicados para un mismo teamId");
        }

        Set<Long> expectedTeamIds = Set.copyOf(teamIdsInTournament);
        if (!providedTeamIds.equals(expectedTeamIds)) {
            throw new IllegalArgumentException("Los resultados deben incluir exactamente a todos los inscriptos");
        }

        Map<Long, RaceResult> resultsByTeam = results.stream()
                .collect(Collectors.toMap(RaceResult::getTeamId, r -> r));

        for (RaceResult r : resultsByTeam.values()) {
            if (r.getTimeMillis() == null || r.getTimeMillis() <= 0) {
                throw new IllegalArgumentException("El tiempo debe ser mayor a 0 para todos los equipos");
            }
        }

        Date now = new Date();
        List<RaceResult> ordered = resultsByTeam.values().stream()
                .sorted((a, b) -> {
                    int cmp = a.getTimeMillis().compareTo(b.getTimeMillis());
                    if (cmp != 0) {
                        return cmp;
                    }
                    return a.getTeamId().compareTo(b.getTeamId());
                })
                .map(r -> {
                    RaceResult rr = new RaceResult();
                    rr.setTournamentId(tournamentId);
                    rr.setTeamId(r.getTeamId());
                    rr.setTimeMillis(r.getTimeMillis());
                    rr.setCreatedAt(now);
                    rr.setUpdatedAt(now);
                    return rr;
                })
                .toList();

        for (int i = 0; i < ordered.size(); i++) {
            ordered.get(i).setPosition(i + 1);
        }

        raceResultPersistencePort.replaceResults(tournamentId, ordered);
    }
}
