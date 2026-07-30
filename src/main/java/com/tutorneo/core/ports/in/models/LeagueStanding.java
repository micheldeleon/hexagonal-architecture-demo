package com.tutorneo.core.ports.in.models;

public record LeagueStanding(
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
