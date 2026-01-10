package com.example.demo.adapters.in.api.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.demo.adapters.in.api.dto.CreateTournamentRequest;
import com.example.demo.core.domain.models.SimpleFormat;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.User;

class TournamentMapperTest {

    @Test
    void toDomain_mapsInputRequestFields() {
        Date startAt = new Date(System.currentTimeMillis() + 100000);
        Date endAt = new Date(System.currentTimeMillis() + 200000);
        Date registrationDeadline = new Date(System.currentTimeMillis() + 50000);

        CreateTournamentRequest request = new CreateTournamentRequest(
                1L,
                2L,
                "Torneo A",
                startAt,
                endAt,
                registrationDeadline,
                true,
                "secret",
                1,
                3,
                0,
                0,
                "Premio",
                0.0,
                true,
                "Detalles");

        Tournament domain = TournamentMapper.toDomain(request);
        assertThat(domain.getName()).isEqualTo("Torneo A");
        assertThat(domain.getDiscipline().getId()).isEqualTo(1L);
        assertThat(domain.getFormat()).isInstanceOf(SimpleFormat.class);
        assertThat(domain.getFormat().getId()).isEqualTo(2L);
        assertThat(domain.isPrivateTournament()).isTrue();
        assertThat(domain.getPassword()).isEqualTo("secret");
        assertThat(domain.getDetalles()).isEqualTo("Detalles");
    }

    @Test
    void toResponse_mapsFieldsWhenOrganizerAndStatusPresent() {
        Tournament t = new Tournament();
        t.setId(10L);
        t.setName("Torneo");
        t.setFormat(new SimpleFormat(2L, "Liga", true));
        t.setDiscipline(null);
        t.setCreatedAt(new Date(0));
        t.setStartAt(new Date(1));
        t.setEndAt(new Date(2));
        t.setRegistrationDeadline(new Date(3));
        t.setPrivateTournament(false);
        t.setPrize("Premio");
        t.setRegistrationCost(0.0);
        t.setMinParticipantsPerTeam(1);
        t.setMaxParticipantsPerTeam(2);
        t.setMinParticipantsPerTournament(0);
        t.setMaxParticipantsPerTournament(0);
        t.setTeamsInscribed(5);
        User organizer = new User();
        organizer.setId(99L);
        t.setOrganizer(organizer);
        t.setStatus(TournamentStatus.ABIERTO);
        t.setIsDoubleRound(false);
        t.setPassword(null);
        t.setDetalles("D");
        t.setTeams(List.of());

        var dto = TournamentMapper.toResponse(t);
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.organizerId()).isEqualTo(99L);
        assertThat(dto.status()).isEqualTo("ABIERTO");
        assertThat(dto.detalles()).isEqualTo("D");
    }
}
