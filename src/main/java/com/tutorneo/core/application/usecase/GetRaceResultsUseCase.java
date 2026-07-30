package com.tutorneo.core.application.usecase;

import java.util.List;

import com.tutorneo.core.domain.models.RaceResult;
import com.tutorneo.core.domain.models.Tournament;
import com.tutorneo.core.domain.models.TournamentModerationStatus;

import com.tutorneo.core.domain.models.Formats.RaceFormat;
import com.tutorneo.core.ports.in.GetRaceResultsPort;
import com.tutorneo.core.ports.out.RaceResultPersistencePort;
import com.tutorneo.core.ports.out.TournamentRepositoryPort;

public class GetRaceResultsUseCase implements GetRaceResultsPort {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final RaceResultPersistencePort raceResultPersistencePort;

    public GetRaceResultsUseCase(TournamentRepositoryPort tournamentRepositoryPort,
            RaceResultPersistencePort raceResultPersistencePort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.raceResultPersistencePort = raceResultPersistencePort;
    }

    @Override
    public List<RaceResult> list(Long tournamentId) {
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
        if (!(tournament.getFormat() instanceof RaceFormat)) {
            throw new IllegalStateException("El formato del torneo no es carrera");
        }

        var persisted = raceResultPersistencePort.findByTournament(tournamentId);
        if (!persisted.isEmpty()) {
            return persisted;
        }

        // Si no hay resultados cargados, devolvemos los inscriptos como placeholder
        // (independientemente del estado) para mostrar el ranking base.
        return raceResultPersistencePort.findRegisteredTeams(tournamentId);
    }
}
