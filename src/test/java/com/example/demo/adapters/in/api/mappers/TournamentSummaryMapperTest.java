package com.example.demo.adapters.in.api.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.example.demo.core.domain.models.Discipline;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;

class TournamentSummaryMapperTest {

    @Test
    void toResponse_returnsNullWhenNullInput() {
        assertThat(TournamentSummaryMapper.toResponse(null)).isNull();
    }

    @Test
    void toResponse_mapsFields() {
        Tournament t = new Tournament();
        t.setId(1L);
        t.setName("Torneo");
        t.setDiscipline(new Discipline(5L, true, "Futbol", null));
        t.setPrivateTournament(true);
        t.setStatus(TournamentStatus.ABIERTO);

        var dto = TournamentSummaryMapper.toResponse(t);
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.disciplineId()).isEqualTo(5L);
        assertThat(dto.status()).isEqualTo(TournamentStatus.ABIERTO);
    }
}

