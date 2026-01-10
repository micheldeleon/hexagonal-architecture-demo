package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class CreateTournamentUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;

    @Test
    void create_validatesDomainAndPersists() {
        CreateTournamentUseCase useCase = new CreateTournamentUseCase(tournamentRepositoryPort);

        Tournament input = TestDataFactory.baseTournament(null, 10L);
        input.setStatus(null);
        input.setCreatedAt(null);

        Tournament persisted = TestDataFactory.baseTournament(1L, 10L);
        when(tournamentRepositoryPort.save(any(Tournament.class), eq(10L))).thenReturn(persisted);

        Tournament result = useCase.create(input, 10L);
        assertThat(result).isSameAs(persisted);

        ArgumentCaptor<Tournament> savedCaptor = ArgumentCaptor.forClass(Tournament.class);
        verify(tournamentRepositoryPort).save(savedCaptor.capture(), eq(10L));
        assertThat(savedCaptor.getValue().getCreatedAt()).isNotNull();
        assertThat(savedCaptor.getValue().getStatus()).isEqualTo(TournamentStatus.ABIERTO);
    }

    @Test
    void create_rejectsInvalidTournamentBeforeCallingRepository() {
        CreateTournamentUseCase useCase = new CreateTournamentUseCase(tournamentRepositoryPort);
        Tournament input = TestDataFactory.baseTournament(null, 10L);
        input.setName(" ");

        assertThatThrownBy(() -> useCase.create(input, 10L))
                .isInstanceOf(IllegalArgumentException.class);
        verify(tournamentRepositoryPort, never()).save(any(), any());
    }
}

