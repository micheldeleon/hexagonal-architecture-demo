package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.CancelTournamentPort.CancelTournamentResult;
import com.example.demo.core.ports.out.NotificationPort;
import com.example.demo.core.ports.out.TournamentCleanupPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class CancelTournamentUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;
    @Mock
    private TournamentCleanupPort tournamentCleanupPort;
    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private NotificationPort notificationPort;

    @Test
    void cancel_validatesInputs() {
        CancelTournamentUseCase useCase = new CancelTournamentUseCase(tournamentRepositoryPort, tournamentCleanupPort,
                userRepositoryPort, notificationPort);

        assertThatThrownBy(() -> useCase.cancel(null, "ana@example.com"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.cancel(1L, " "))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void cancel_rejectsWhenUserIsNotOrganizerOrTournamentNotOpen() {
        CancelTournamentUseCase useCase = new CancelTournamentUseCase(tournamentRepositoryPort, tournamentCleanupPort,
                userRepositoryPort, notificationPort);

        User user = TestDataFactory.validUser(1L);
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(user));

        Tournament tournament = TestDataFactory.baseTournament(10L, 99L);
        tournament.setStatus(TournamentStatus.ABIERTO);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        assertThatThrownBy(() -> useCase.cancel(10L, "ana@example.com"))
                .isInstanceOf(SecurityException.class);

        tournament.getOrganizer().setId(1L);
        tournament.setStatus(TournamentStatus.INICIADO);
        assertThatThrownBy(() -> useCase.cancel(10L, "ana@example.com"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cancel_success_updatesTournamentAndCleansRegistrations() {
        CancelTournamentUseCase useCase = new CancelTournamentUseCase(tournamentRepositoryPort, tournamentCleanupPort,
                userRepositoryPort, notificationPort);

        User user = TestDataFactory.validUser(1L);
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(user));

        Tournament tournament = TestDataFactory.baseTournament(10L, 1L);
        tournament.setStatus(TournamentStatus.ABIERTO);
        tournament.setTeamsInscribed(5);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        CancelTournamentResult result = useCase.cancel(10L, "ana@example.com");
        assertThat(result.tournamentId()).isEqualTo(10L);
        assertThat(result.canceledByName()).isEqualTo(user.getName());
        assertThat(result.canceledAt()).isNotNull();

        verify(tournamentCleanupPort).removeTeamsAndRegistrations(10L);

        ArgumentCaptor<Tournament> updatedCaptor = ArgumentCaptor.forClass(Tournament.class);
        verify(tournamentRepositoryPort).update(updatedCaptor.capture());
        assertThat(updatedCaptor.getValue().getStatus()).isEqualTo(TournamentStatus.CANCELADO);
        assertThat(updatedCaptor.getValue().getTeamsInscribed()).isEqualTo(0);

        verify(notificationPort).notifyUsersOfTournament(eq(10L), any(), any(), eq(NotificationType.TOURNAMENT_CANCELED));
    }

    @Test
    void cancel_doesNotFailWhenNotificationFails() {
        CancelTournamentUseCase useCase = new CancelTournamentUseCase(tournamentRepositoryPort, tournamentCleanupPort,
                userRepositoryPort, notificationPort);

        User user = TestDataFactory.validUser(1L);
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        Tournament tournament = TestDataFactory.baseTournament(10L, 1L);
        tournament.setStatus(TournamentStatus.ABIERTO);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        org.mockito.Mockito.doThrow(new RuntimeException("boom")).when(notificationPort)
                .notifyUsersOfTournament(any(), any(), any(), any());

        useCase.cancel(10L, "ana@example.com");
        verify(tournamentCleanupPort).removeTeamsAndRegistrations(10L);
        verify(tournamentRepositoryPort).update(any(Tournament.class));
    }

    @Test
    void cancel_doesNotCallCleanupWhenTournamentMissing() {
        CancelTournamentUseCase useCase = new CancelTournamentUseCase(tournamentRepositoryPort, tournamentCleanupPort,
                userRepositoryPort, notificationPort);

        User user = TestDataFactory.validUser(1L);
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(tournamentRepositoryPort.findById(10L)).thenReturn(null);

        assertThatThrownBy(() -> useCase.cancel(10L, "ana@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Torneo no encontrado");
        verify(tournamentCleanupPort, never()).removeTeamsAndRegistrations(any());
    }
}

