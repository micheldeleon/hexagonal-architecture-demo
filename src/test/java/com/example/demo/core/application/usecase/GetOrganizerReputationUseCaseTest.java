package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.Organizer;
import com.example.demo.core.domain.models.Reputation;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.GetOrganizerReputationPort.OrganizerReputationResult;
import com.example.demo.core.ports.out.ReputationRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class GetOrganizerReputationUseCaseTest {

    @Mock
    private ReputationRepositoryPort reputationRepositoryPort;
    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Test
    void getReputation_validatesOrganizerIdAndExistence() {
        GetOrganizerReputationUseCase useCase = new GetOrganizerReputationUseCase(reputationRepositoryPort,
                userRepositoryPort);
        assertThatThrownBy(() -> useCase.getReputation(null)).isInstanceOf(IllegalArgumentException.class);
        when(userRepositoryPort.findById(10L)).thenReturn(null);
        assertThatThrownBy(() -> useCase.getReputation(10L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getReputation_mapsDistributionAndRecentRatings() {
        GetOrganizerReputationUseCase useCase = new GetOrganizerReputationUseCase(reputationRepositoryPort,
                userRepositoryPort);

        User organizer = new Organizer();
        organizer.setId(10L);
        organizer.setName("Org");
        organizer.setLastName("One");
        when(userRepositoryPort.findById(10L)).thenReturn(organizer);

        when(reputationRepositoryPort.getAverageScore(10L)).thenReturn(4.0);
        when(reputationRepositoryPort.countRatingsByOrganizer(10L)).thenReturn(3);
        when(reputationRepositoryPort.getDistribution(10L)).thenReturn(new ReputationRepositoryPort.RatingDistribution(2, 1, 0, 0, 0));

        User rater = TestDataFactory.validUser(1L);
        Tournament tournament = new Tournament();
        tournament.setId(99L);
        tournament.setName("Torneo");
        Reputation rep = new Reputation(rater, null, tournament, 5, "ok");
        rep.setCreatedAt(new Date(0));

        when(reputationRepositoryPort.findRecentByOrganizerId(10L, 10)).thenReturn(List.of(rep));

        OrganizerReputationResult result = useCase.getReputation(10L);
        assertThat(result.organizerId()).isEqualTo(10L);
        assertThat(result.organizerName()).isEqualTo("Org One");
        assertThat(result.averageScore()).isEqualTo(4.0);
        assertThat(result.totalRatings()).isEqualTo(3);
        assertThat(result.distribution().fiveStars()).isEqualTo(2);
        assertThat(result.recentRatings()).hasSize(1);
        assertThat(result.recentRatings().get(0).tournamentName()).isEqualTo("Torneo");
        assertThat(result.recentRatings().get(0).createdAt()).isNotBlank();
    }
}

