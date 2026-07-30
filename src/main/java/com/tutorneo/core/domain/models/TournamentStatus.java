package com.tutorneo.core.domain.models;

/**
 * Representa el estado de un torneo. Se modela como enum para facilitar
 * validaciones y evitar valores libres.
 */
public enum TournamentStatus {
    ABIERTO, //por defecto estado al crearse
    INICIADO, // estado cuando el torneo ha comenzado
    FINALIZADO, // estado cuando el torneo ha terminado
    CANCELADO // estado cuando el torneo ha sido cancelado
}
