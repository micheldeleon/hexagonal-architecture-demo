package com.tutorneo.core.application.usecase;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.tutorneo.core.domain.models.Tournament;
import com.tutorneo.core.domain.models.TournamentStatus;
import com.tutorneo.core.ports.in.ListTournamentsByStatusPort;
import com.tutorneo.core.ports.out.FindTournamentsByStatusPort;

@Service
public class ListTournamentsByStatusUseCase implements ListTournamentsByStatusPort {

    private final FindTournamentsByStatusPort findTournamentsByStatusPort;

    public ListTournamentsByStatusUseCase(FindTournamentsByStatusPort findTournamentsByStatusPort) {
        this.findTournamentsByStatusPort = findTournamentsByStatusPort;
    }

    @Override
    public List<Tournament> listByStatus(TournamentStatus status) {
        Objects.requireNonNull(status, "status es requerido");
        return findTournamentsByStatusPort.findByStatus(status);
    }
}
