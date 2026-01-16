package com.example.demo.adapters.in.api.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar un torneo existente.
 * Solo se pueden modificar ciertos campos y con restricciones según el estado del torneo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTournamentRequest {
    
    private String name;
    private Date startAt;
    private Date endAt;
    private Date registrationDeadline;
    private String detalles;
    private String imageUrl;
    private Boolean privateTournament;
    private String password;
    private Integer minParticipantsPerTournament;
    private Integer maxParticipantsPerTournament;
    
    // Campos que solo se pueden modificar si NO hay equipos inscritos
    private Double registrationCost;
    private String prize;
}
