package com.tutorneo.adapters.in.api.dto;

public record LeagueStandingResponse(
        Long teamId,
        String teamName,
        int played,
        int won,
        int draw,
        int lost,
        int goalsFor,
        int goalsAgainst,
        int goalDifference,
        int points) {
}
