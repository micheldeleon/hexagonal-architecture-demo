package com.tutorneo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tutorneo.core.domain.models.Tournament;
import com.tutorneo.core.ports.out.TournamentRepositoryPort;
import com.tutorneo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class GetAllTournamentsUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;

    @Test
    void getAllTournaments_delegatesToRepository() {
        GetAllTournamentsUseCase useCase = new GetAllTournamentsUseCase(tournamentRepositoryPort);
        Tournament t = TestDataFactory.baseTournament(1L, 1L);
        when(tournamentRepositoryPort.findAll()).thenReturn(List.of(t));
        assertThat(useCase.getAllTournaments()).containsExactly(t);
    }
}

