package com.example.demo.core.ports.out;

import java.util.List;

import com.example.demo.core.domain.models.Tournament;

public interface TournamentRepositoryPort {
    Tournament save(Tournament tournament, Long organizerId);

    Tournament update(Tournament tournament);

    Tournament findById(Long id);

    List<Tournament> findAll();
    
    /**
     * Obtiene un torneo por ID con sus equipos y participantes cargados
     * @param id ID del torneo
     * @return Tournament con equipos cargados
     */
    Tournament findByIdWithTeams(Long id);
    
    /**
     * Obtiene todos los torneos con sus equipos y participantes cargados
     * @return Lista de torneos con equipos cargados
     */
    List<Tournament> findAllWithTeams();
    
    /**
     * Obtiene los últimos 3 torneos creados ordenados por fecha de creación descendente
     * @return Lista con los últimos 3 torneos creados
     */
    List<Tournament> findLatest3();
}
