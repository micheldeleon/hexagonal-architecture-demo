package com.tutorneo.core.domain.models;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Round { // Jornada
    private Long id;
    private Fixture fixture;
    private Integer roundNumber;
    private String name;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private List<Match> matches; // duelos
}
