package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.adapters.in.api.dto.ParticipantRequest;
import com.example.demo.adapters.in.api.dto.RunnerRegistrationRequest;
import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.domain.models.Formats.RaceFormat;
import com.example.demo.core.ports.in.CreateNotificationPort;
import com.example.demo.core.ports.out.NotificationPort;
import com.example.demo.core.ports.out.TeamRegistrationPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class RegisterRunnerToTournamentUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;
    @Mock
    private TeamRegistrationPort teamRegistrationPort;
    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private NotificationPort notificationPort;
    @Mock
    private CreateNotificationPort createNotificationPort;

    @Test
    void register_validatesTournamentAndUserAndDeadline() {
        RegisterRunnerToTournamentUseCase useCase = new RegisterRunnerToTournamentUseCase(tournamentRepositoryPort,
                teamRegistrationPort, userRepositoryPort, notificationPort, createNotificationPort);

        assertThatThrownBy(() -> useCase.register(null, "ana@example.com", null))
                .isInstanceOf(IllegalArgumentException.class);

        Tournament t = TestDataFactory.baseTournament(10L, 1L);
        t.setFormat(new RaceFormat());
        t.setStatus(TournamentStatus.ABIERTO);
        t.setRegistrationDeadline(new Date(System.currentTimeMillis() - 1000));
        when(tournamentRepositoryPort.findById(10L)).thenReturn(t);

        assertThatThrownBy(() -> useCase.register(10L, "ana@example.com", null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cerro");
    }

    @Test
    void register_usesRequestFullNameAndNationalIdWhenProvided() {
        RegisterRunnerToTournamentUseCase useCase = new RegisterRunnerToTournamentUseCase(tournamentRepositoryPort,
                teamRegistrationPort, userRepositoryPort, notificationPort, createNotificationPort);

        Tournament t = TestDataFactory.baseTournament(10L, 1L);
        t.setFormat(new RaceFormat());
        t.setStatus(TournamentStatus.ABIERTO);
        t.setRegistrationDeadline(new Date(System.currentTimeMillis() + 100000));
        when(tournamentRepositoryPort.findById(10L)).thenReturn(t);

        User user = new User(5L, "Ana", "Pérez", "ana@example.com", "password123");
        user.setNationalId(""); // force request nationalId
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(user));

        RunnerRegistrationRequest req = new RunnerRegistrationRequest("  Corredor X  ", "12345678");
        useCase.register(10L, "ana@example.com", req);

        ArgumentCaptor<String> teamName = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ParticipantRequest>> participants = ArgumentCaptor.forClass(List.class);

        verify(teamRegistrationPort).registerTeam(eq(10L), eq(5L), teamName.capture(), eq(1L), participants.capture());
        assertThat(teamName.getValue()).isEqualTo("Corredor X");
        assertThat(participants.getValue()).hasSize(1);
        assertThat(participants.getValue().get(0).fullName()).isEqualTo("Corredor X");
        assertThat(participants.getValue().get(0).nationalId()).isEqualTo("12345678");

        verify(createNotificationPort).createNotification(eq(5L), eq(NotificationType.REGISTRATION_CONFIRMED), any(),
                any(), eq(10L));
    }

    @Test
    void register_rejectsWhenCannotResolveNameOrNationalId() {
        RegisterRunnerToTournamentUseCase useCase = new RegisterRunnerToTournamentUseCase(tournamentRepositoryPort,
                teamRegistrationPort, userRepositoryPort, notificationPort, createNotificationPort);

        Tournament t = TestDataFactory.baseTournament(10L, 1L);
        t.setFormat(new RaceFormat());
        t.setStatus(TournamentStatus.ABIERTO);
        t.setRegistrationDeadline(new Date(System.currentTimeMillis() + 100000));
        when(tournamentRepositoryPort.findById(10L)).thenReturn(t);

        User user = new User();
        user.setId(5L);
        user.setEmail("ana@example.com");
        user.setName(" ");
        user.setLastName(" ");
        user.setNationalId(" ");
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> useCase.register(10L, "ana@example.com", new RunnerRegistrationRequest(null, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre completo");
    }

    @Test
    void register_notifiesTournamentFullWhenMaxReached() {
        RegisterRunnerToTournamentUseCase useCase = new RegisterRunnerToTournamentUseCase(tournamentRepositoryPort,
                teamRegistrationPort, userRepositoryPort, notificationPort, createNotificationPort);

        Tournament t = TestDataFactory.baseTournament(10L, 1L);
        t.setFormat(new RaceFormat());
        t.setStatus(TournamentStatus.ABIERTO);
        t.setRegistrationDeadline(new Date(System.currentTimeMillis() + 100000));
        t.setTeamsInscribed(1);
        t.setMaxParticipantsPerTournament(2);
        when(tournamentRepositoryPort.findById(10L)).thenReturn(t);

        User user = TestDataFactory.validUser(5L);
        user.setEmail("ana@example.com");
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(user));

        useCase.register(10L, "ana@example.com", null);
        verify(notificationPort).notifyUsersOfTournament(eq(10L), any(), any(), eq(NotificationType.TOURNAMENT_FULL));
    }
}

