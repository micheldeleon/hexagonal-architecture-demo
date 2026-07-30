package com.tutorneo.core.ports.in;

import java.util.Date;
import java.util.List;

import com.tutorneo.core.domain.models.Tournament;
import com.tutorneo.core.domain.models.TournamentStatus;

/**
 * Puerto de entrada para exponer el listado público de torneos (dashboard).
 * Permite aplicar filtros opcionales desde el controller.
 */
public interface ListPublicTournamentsPort {

    List<Tournament> list(
            TournamentStatus status,
            Long disciplineId,
            String nameContains,
            Date startFrom,
            Date startTo,
            Boolean withPrize,
            Boolean withRegistrationCost);
}
