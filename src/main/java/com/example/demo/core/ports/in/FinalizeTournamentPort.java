package com.example.demo.core.ports.in;

public interface FinalizeTournamentPort {
    /**
     * Finaliza un torneo validando que todos los partidos tengan resultado
     * y en caso de carrera que los resultados hayan sido establecidos.
     * 
     * @param tournamentId ID del torneo a finalizar
     * @throws IllegalArgumentException si el torneo no existe
     * @throws IllegalStateException si el torneo no puede finalizarse (no está iniciado,
     *         o faltan resultados)
     */
    void finalizeTournament(Long tournamentId);
}
