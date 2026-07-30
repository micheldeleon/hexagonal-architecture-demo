package com.tutorneo.core.application.usecase;

import java.util.Date;
import java.util.List;

import com.tutorneo.core.domain.models.Tournament;
import com.tutorneo.core.domain.models.TournamentStatus;
import com.tutorneo.core.ports.in.ListPublicTournamentsPort;
import com.tutorneo.core.ports.out.FindTournamentsPort;

public class ListPublicTournamentsUseCase implements ListPublicTournamentsPort {

    private final FindTournamentsPort findTournamentsPort;

    public ListPublicTournamentsUseCase(FindTournamentsPort findTournamentsPort) {
        this.findTournamentsPort = findTournamentsPort;
    }

    @Override
    public List<Tournament> list(
            TournamentStatus status,
            Long disciplineId,
            String nameContains,
            Date startFrom,
            Date startTo,
            Boolean withPrize,
            Boolean withRegistrationCost) {
        return findTournamentsPort.findByFilters(
                status,
                disciplineId,
                nameContains,
                startFrom,
                startTo,
                withPrize,
                withRegistrationCost);
    }
}
