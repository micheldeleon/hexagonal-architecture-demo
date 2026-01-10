package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.ports.in.CreateNotificationPort;
import com.example.demo.core.ports.out.NotificationPort;
import com.example.demo.core.ports.out.TournamentRegistrationPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class RegisterToTournamentUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;
    @Mock
    private TournamentRegistrationPort tournamentRegistrationPort;
    @Mock
    private NotificationPort notificationPort;
    @Mock
    private CreateNotificationPort createNotificationPort;

    @Test
    void register_validatesInputs() {
        RegisterToTournamentUseCase useCase = new RegisterToTournamentUseCase(tournamentRepositoryPort,
                tournamentRegistrationPort, notificationPort, createNotificationPort);

        assertThatThrownBy(() -> useCase.register(null, 1L)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.register(1L, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void register_rejectsWhenTournamentNotOpenOrDeadlinePassedOrAlreadyRegistered() {
        RegisterToTournamentUseCase useCase = new RegisterToTournamentUseCase(tournamentRepositoryPort,
                tournamentRegistrationPort, notificationPort, createNotificationPort);

        Tournament tournament = TestDataFactory.baseTournament(10L, 1L);
        tournament.setStatus(TournamentStatus.INICIADO);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        assertThatThrownBy(() -> useCase.register(10L, 2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("abierto");

        tournament.setStatus(TournamentStatus.ABIERTO);
        tournament.setRegistrationDeadline(new Date(System.currentTimeMillis() - 1000));
        assertThatThrownBy(() -> useCase.register(10L, 2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cerr");

        tournament.setRegistrationDeadline(new Date(System.currentTimeMillis() + 100000));
        when(tournamentRegistrationPort.exists(10L, 2L)).thenReturn(true);
        assertThatThrownBy(() -> useCase.register(10L, 2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya est");
        verify(tournamentRegistrationPort, never()).register(any(), any());
    }

    @Test
    void register_success_incrementsTeamsInscribedAndNotifiesUser() {
        RegisterToTournamentUseCase useCase = new RegisterToTournamentUseCase(tournamentRepositoryPort,
                tournamentRegistrationPort, notificationPort, createNotificationPort);

        Tournament tournament = TestDataFactory.baseTournament(10L, 1L);
        tournament.setStatus(TournamentStatus.ABIERTO);
        tournament.setTeamsInscribed(0);
        tournament.setMaxParticipantsPerTournament(2);
        tournament.setRegistrationDeadline(new Date(System.currentTimeMillis() + 100000));
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);
        when(tournamentRegistrationPort.exists(10L, 2L)).thenReturn(false);

        useCase.register(10L, 2L);

        verify(tournamentRegistrationPort).register(10L, 2L);

        ArgumentCaptor<Tournament> savedTournament = ArgumentCaptor.forClass(Tournament.class);
        verify(tournamentRepositoryPort).save(savedTournament.capture(), eq(1L));
        assertThat(savedTournament.getValue().getTeamsInscribed()).isEqualTo(1);

        verify(createNotificationPort).createNotification(eq(2L), eq(NotificationType.REGISTRATION_CONFIRMED), any(),
                any(), eq(10L));
    }

    @Test
    void register_notifiesTournamentFullWhenMaxReached() {
        RegisterToTournamentUseCase useCase = new RegisterToTournamentUseCase(tournamentRepositoryPort,
                tournamentRegistrationPort, notificationPort, createNotificationPort);

        Tournament tournament = TestDataFactory.baseTournament(10L, 1L);
        tournament.setStatus(TournamentStatus.ABIERTO);
        tournament.setTeamsInscribed(1);
        tournament.setMaxParticipantsPerTournament(2);
        tournament.setRegistrationDeadline(new Date(System.currentTimeMillis() + 100000));
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);
        when(tournamentRegistrationPort.exists(10L, 2L)).thenReturn(false);

        useCase.register(10L, 2L);

        verify(notificationPort).notifyUsersOfTournament(eq(10L), any(), any(), eq(NotificationType.TOURNAMENT_FULL));
    }
}

