package com.example.demo.core.ports.out;

import java.util.List;

/**
 * Consultas para resolver equipos inscriptos a un torneo por capitán/creador.
 */
public interface TeamCaptainQueryPort {

    /**
     * Devuelve los IDs de equipos inscriptos al torneo cuyo creador (capitán) coincide con
     * {@code captainUserId}.
     */
    List<Long> findCaptainTeamIdsInTournament(Long tournamentId, Long captainUserId);
}

