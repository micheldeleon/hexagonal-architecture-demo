package com.tutorneo.core.domain.models;

import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organizer extends User {
    private List<Reputation> reputation;

    // Calcula el promedio a partir de la lista de Reputation.
    public double getReputationAverage() {
        if (reputation == null || reputation.isEmpty()) return 0.0;
        return reputation.stream()
                .filter(Objects::nonNull)
                .mapToDouble(r -> r.getScore())
                .average()
                .orElse(0.0);
    }
}



