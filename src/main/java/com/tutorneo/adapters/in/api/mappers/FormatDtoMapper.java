package com.tutorneo.adapters.in.api.mappers;

import com.tutorneo.adapters.in.api.dto.FormatResponse;
import com.tutorneo.core.domain.models.Format;

public class FormatDtoMapper {

    private FormatDtoMapper() {
    }

    public static FormatResponse toResponse(Format format) {
        if (format == null) {
            return null;
        }
        return new FormatResponse(format.getId(), format.getName(), format.isGeneraFixture());
    }
}
