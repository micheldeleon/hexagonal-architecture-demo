package com.tutorneo.core.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant { // Participante
    private Long id;
    private String nationalId; // ci
    private String fullName;   // nombreCompleto
}

