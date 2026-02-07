package com.example.demo.core.domain.models;

public enum NotificationType {
    WELCOME("Bienvenida"),
    TOURNAMENT_CANCELED("Torneo cancelado"),
    TOURNAMENT_UPDATED("Torneo actualizado"),
    TEAM_REMOVED("Equipo eliminado del torneo"),
    TOURNAMENT_STARTED("Torneo iniciado"),
    TOURNAMENT_FINALIZED("Torneo finalizado"),
    MATCH_SCHEDULED("Partido programado"),
    MATCH_RESULT("Resultado de partido"),
    REGISTRATION_CONFIRMED("Inscripción confirmada"),
    TOURNAMENT_FULL("Torneo lleno"),
    TOURNAMENT_REMINDER("Recordatorio de torneo"),
    TOURNAMENT_DEACTIVATED_BY_ADMIN("Torneo dado de baja por moderación"),
    TOURNAMENT_REACTIVATED_BY_ADMIN("Torneo reactivado por moderación"),
    GENERAL("Notificación general"),
    // Nuevos tipos para el blog
    NUEVO_COMENTARIO_POST("Nuevo comentario en tu publicación"),
    RESPUESTA_COMENTARIO("Alguien respondió tu comentario"),
    CONTACTO_AVISO("Interés en tu aviso");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
