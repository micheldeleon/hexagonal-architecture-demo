package com.tutorneo.core.domain.models;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fixture { // Fixture
    private Long id;
    private List<Round> rounds; // jornadas
    private Tournament tournament;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
