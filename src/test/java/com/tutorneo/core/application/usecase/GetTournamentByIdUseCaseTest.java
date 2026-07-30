package com.tutorneo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tutorneo.core.domain.models.Tournament;
import com.tutorneo.core.ports.out.TournamentRepositoryPort;
import com.tutorneo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class GetTournamentByIdUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;

    @Test
    void getTournamentById_delegatesToRepository() {
        GetTournamentByIdUseCase useCase = new GetTournamentByIdUseCase(tournamentRepositoryPort);
        Tournament tournament = TestDataFactory.baseTournament(10L, 1L);
        when(tournamentRepositoryPort.findByIdWithTeams(10L)).thenReturn(tournament);
        assertThat(useCase.getTournamentById(10L)).isSameAs(tournament);
    }
}

