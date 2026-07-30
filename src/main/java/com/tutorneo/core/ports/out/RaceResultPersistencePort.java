package com.tutorneo.core.ports.out;

import java.util.List;

import com.tutorneo.core.domain.models.RaceResult;

public interface RaceResultPersistencePort {

    List<Long> getTeamIdsForTournament(Long tournamentId);

    void replaceResults(Long tournamentId, List<RaceResult> results);

    List<RaceResult> findByTournament(Long tournamentId);

    /**
     * Devuelve los equipos inscriptos en el torneo (sin resultados), para mostrar un
     * placeholder de ranking cuando aún no se reportaron tiempos.
     */
    List<RaceResult> findRegisteredTeams(Long tournamentId);
}
