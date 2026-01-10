package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.adapters.in.api.dto.ParticipantRequest;
import com.example.demo.core.domain.models.Formats.RaceFormat;
import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.ports.in.CreateNotificationPort;
import com.example.demo.core.ports.out.NotificationPort;
import com.example.demo.core.ports.out.TeamRegistrationPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class RegisterTeamToTournamentUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;
    @Mock
    private TeamRegistrationPort teamRegistrationPort;
    @Mock
    private NotificationPort notificationPort;
    @Mock
    private CreateNotificationPort createNotificationPort;

    @Test
    void registerTeam_validatesParticipants() {
        RegisterTeamToTournamentUseCase useCase = new RegisterTeamToTournamentUseCase(tournamentRepositoryPort,
                teamRegistrationPort, notificationPort, createNotificationPort);

        assertThatThrownBy(() -> useCase.registerTeam(1L, 2L, "A", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.registerTeam(1L, 2L, "A", List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void registerTeam_rejectsRaceFormatWithMoreThanOneParticipant() {
        RegisterTeamToTournamentUseCase useCase = new RegisterTeamToTournamentUseCase(tournamentRepositoryPort,
                teamRegistrationPort, notificationPort, createNotificationPort);

        Tournament t = TestDataFactory.baseTournament(10L, 1L);
        t.setFormat(new RaceFormat());
        t.setStatus(TournamentStatus.ABIERTO);
        t.setRegistrationDeadline(new Date(System.currentTimeMillis() + 100000));
        when(tournamentRepositoryPort.findById(10L)).thenReturn(t);

        List<ParticipantRequest> participants = List.of(
                new ParticipantRequest("A", "12345678"),
                new ParticipantRequest("B", "87654321"));

        assertThatThrownBy(() -> useCase.registerTeam(10L, 2L, "Equipo", participants))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("carrera");
    }

    @Test
    void registerTeam_success_registersTeamAndNotifiesCaptain() {
        RegisterTeamToTournamentUseCase useCase = new RegisterTeamToTournamentUseCase(tournamentRepositoryPort,
                teamRegistrationPort, notificationPort, createNotificationPort);

        Tournament t = TestDataFactory.baseTournament(10L, 1L);
        t.setStatus(TournamentStatus.ABIERTO);
        t.setRegistrationDeadline(new Date(System.currentTimeMillis() + 100000));
        when(tournamentRepositoryPort.findById(10L)).thenReturn(t);

        List<ParticipantRequest> participants = List.of(new ParticipantRequest("A", "12345678"));
        useCase.registerTeam(10L, 2L, "Equipo", participants);

        verify(teamRegistrationPort).registerTeam(eq(10L), eq(2L), eq("Equipo"), eq(1L), eq(participants));
        verify(tournamentRepositoryPort).save(any(Tournament.class), eq(1L));
        verify(createNotificationPort).createNotification(eq(2L), eq(NotificationType.REGISTRATION_CONFIRMED), any(),
                any(), eq(10L));
    }
}

