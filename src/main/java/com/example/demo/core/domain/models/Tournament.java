package com.example.demo.core.domain.models;

import java.util.Date;
import java.util.List;

import com.example.demo.adapters.out.persistence.jpa.entities.DisciplineEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tournament { // Torneo

    private Long id;
    private List<Team> teams; // equipos
    private int teamsInscribed; // equiposInscritos
    private Discipline discipline; // disciplina
    private Format format; // formato (clase padre de los distintos formats)
    private String name; // nombre
    private Date createdAt; // fechaCreacion
    private Date endAt; // fechaFin
    private Date startAt; // fechaInicio
    private boolean privateTournament; // esPrivado
    private String password; // password
    private int minParticipantsPerTeam; // cantMinParticipantesXEquipo
    private int maxParticipantsPerTeam; // cantMaxParticipantesXEquipo
    private Date registrationDeadline; // fechaLimiteInscripciones
    private String prize; // premio
    private double registrationCost; // costoInscripcion
    private User organizer; // organizador
    private int minParticipantsPerTournament; // cantMinParticipantesXTorneo
    private int maxParticipantsPerTournament; // cantMaxParticipantesXTorneo
    private TournamentStatus status; // estado
    private Boolean isDoubleRound; // Para formato liga: indica si es a doble ronda (ida y vuelta)
    private String detalles; // detalles del torneo con toda la información
    // Factory principal para crear torneos sanos

    public static Tournament create(Tournament t) {
        t.createdAt = new Date();
        t.status = TournamentStatus.ABIERTO;
        t.validate();
        return t;
    }

    // Reglas de dominio
    public void validate() {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name es requerido");
        }

        // Equipo: min/max > 0
        if (minParticipantsPerTeam <= 0 || maxParticipantsPerTeam <= 0) {
            throw new IllegalArgumentException("min/maxParticipantsPerTeam deben ser > 0");
        }

        if (minParticipantsPerTeam > maxParticipantsPerTeam) {
            throw new IllegalArgumentException("minParticipantsPerTeam > maxParticipantsPerTeam");
        }

        if (privateTournament && (password == null || password.isBlank())) {
            throw new IllegalArgumentException("password requerido para torneo privado");
        }

        // Fechas
        if (startAt != null && endAt != null && endAt.before(startAt)) {
            throw new IllegalArgumentException("endAt < startAt");
        }

        if (startAt != null && registrationDeadline != null && registrationDeadline.after(startAt)) {
            throw new IllegalArgumentException("registration_deadline > startAt");
        }

        // Min/max torneo
        if (minParticipantsPerTournament > 0 && maxParticipantsPerTournament > 0
                && minParticipantsPerTournament > maxParticipantsPerTournament) {

            throw new IllegalArgumentException("minParticipantsPerTournament > maxParticipantsPerTournament");
        }
    }

public boolean isParticipant(Long userId) {
    if (this.teams != null && !this.teams.isEmpty()) {
        for (Team team : teams) {
            if (team.hasParticipated(userId)) {
                return true;
            }
        }
        return false; // no estuvo en ningún equipo
    }
    return false; // no hay equipos
}

/**
 * Verifica si una persona con el nationalId dado participa en este torneo
 * @param nationalId Cédula de identidad del participante
 * @return true si participa en algún equipo, false en caso contrario
 */
public boolean isParticipantByNationalId(String nationalId) {
    if (nationalId == null || nationalId.isBlank()) {
        return false;
    }
    if (this.teams != null && !this.teams.isEmpty()) {
        for (Team team : teams) {
            if (team.hasParticipantWithNationalId(nationalId)) {
                return true;
            }
        }
    }
    return false;
}

}
