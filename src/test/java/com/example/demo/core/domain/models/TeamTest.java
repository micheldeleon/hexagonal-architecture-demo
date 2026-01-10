package com.example.demo.core.domain.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

class TeamTest {

    @Test
    void hasParticipated_returnsTrueWhenParticipantIdMatches() {
        Participant p1 = new Participant();
        p1.setId(10L);
        Participant p2 = new Participant();
        p2.setId(20L);

        Team team = new Team(1L, "Equipo", List.of(p1, p2), null, null);
        assertThat(team.hasParticipated(20L)).isTrue();
        assertThat(team.hasParticipated(30L)).isFalse();
    }

    @Test
    void hasParticipated_throwsWhenNoParticipants() {
        Team team = new Team(1L, "Equipo", null, null, null);
        assertThatThrownBy(() -> team.hasParticipated(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No hay participantes");
    }

    @Test
    void hasParticipantWithNationalId_handlesNullAndBlank() {
        Team team = new Team();
        team.setParticipants(List.of());
        assertThat(team.hasParticipantWithNationalId(null)).isFalse();
        assertThat(team.hasParticipantWithNationalId(" ")).isFalse();
    }

    @Test
    void hasParticipantWithNationalId_returnsTrueWhenFound() {
        Participant p = new Participant();
        p.setNationalId("12345678");
        Team team = new Team(1L, "Equipo", List.of(p), null, null);
        assertThat(team.hasParticipantWithNationalId("12345678")).isTrue();
        assertThat(team.hasParticipantWithNationalId("00000000")).isFalse();
    }
}

