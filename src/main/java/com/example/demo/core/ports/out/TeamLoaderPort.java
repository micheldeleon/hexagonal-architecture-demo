package com.example.demo.core.ports.out;

import java.util.List;
import java.util.Map;

import com.example.demo.core.domain.models.Team;

/**
 * Puerto de salida para cargar equipos con sus participantes
 */
public interface TeamLoaderPort {
    /**
     * Carga todos los equipos de un torneo con sus participantes
     * @param tournamentId ID del torneo
     * @return Lista de equipos con participantes cargados
     */
    List<Team> loadTeamsByTournament(Long tournamentId);
    
    /**
     * Carga equipos para múltiples torneos en una sola consulta (batch loading)
     * @param tournamentIds Lista de IDs de torneos
     * @return Mapa de tournamentId -> Lista de equipos
     */
    Map<Long, List<Team>> loadTeamsForMultipleTournaments(List<Long> tournamentIds);
}
