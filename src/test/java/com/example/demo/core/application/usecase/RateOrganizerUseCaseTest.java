package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.Participant;
import com.example.demo.core.domain.models.Team;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.domain.models.Reputation;
import com.example.demo.core.ports.in.RateOrganizerPort.RateOrganizerResult;
import com.example.demo.core.ports.out.ReputationRepositoryPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class RateOrganizerUseCaseTest {

    @Mock
    private TournamentRepositoryPort tournamentRepositoryPort;
    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private ReputationRepositoryPort reputationRepositoryPort;

    @Test
    void rate_validatesInputsAndScoreAndSelfRating() {
        RateOrganizerUseCase useCase = new RateOrganizerUseCase(tournamentRepositoryPort, userRepositoryPort,
                reputationRepositoryPort);

        assertThatThrownBy(() -> useCase.rate(null, 1L, 1L, 5, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.rate(1L, null, 1L, 5, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.rate(1L, 2L, null, 5, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.rate(1L, 1L, 1L, 5, null)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> useCase.rate(1L, 2L, 1L, 0, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.rate(1L, 2L, 1L, 6, null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rate_success_savesReputationAndReturnsUpdatedStats() {
        RateOrganizerUseCase useCase = new RateOrganizerUseCase(tournamentRepositoryPort, userRepositoryPort,
                reputationRepositoryPort);

        User user = TestDataFactory.validUser(10L);
        user.setNationalId(TestDataFactory.validUruguayanId("1234567"));
        when(userRepositoryPort.findById(10L)).thenReturn(user);

        User organizerUser = TestDataFactory.validUser(20L);
        when(userRepositoryPort.findById(20L)).thenReturn(organizerUser);

        Participant participant = new Participant();
        participant.setNationalId(user.getNationalId());
        Team team = new Team();
        team.setId(1L);
        team.setParticipants(List.of(participant));

        Tournament tournament = TestDataFactory.baseTournament(30L, 20L);
        tournament.setStatus(TournamentStatus.FINALIZADO);
        tournament.setTeams(List.of(team));
        when(tournamentRepositoryPort.findByIdWithTeams(30L)).thenReturn(tournament);

        when(reputationRepositoryPort.hasUserRatedOrganizerInTournament(10L, 20L, 30L)).thenReturn(false);

        Reputation saved = new Reputation(user, null, tournament, 5, "Excelente");
        saved.setId(99L);
        when(reputationRepositoryPort.save(any(Reputation.class))).thenReturn(saved);
        when(reputationRepositoryPort.getAverageScore(20L)).thenReturn(4.5);
        when(reputationRepositoryPort.countRatingsByOrganizer(20L)).thenReturn(2);

        RateOrganizerResult result = useCase.rate(10L, 20L, 30L, 5, "Excelente");
        assertThat(result.reputationId()).isEqualTo(99L);
        assertThat(result.organizerId()).isEqualTo(20L);
        assertThat(result.score()).isEqualTo(5);
        assertThat(result.newAverage()).isEqualTo(4.5);
        assertThat(result.totalRatings()).isEqualTo(2);

        ArgumentCaptor<Reputation> repCaptor = ArgumentCaptor.forClass(Reputation.class);
        verify(reputationRepositoryPort).save(repCaptor.capture());
        assertThat(repCaptor.getValue().getScore()).isEqualTo(5);
        assertThat(repCaptor.getValue().getUser().getId()).isEqualTo(10L);
        assertThat(repCaptor.getValue().getTournament().getId()).isEqualTo(30L);
    }
}

