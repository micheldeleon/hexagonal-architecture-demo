package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.ports.out.FindTournamentsPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class ListPublicTournamentsUseCaseTest {

    @Mock
    private FindTournamentsPort findTournamentsPort;

    @Test
    void list_delegatesToFindPortWithSameFilters() {
        ListPublicTournamentsUseCase useCase = new ListPublicTournamentsUseCase(findTournamentsPort);
        Tournament t = TestDataFactory.baseTournament(1L, 1L);

        when(findTournamentsPort.findByFilters(TournamentStatus.ABIERTO, 1L, "torneo", new Date(0), new Date(1), true,
                false)).thenReturn(List.of(t));

        assertThat(useCase.list(TournamentStatus.ABIERTO, 1L, "torneo", new Date(0), new Date(1), true, false))
                .containsExactly(t);
    }
}

