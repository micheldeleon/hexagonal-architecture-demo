package com.example.demo.core.ports.in;

import java.util.Date;

public interface StartTournamentPort {

    /**
     * Inicia un torneo cambiando su estado de ABIERTO a INICIADO.
     * 
     * @param tournamentId ID del torneo a iniciar
     * @param organizerEmail Email del organizador que inicia el torneo
     * @return Resultado con la información del inicio del torneo
     */
    StartTournamentResult start(Long tournamentId, String organizerEmail);

    /**
     * Clase interna para devolver el resultado del inicio del torneo.
     */
    record StartTournamentResult(Long tournamentId, String startedByName, Date startedAt, int participantsCount) {
    }
}
