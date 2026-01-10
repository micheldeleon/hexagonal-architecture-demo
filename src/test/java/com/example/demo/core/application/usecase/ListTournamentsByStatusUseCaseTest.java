package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.ports.out.FindTournamentsByStatusPort;

@ExtendWith(MockitoExtension.class)
class ListTournamentsByStatusUseCaseTest {

    @Mock
    private FindTournamentsByStatusPort findTournamentsByStatusPort;

    @Test
    void listByStatus_requiresNonNullStatus() {
        ListTournamentsByStatusUseCase useCase = new ListTournamentsByStatusUseCase(findTournamentsByStatusPort);
        assertThatThrownBy(() -> useCase.listByStatus(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("status es requerido");

        useCase.listByStatus(TournamentStatus.ABIERTO);
        verify(findTournamentsByStatusPort).findByStatus(TournamentStatus.ABIERTO);
    }
}

