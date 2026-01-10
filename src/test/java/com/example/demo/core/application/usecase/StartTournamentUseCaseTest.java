package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.Formats.EliminationFormat;
import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.StartTournamentPort.StartTournamentResult;
import com.example.demo.core.ports.out.NotificationPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class StartTournamentUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;
    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private NotificationPort notificationPort;

    @Test
    void start_validatesInputs() {
        StartTournamentUseCase useCase = new StartTournamentUseCase(tournamentRepositoryPort, userRepositoryPort,
                notificationPort);
        assertThatThrownBy(() -> useCase.start(null, "ana@example.com")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.start(1L, " ")).isInstanceOf(SecurityException.class);
    }

    @Test
    void start_rejectsWhenNotOrganizerOrWrongStatusOrMinParticipantsNotMet() {
        StartTournamentUseCase useCase = new StartTournamentUseCase(tournamentRepositoryPort, userRepositoryPort,
                notificationPort);

        User organizer = TestDataFactory.validUser(1L);
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(organizer));

        Tournament tournament = TestDataFactory.baseTournament(10L, 2L);
        tournament.setStatus(TournamentStatus.ABIERTO);
        tournament.setTeamsInscribed(0);
        tournament.setMinParticipantsPerTournament(2);
        tournament.setStartAt(new Date(System.currentTimeMillis() - 1000));
        tournament.setFormat(new EliminationFormat());
        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        assertThatThrownBy(() -> useCase.start(10L, "ana@example.com")).isInstanceOf(SecurityException.class);

        tournament.getOrganizer().setId(1L);
        assertThatThrownBy(() -> useCase.start(10L, "ana@example.com")).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("al menos");

        tournament.setMinParticipantsPerTournament(0);
        tournament.setStatus(TournamentStatus.INICIADO);
        assertThatThrownBy(() -> useCase.start(10L, "ana@example.com")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void start_rejectsStartingBeforeStartDateWhenNotFullAndBeforeDeadline() {
        StartTournamentUseCase useCase = new StartTournamentUseCase(tournamentRepositoryPort, userRepositoryPort,
                notificationPort);

        User organizer = TestDataFactory.validUser(1L);
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(organizer));

        Tournament tournament = TestDataFactory.baseTournament(10L, 1L);
        tournament.setStatus(TournamentStatus.ABIERTO);
        tournament.setTeamsInscribed(1);
        tournament.setMaxParticipantsPerTournament(10);
        tournament.setStartAt(new Date(System.currentTimeMillis() + 3600000L));
        tournament.setRegistrationDeadline(new Date(System.currentTimeMillis() + 1800000L));
        tournament.setFormat(new EliminationFormat());

        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        assertThatThrownBy(() -> useCase.start(10L, "ana@example.com"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("solo puede iniciarse");
    }

    @Test
    void start_success_setsStatusAndNotifies() {
        StartTournamentUseCase useCase = new StartTournamentUseCase(tournamentRepositoryPort, userRepositoryPort,
                notificationPort);

        User organizer = TestDataFactory.validUser(1L);
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(organizer));

        Tournament tournament = TestDataFactory.baseTournament(10L, 1L);
        tournament.setStatus(TournamentStatus.ABIERTO);
        tournament.setTeamsInscribed(3);
        tournament.setStartAt(new Date(System.currentTimeMillis() - 1000));
        tournament.setRegistrationDeadline(new Date(System.currentTimeMillis() - 2000));
        tournament.setFormat(new EliminationFormat());

        when(tournamentRepositoryPort.findById(10L)).thenReturn(tournament);

        StartTournamentResult result = useCase.start(10L, "ana@example.com");
        assertThat(result.tournamentId()).isEqualTo(10L);
        assertThat(result.startedByName()).isEqualTo(organizer.getName());
        assertThat(result.participantsCount()).isEqualTo(3);

        ArgumentCaptor<Tournament> updatedCaptor = ArgumentCaptor.forClass(Tournament.class);
        verify(tournamentRepositoryPort).update(updatedCaptor.capture());
        assertThat(updatedCaptor.getValue().getStatus()).isEqualTo(TournamentStatus.INICIADO);

        verify(notificationPort).notifyUsersOfTournament(eq(10L), any(), any(), eq(NotificationType.TOURNAMENT_STARTED));
    }
}

