package com.example.demo.core.domain.models;

public enum NotificationType {
    TOURNAMENT_CANCELED("Torneo cancelado"),
    TEAM_REMOVED("Equipo eliminado del torneo"),
    TOURNAMENT_STARTED("Torneo iniciado"),
    MATCH_SCHEDULED("Partido programado"),
    MATCH_RESULT("Resultado de partido"),
    REGISTRATION_CONFIRMED("Inscripción confirmada"),
    TOURNAMENT_FULL("Torneo lleno"),
    TOURNAMENT_REMINDER("Recordatorio de torneo"),
    GENERAL("Notificación general");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
