package com.example.demo.core.domain.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class OrganizerTest {

    @Test
    void getReputationAverage_returns0WhenNullOrEmpty() {
        Organizer organizer = new Organizer();
        organizer.setReputation(null);
        assertThat(organizer.getReputationAverage()).isEqualTo(0.0);

        organizer.setReputation(List.of());
        assertThat(organizer.getReputationAverage()).isEqualTo(0.0);
    }

    @Test
    void getReputationAverage_ignoresNullEntries() {
        Reputation r1 = new Reputation();
        r1.setScore(5);
        Reputation r2 = new Reputation();
        r2.setScore(1);

        Organizer organizer = new Organizer();
        organizer.setReputation(java.util.Arrays.asList(r1, null, r2));

        assertThat(organizer.getReputationAverage()).isEqualTo(3.0);
    }
}
