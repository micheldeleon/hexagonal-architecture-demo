package com.example.demo.testsupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.demo.core.domain.models.Department;
import com.example.demo.core.domain.models.Discipline;
import com.example.demo.core.domain.models.Participant;
import com.example.demo.core.domain.models.Team;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.domain.models.Formats.EliminationFormat;
import com.example.demo.core.domain.models.Formats.LeagueFormat;
import com.example.demo.core.domain.models.Formats.RaceFormat;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static User validUser(Long id) {
        User user = new User(id, "Ana", "Pérez", "ana@example.com", "password123");
        user.setNationalId(validUruguayanId("1234567"));
        user.setDateOfBirth(new Date(0));
        user.setPhoneNumber("+598 91 234 567");
        user.setAddress("Calle 123");
        user.setDepartment(new Department(1L));
        return user;
    }

    public static User organizerUser(Long id) {
        return validUser(id);
    }

    public static Tournament baseTournament(Long id, Long organizerId) {
        Tournament t = new Tournament();
        t.setId(id);
        t.setName("Torneo Test");
        t.setMinParticipantsPerTeam(1);
        t.setMaxParticipantsPerTeam(5);
        t.setMinParticipantsPerTournament(0);
        t.setMaxParticipantsPerTournament(0);
        t.setTeamsInscribed(0);
        t.setPrivateTournament(false);
        t.setStartAt(new Date(System.currentTimeMillis() + 86400000L));
        t.setEndAt(new Date(System.currentTimeMillis() + 2 * 86400000L));
        t.setRegistrationDeadline(new Date(System.currentTimeMillis() + 3600000L));
        t.setDiscipline(new Discipline(1L, true, "Futbol", null));
        User org = validUser(organizerId);
        t.setOrganizer(org);
        t.setTeams(new ArrayList<>());
        t.setStatus(TournamentStatus.ABIERTO);
        return t;
    }

    public static Tournament startedEliminationTournament(Long tournamentId, Long organizerId) {
        Tournament t = baseTournament(tournamentId, organizerId);
        EliminationFormat format = new EliminationFormat();
        format.setId(10L);
        format.setName("Eliminatorio");
        format.setGeneraFixture(true);
        t.setFormat(format);
        t.setStatus(TournamentStatus.INICIADO);
        return t;
    }

    public static Tournament startedLeagueTournament(Long tournamentId, Long organizerId) {
        Tournament t = baseTournament(tournamentId, organizerId);
        LeagueFormat format = new LeagueFormat(3, 1, 0, true);
        format.setId(11L);
        format.setName("Liga");
        format.setGeneraFixture(true);
        t.setFormat(format);
        t.setStatus(TournamentStatus.INICIADO);
        return t;
    }

    public static Tournament startedRaceTournament(Long tournamentId, Long organizerId) {
        Tournament t = baseTournament(tournamentId, organizerId);
        RaceFormat format = new RaceFormat();
        format.setId(12L);
        format.setName("Carrera");
        format.setGeneraFixture(false);
        t.setFormat(format);
        t.setStatus(TournamentStatus.INICIADO);
        return t;
    }

    public static Team teamWithParticipants(Long teamId, Long... participantUserIds) {
        Team team = new Team();
        team.setId(teamId);
        team.setName("Equipo " + teamId);
        List<Participant> participants = new ArrayList<>();
        for (Long participantUserId : participantUserIds) {
            Participant p = new Participant();
            p.setId(participantUserId);
            p.setNationalId(validUruguayanId("7654321"));
            participants.add(p);
        }
        team.setParticipants(participants);
        return team;
    }

    public static String validUruguayanId(String first7Digits) {
        String base = first7Digits.replaceAll("\\D", "");
        if (base.length() != 7) {
            throw new IllegalArgumentException("first7Digits must have 7 digits");
        }
        int[] multipliers = { 2, 9, 8, 7, 6, 3, 4 };
        int sum = 0;
        for (int i = 0; i < 7; i++) {
            sum += Character.getNumericValue(base.charAt(i)) * multipliers[i];
        }
        int remainder = sum % 10;
        int checkDigit = remainder == 0 ? 0 : 10 - remainder;
        return base + checkDigit;
    }
}

