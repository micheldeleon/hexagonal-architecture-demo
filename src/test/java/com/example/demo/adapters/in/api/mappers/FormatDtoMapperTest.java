package com.example.demo.adapters.in.api.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.example.demo.core.domain.models.SimpleFormat;

class FormatDtoMapperTest {

    @Test
    void toResponse_returnsNullWhenNullInput() {
        assertThat(FormatDtoMapper.toResponse(null)).isNull();
    }

    @Test
    void toResponse_mapsFields() {
        SimpleFormat f = new SimpleFormat(2L, "Liga", true);
        var dto = FormatDtoMapper.toResponse(f);
        assertThat(dto.id()).isEqualTo(2L);
        assertThat(dto.name()).isEqualTo("Liga");
        assertThat(dto.generaFixture()).isTrue();
    }
}

