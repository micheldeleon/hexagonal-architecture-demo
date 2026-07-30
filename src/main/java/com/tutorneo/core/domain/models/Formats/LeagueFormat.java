package com.tutorneo.core.domain.models.Formats;

import com.tutorneo.core.domain.models.Format;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeagueFormat extends Format { // Liga
    private int winPoints;   // puntosVictoria
    private int drawPoints;  // puntosEmpate
    private int lossPoints;  // puntosDerrota
    private boolean hasTiebreak; // tieneEmpate
}

