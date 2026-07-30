package com.tutorneo.core.ports.out;

import java.util.Date;
import java.util.List;

import com.tutorneo.core.domain.models.Tournament;
import com.tutorneo.core.domain.models.TournamentStatus;

/**
 * Puerto de salida para consultar torneos existentes aplicando filtros
 * opcionales. Se utiliza desde los casos de uso públicos que necesitan mostrar
 * torneos sin exigir autenticación.
 */
public interface FindTournamentsPort {

    /**
     * Devuelve los torneos que cumplan con los filtros. Todos los parámetros son
     * opcionales; cuando se envía {@code null} el filtro se ignora.
     *
     * @param status                estado del torneo (ABIERTO, INICIADO, etc.)
     * @param disciplineId          disciplina
     * @param nameContains          cadena para buscar en el nombre
     * @param startFrom             fecha mínima de inicio
     * @param startTo               fecha máxima de inicio
     * @param withPrize             si debe tener premio definido
     * @param withRegistrationCost  si requiere costo de inscripción (> 0)
     * @return lista de torneos que cumplen los criterios
     */
    List<Tournament> findByFilters(
            TournamentStatus status,
            Long disciplineId,
            String nameContains,
            Date startFrom,
            Date startTo,
            Boolean withPrize,
            Boolean withRegistrationCost);
}
