package com.tutorneo.core.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public abstract class Format {

    private Long id;
    private String name;
    private boolean generaFixture;
}
