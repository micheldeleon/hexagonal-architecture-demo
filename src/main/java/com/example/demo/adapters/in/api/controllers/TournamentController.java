package com.example.demo.adapters.in.api.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adapters.in.api.dto.CreateTournamentRequest;
import com.example.demo.adapters.in.api.dto.RegisterToTournamentRequest;
import com.example.demo.adapters.in.api.dto.RunnerRegistrationRequest;
import com.example.demo.adapters.in.api.dto.TournamentResponse;
import com.example.demo.adapters.in.api.dto.TournamentSummaryResponse;
import com.example.demo.adapters.in.api.mappers.TournamentMapper;
import com.example.demo.adapters.in.api.mappers.TournamentSummaryMapper;
import com.example.demo.adapters.in.api.dto.RegisterTeamRequest;
import com.example.demo.adapters.in.api.dto.TournamentMatchResponse;
import com.example.demo.adapters.in.api.dto.RaceResultRequest;
import com.example.demo.adapters.in.api.dto.RaceResultResponse;
import com.example.demo.adapters.in.api.dto.LeagueMatchResultRequest;
import com.example.demo.adapters.in.api.dto.LeagueStandingResponse;
import com.example.demo.adapters.in.api.dto.CancelTournamentResponse;
import com.example.demo.adapters.in.api.dto.TeamSummaryDto;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.domain.models.TournamentMatch;
import com.example.demo.core.domain.models.RaceResult;
import com.example.demo.core.ports.in.CreateTournamentPort;
import com.example.demo.core.ports.in.GenerateEliminationFixturePort;
import com.example.demo.core.ports.in.GenerateLeagueFixturePort;
import com.example.demo.core.ports.in.GetFixturePort;
import com.example.demo.core.ports.in.GetTournamentByIdPort;
import com.example.demo.core.ports.in.GetAllTournamentsPort;
import com.example.demo.core.ports.in.ListPublicTournamentsPort;
import com.example.demo.core.ports.in.ListTournamentsByStatusPort;
import com.example.demo.core.ports.in.RegisterToTournamentPort;
import com.example.demo.core.ports.in.RegisterRunnerToTournamentPort;
import com.example.demo.core.ports.in.RegisterTeamToTournamentPort;
import com.example.demo.core.ports.in.ReportMatchResultPort;
import com.example.demo.core.ports.in.ReportRaceResultsPort;
import com.example.demo.core.ports.in.GetRaceResultsPort;
import com.example.demo.core.ports.in.ReportLeagueMatchResultPort;
import com.example.demo.core.ports.in.GetLeagueStandingsPort;
import com.example.demo.core.ports.in.CancelTournamentPort;
import com.example.demo.core.ports.out.TeamQueryPort;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    private final CreateTournamentPort createTournamentPort;
    private final GetAllTournamentsPort getAllTournamentsPort;
    private final GetTournamentByIdPort getTournamentById;
    private final ListPublicTournamentsPort listPublicTournamentsPort;
    private final ListTournamentsByStatusPort listTournamentsByStatusPort;
    private final RegisterToTournamentPort registerToTournamentPort;
    private final RegisterRunnerToTournamentPort registerRunnerToTournamentPort;
    private final RegisterTeamToTournamentPort registerTeamToTournamentPort;
    private final GenerateEliminationFixturePort generateEliminationFixturePort;
    private final GenerateLeagueFixturePort generateLeagueFixturePort;
    private final GetFixturePort getFixturePort;
    private final ReportMatchResultPort reportMatchResultPort;
    private final ReportLeagueMatchResultPort reportLeagueMatchResultPort;
    private final ReportRaceResultsPort reportRaceResultsPort;
    private final GetRaceResultsPort getRaceResultsPort;
    private final GetLeagueStandingsPort getLeagueStandingsPort;
    private final CancelTournamentPort cancelTournamentPort;
    private final TeamQueryPort teamQueryPort;

    public TournamentController(CreateTournamentPort createTournamentPort,
            GetAllTournamentsPort getAllTournamentsPort,
            GetTournamentByIdPort getTournamentById,
            ListPublicTournamentsPort listPublicTournamentsPort,
            ListTournamentsByStatusPort listTournamentsByStatusPort,
            RegisterToTournamentPort registerToTournamentPort,
            RegisterRunnerToTournamentPort registerRunnerToTournamentPort,
            RegisterTeamToTournamentPort registerTeamToTournamentPort,
            GenerateEliminationFixturePort generateEliminationFixturePort,
            GenerateLeagueFixturePort generateLeagueFixturePort,
            GetFixturePort getFixturePort,
            ReportMatchResultPort reportMatchResultPort,
            ReportLeagueMatchResultPort reportLeagueMatchResultPort,
            ReportRaceResultsPort reportRaceResultsPort,
            GetRaceResultsPort getRaceResultsPort,
            GetLeagueStandingsPort getLeagueStandingsPort,
            CancelTournamentPort cancelTournamentPort,
            TeamQueryPort teamQueryPort) {
        this.createTournamentPort = createTournamentPort;
        this.getAllTournamentsPort = getAllTournamentsPort;
        this.getTournamentById = getTournamentById;
        this.listPublicTournamentsPort = listPublicTournamentsPort;
        this.listTournamentsByStatusPort = listTournamentsByStatusPort;
        this.registerToTournamentPort = registerToTournamentPort;
        this.registerRunnerToTournamentPort = registerRunnerToTournamentPort;
        this.registerTeamToTournamentPort = registerTeamToTournamentPort;
        this.generateEliminationFixturePort = generateEliminationFixturePort;
        this.generateLeagueFixturePort = generateLeagueFixturePort;
        this.getFixturePort = getFixturePort;
        this.reportMatchResultPort = reportMatchResultPort;
        this.reportLeagueMatchResultPort = reportLeagueMatchResultPort;
        this.reportRaceResultsPort = reportRaceResultsPort;
        this.getRaceResultsPort = getRaceResultsPort;
        this.getLeagueStandingsPort = getLeagueStandingsPort;
        this.cancelTournamentPort = cancelTournamentPort;
        this.teamQueryPort = teamQueryPort;
    }

    @PostMapping("/organizer/{organizerId}")
    public ResponseEntity<TournamentResponse> create(
            @PathVariable Long organizerId,
            @Valid @RequestBody CreateTournamentRequest request) {

        Tournament saved = createTournamentPort.create(
                TournamentMapper.toDomain(request),
                organizerId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TournamentMapper.toResponse(saved));
    }

    @GetMapping("/public")
    public List<TournamentSummaryResponse> listPublicTournaments(
            @RequestParam(required = false) TournamentStatus status,
            @RequestParam(required = false) Long disciplineId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startTo,
            @RequestParam(required = false) Boolean withPrize,
            @RequestParam(required = false) Boolean withRegistrationCost) {

        return listPublicTournamentsPort.list(
                status,
                disciplineId,
                name,
                startFrom,
                startTo,
                withPrize,
                withRegistrationCost)
                .stream()
                .map(TournamentSummaryMapper::toResponse)
                .toList();
    }

    @GetMapping("/status")
    public List<TournamentSummaryResponse> listByStatus(@RequestParam TournamentStatus status) {
        return listTournamentsByStatusPort.listByStatus(status)
                .stream()
                .map(TournamentSummaryMapper::toResponse)
                .toList();
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTournaments() {
        try {
            return ResponseEntity.ok(getAllTournamentsPort.getAllTournaments());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTournamentById(@PathVariable Long id) {
        try {
            Tournament tournament = getTournamentById.getTournamentById(id);
            return ResponseEntity.ok(TournamentMapper.toResponse(tournament));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTournament(@PathVariable Long id, Authentication authentication) {
        try {
            String userEmail = authentication != null ? authentication.getName() : null;
            var result = cancelTournamentPort.cancel(id, userEmail);
            String message = String.format(
                    "El usuario %s ha cancelado el torneo el día %s. Se reembolsará a todos los que abonaron dentro de las próximas 48 horas.",
                    result.canceledByName(),
                    result.canceledAt());
            return ResponseEntity.ok(new CancelTournamentResponse(
                    id,
                    TournamentStatus.CANCELADO,
                    result.canceledAt(),
                    result.canceledByName(),
                    message));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<?> registerToTournament(@PathVariable Long id,
            @Valid @RequestBody RegisterToTournamentRequest request) {
        try {
            registerToTournamentPort.register(id, request.userId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Inscripción realizada correctamente",
                            "tournamentId", id,
                            "userId", request.userId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/register/runner")
    public ResponseEntity<?> registerRunnerToTournament(@PathVariable Long id,
            Authentication authentication,
            @RequestBody(required = false) RunnerRegistrationRequest request) {
        try {
            String userEmail = authentication != null ? authentication.getName() : null;
            registerRunnerToTournamentPort.register(id, userEmail, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Inscripcion a carrera registrada",
                            "tournamentId", id,
                            "userEmail", userEmail));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/register/team")
    public ResponseEntity<?> registerTeamToTournament(@PathVariable Long id,
            @Valid @RequestBody RegisterTeamRequest request) {
        try {
            registerTeamToTournamentPort.registerTeam(id, request.userId(), request.teamName(), request.participants());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Equipo inscrito correctamente",
                            "tournamentId", id,
                            "userId", request.userId(),
                            "teamName", request.teamName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/fixture/elimination")
    public ResponseEntity<?> generateEliminationFixture(@PathVariable Long id) {
        try {
            generateEliminationFixturePort.generate(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Fixture eliminatorio generado",
                    "tournamentId", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/fixture/league")
    public ResponseEntity<?> generateLeagueFixture(@PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean doubleRound) {
        try {
            generateLeagueFixturePort.generate(id, doubleRound);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Fixture de liga generado",
                    "tournamentId", id,
                    "doubleRound", doubleRound));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/fixture")
    public ResponseEntity<?> getFixture(@PathVariable Long id) {
        try {
            List<TournamentMatch> matches = getFixturePort.getFixture(id);
            
            // Obtener todos los equipos del torneo
            List<TeamQueryPort.TeamSummary> teams = teamQueryPort.findTeamsByTournament(id);
            Map<Long, TeamQueryPort.TeamSummary> teamMap = teams.stream()
                    .collect(java.util.stream.Collectors.toMap(
                            TeamQueryPort.TeamSummary::id,
                            team -> team));
            
            List<TournamentMatchResponse> response = matches.stream()
                    .map(m -> new TournamentMatchResponse(
                            m.getId(),
                            m.getRound(),
                            m.getMatchNumber(),
                            mapToTeamDto(teamMap.get(m.getHomeTeamId())),
                            mapToTeamDto(teamMap.get(m.getAwayTeamId())),
                            mapToTeamDto(teamMap.get(m.getWinnerTeamId())),
                            m.getStatus(),
                            m.getScoreHome(),
                            m.getScoreAway(),
                            m.getScheduledAt()))
                    .toList();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    private TeamSummaryDto mapToTeamDto(TeamQueryPort.TeamSummary team) {
        return team != null ? new TeamSummaryDto(team.id(), team.name()) : null;
    }

    @PostMapping("/{tournamentId}/matches/{matchId}/result")
    public ResponseEntity<?> reportMatchResult(@PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @Valid @RequestBody com.example.demo.adapters.in.api.dto.ReportMatchResultRequest request) {
        try {
            reportMatchResultPort.reportResult(
                    tournamentId,
                    matchId,
                    request.scoreHome(),
                    request.scoreAway(),
                    request.winnerTeamId());
            return ResponseEntity.ok(Map.of(
                    "message", "Resultado registrado",
                    "tournamentId", tournamentId,
                    "matchId", matchId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/race/results")
    public ResponseEntity<?> reportRaceResults(@PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody List<RaceResultRequest> request) {
        try {
            String organizerEmail = authentication != null ? authentication.getName() : null;
            List<RaceResult> results = request.stream()
                    .map(r -> new RaceResult(null, id, r.teamId(), null, r.timeMillis(), null, null, null))
                    .toList();
            reportRaceResultsPort.report(id, organizerEmail, results);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Resultados de carrera registrados",
                            "tournamentId", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/race/results")
    public ResponseEntity<?> getRaceResults(@PathVariable Long id) {
        try {
            List<RaceResultResponse> response = getRaceResultsPort.list(id)
                    .stream()
                    .map(r -> new RaceResultResponse(
                            r.getTeamId(),
                            r.getTeamName(),
                            r.getTimeMillis(),
                            r.getPosition()))
                    .toList();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{tournamentId}/matches/{matchId}/league-result")
    public ResponseEntity<?> reportLeagueMatchResult(@PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @Valid @RequestBody LeagueMatchResultRequest request) {
        try {
            reportLeagueMatchResultPort.reportResult(
                    tournamentId,
                    matchId,
                    request.scoreHome(),
                    request.scoreAway());
            return ResponseEntity.ok(Map.of(
                    "message", "Resultado de liga registrado",
                    "tournamentId", tournamentId,
                    "matchId", matchId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/standings")
    public ResponseEntity<?> getStandings(@PathVariable Long id) {
        try {
            List<LeagueStandingResponse> response = getLeagueStandingsPort.list(id)
                    .stream()
                    .map(s -> new LeagueStandingResponse(
                            s.teamId(),
                            s.teamName(),
                            s.played(),
                            s.won(),
                            s.draw(),
                            s.lost(),
                            s.goalsFor(),
                            s.goalsAgainst(),
                            s.goalDifference(),
                            s.points()))
                    .toList();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

