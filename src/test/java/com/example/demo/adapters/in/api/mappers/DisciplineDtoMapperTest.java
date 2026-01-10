package com.example.demo.adapters.in.api.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.example.demo.core.domain.models.Discipline;

class DisciplineDtoMapperTest {

    @Test
    void toResponse_returnsNullWhenNullInput() {
        assertThat(DisciplineDtoMapper.toResponse(null)).isNull();
    }

    @Test
    void toResponse_mapsFields() {
        Discipline d = new Discipline(1L, true, "Futbol", null);
        var dto = DisciplineDtoMapper.toResponse(d);
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Futbol");
        assertThat(dto.collective()).isTrue();
    }
}

