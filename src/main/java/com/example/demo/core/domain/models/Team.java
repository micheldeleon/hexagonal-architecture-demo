package com.example.demo.core.domain.models;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team { // Equipo

    private Long id;
    private String name; // nombre del equipo
    private List<Participant> participants; // participantes
    private User creator; // creador
    //Fecha de creacion 
    private LocalDateTime createdAt;

    public boolean hasParticipated(Long userId) {
        if (this.participants != null && !this.participants.isEmpty()) {
            for (Participant participant : participants) {
                if (participant.getId().equals(userId)) {
                    return true;
                }
            }
            return false;
        } else {
            throw new IllegalArgumentException("No hay participantes");
        }
    }
    
    /**
     * Verifica si una persona con el nationalId dado participa en este equipo
     * @param nationalId Cédula de identidad del participante
     * @return true si participa, false en caso contrario
     */
    public boolean hasParticipantWithNationalId(String nationalId) {
        if (nationalId == null || nationalId.isBlank()) {
            return false;
        }
        if (this.participants != null && !this.participants.isEmpty()) {
            for (Participant participant : participants) {
                if (nationalId.equals(participant.getNationalId())) {
                    return true;
                }
            }
        }
        return false;
    }
    //Si vemos mas datos que pueden llegar a ir
}
