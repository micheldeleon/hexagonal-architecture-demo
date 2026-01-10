package com.example.demo.core.domain.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.demo.testsupport.TestDataFactory;

class TournamentTest {

    @Test
    void create_setsCreatedAtAndStatusAndValidates() {
        Tournament t = TestDataFactory.baseTournament(null, 1L);
        t.setStatus(null);
        t.setCreatedAt(null);

        Tournament created = Tournament.create(t);
        assertThat(created.getCreatedAt()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(TournamentStatus.ABIERTO);
    }

    @Test
    void validate_rejectsMissingName() {
        Tournament t = TestDataFactory.baseTournament(1L, 1L);
        t.setName(" ");
        assertThatThrownBy(t::validate).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("name");
    }

    @Test
    void validate_rejectsMinMaxParticipantsPerTeamRules() {
        Tournament t = TestDataFactory.baseTournament(1L, 1L);
        t.setMinParticipantsPerTeam(0);
        t.setMaxParticipantsPerTeam(1);
        assertThatThrownBy(t::validate).isInstanceOf(IllegalArgumentException.class);

        t.setMinParticipantsPerTeam(5);
        t.setMaxParticipantsPerTeam(2);
        assertThatThrownBy(t::validate).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("minParticipantsPerTeam");
    }

    @Test
    void validate_rejectsPrivateTournamentWithoutPassword() {
        Tournament t = TestDataFactory.baseTournament(1L, 1L);
        t.setPrivateTournament(true);
        t.setPassword(" ");
        assertThatThrownBy(t::validate).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("password");
    }

    @Test
    void validate_rejectsEndBeforeStartAndRegistrationDeadlineAfterStart() {
        Tournament t = TestDataFactory.baseTournament(1L, 1L);
        Date start = new Date(System.currentTimeMillis() + 100000);
        Date end = new Date(System.currentTimeMillis() + 50000);
        t.setStartAt(start);
        t.setEndAt(end);
        assertThatThrownBy(t::validate).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("endAt < startAt");

        t.setEndAt(new Date(System.currentTimeMillis() + 200000));
        t.setRegistrationDeadline(new Date(System.currentTimeMillis() + 150000));
        assertThatThrownBy(t::validate).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("registration_deadline");
    }

    @Test
    void isParticipantByNationalId_checksAcrossTeams() {
        Participant p = new Participant();
        p.setNationalId("12345678");
        Team team = new Team(1L, "A", List.of(p), null, null);
        Tournament t = new Tournament();
        t.setTeams(List.of(team));
        assertThat(t.isParticipantByNationalId("12345678")).isTrue();
        assertThat(t.isParticipantByNationalId("00000000")).isFalse();
        assertThat(t.isParticipantByNationalId(null)).isFalse();
    }
}

