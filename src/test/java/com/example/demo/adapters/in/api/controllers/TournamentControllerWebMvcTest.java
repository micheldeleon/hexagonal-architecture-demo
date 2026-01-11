package com.example.demo.adapters.in.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.adapters.in.api.dto.CreateTournamentRequest;
import com.example.demo.adapters.in.api.dto.LeagueMatchResultRequest;
import com.example.demo.adapters.in.api.dto.RegisterTeamRequest;
import com.example.demo.adapters.in.api.dto.RegisterToTournamentRequest;
import com.example.demo.adapters.in.api.dto.RemoveTeamFromTournamentRequest;
import com.example.demo.adapters.in.api.dto.ReportMatchResultRequest;
import com.example.demo.adapters.in.api.dto.RaceResultRequest;
import com.example.demo.core.domain.models.Formats.EliminationFormat;
import com.example.demo.core.domain.models.Formats.LeagueFormat;
import com.example.demo.core.domain.models.RaceResult;
import com.example.demo.core.domain.models.SimpleFormat;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.TournamentMatch;
import com.example.demo.core.domain.models.TournamentStatus;
import com.example.demo.core.application.service.ImageUploadService;
import com.example.demo.core.ports.in.CancelTournamentPort;
import com.example.demo.core.ports.in.CreateTournamentPort;
import com.example.demo.core.ports.in.GenerateEliminationFixturePort;
import com.example.demo.core.ports.in.GenerateLeagueFixturePort;
import com.example.demo.core.ports.in.GetAllTournamentsPort;
import com.example.demo.core.ports.in.GetFixturePort;
import com.example.demo.core.ports.in.GetLeagueStandingsPort;
import com.example.demo.core.ports.in.GetRaceResultsPort;
import com.example.demo.core.ports.in.GetTournamentByIdPort;
import com.example.demo.core.ports.in.ListPublicTournamentsPort;
import com.example.demo.core.ports.in.ListTournamentsByStatusPort;
import com.example.demo.core.ports.in.RegisterRunnerToTournamentPort;
import com.example.demo.core.ports.in.RegisterTeamToTournamentPort;
import com.example.demo.core.ports.in.RegisterToTournamentPort;
import com.example.demo.core.ports.in.RemoveTeamFromTournamentPort;
import com.example.demo.core.ports.in.ReportLeagueMatchResultPort;
import com.example.demo.core.ports.in.ReportMatchResultPort;
import com.example.demo.core.ports.in.ReportRaceResultsPort;
import com.example.demo.core.ports.in.StartTournamentPort;
import com.example.demo.core.ports.in.UpdateTournamentPort;
import com.example.demo.core.ports.out.TeamQueryPort;
import com.example.demo.testsupport.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("removal")
@WebMvcTest(TournamentController.class)
@AutoConfigureMockMvc(addFilters = false)
class TournamentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateTournamentPort createTournamentPort;
    @MockBean
    private GetAllTournamentsPort getAllTournamentsPort;
    @MockBean
    private GetTournamentByIdPort getTournamentByIdPort;
    @MockBean
    private ListPublicTournamentsPort listPublicTournamentsPort;
    @MockBean
    private ListTournamentsByStatusPort listTournamentsByStatusPort;
    @MockBean
    private RegisterToTournamentPort registerToTournamentPort;
    @MockBean
    private RegisterRunnerToTournamentPort registerRunnerToTournamentPort;
    @MockBean
    private RegisterTeamToTournamentPort registerTeamToTournamentPort;
    @MockBean
    private GenerateEliminationFixturePort generateEliminationFixturePort;
    @MockBean
    private GenerateLeagueFixturePort generateLeagueFixturePort;
    @MockBean
    private GetFixturePort getFixturePort;
    @MockBean
    private ReportMatchResultPort reportMatchResultPort;
    @MockBean
    private ReportLeagueMatchResultPort reportLeagueMatchResultPort;
    @MockBean
    private ReportRaceResultsPort reportRaceResultsPort;
    @MockBean
    private GetRaceResultsPort getRaceResultsPort;
    @MockBean
    private GetLeagueStandingsPort getLeagueStandingsPort;
    @MockBean
    private CancelTournamentPort cancelTournamentPort;
    @MockBean
    private StartTournamentPort startTournamentPort;
    @MockBean
    private RemoveTeamFromTournamentPort removeTeamFromTournamentPort;
    @MockBean
    private TeamQueryPort teamQueryPort;
    @MockBean
    private ImageUploadService imageUploadService;
    @MockBean
    private UpdateTournamentPort updateTournamentPort;

    @Test
    void create_returnsCreatedTournamentResponse() throws Exception {
        Date startAt = new Date(1000);
        Date endAt = new Date(2000);
        CreateTournamentRequest request = new CreateTournamentRequest(
                1L,
                2L,
                "Torneo",
                startAt,
                endAt,
                null,
                false,
                null,
                1,
                2,
                0,
                0,
                null,
                0.0,
                false,
                "Detalles",
                null);

        Tournament domain = TestDataFactory.baseTournament(10L, 1L);
        domain.setFormat(new SimpleFormat(2L, "Liga", true));
        domain.setStatus(TournamentStatus.ABIERTO);
        domain.setIsDoubleRound(false);
        when(createTournamentPort.create(any(Tournament.class), eq(1L))).thenReturn(domain);

        mockMvc.perform(post("/api/tournaments/organizer/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.organizerId").value(1))
                .andExpect(jsonPath("$.status").value("ABIERTO"));
    }

    @Test
    void listPublicTournaments_mapsSummaryResponses() throws Exception {
        Tournament t = TestDataFactory.baseTournament(10L, 1L);
        t.setStatus(TournamentStatus.ABIERTO);
        when(listPublicTournamentsPort.list(any(), any(), any(), any(), any(), any(), any())).thenReturn(List.of(t));

        mockMvc.perform(get("/api/tournaments/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].status").value("ABIERTO"));
    }

    @Test
    void cancelTournament_returns404Or403Or200() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken("org@example.com", "N/A");

        when(cancelTournamentPort.cancel(eq(10L), eq("org@example.com")))
                .thenReturn(new CancelTournamentPort.CancelTournamentResult(10L, "Org", new Date(0)));

        mockMvc.perform(post("/api/tournaments/10/cancel").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"));

        doThrow(new IllegalArgumentException("not found")).when(cancelTournamentPort).cancel(eq(11L), any());
        mockMvc.perform(post("/api/tournaments/11/cancel").principal(auth))
                .andExpect(status().isNotFound());

        doThrow(new SecurityException("forbidden")).when(cancelTournamentPort).cancel(eq(12L), any());
        mockMvc.perform(post("/api/tournaments/12/cancel").principal(auth))
                .andExpect(status().isForbidden());
    }

    @Test
    void registerToTournament_returnsCreatedAndHandlesErrors() throws Exception {
        RegisterToTournamentRequest req = new RegisterToTournamentRequest(2L);

        mockMvc.perform(post("/api/tournaments/10/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tournamentId").value(10))
                .andExpect(jsonPath("$.userId").value(2));

        verify(registerToTournamentPort).register(10L, 2L);

        doThrow(new IllegalArgumentException("not found")).when(registerToTournamentPort).register(eq(11L), any());
        mockMvc.perform(post("/api/tournaments/11/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void generateFixtureAndGetFixture_mapsTeams() throws Exception {
        TournamentMatch match = new TournamentMatch();
        match.setId(100L);
        match.setTournamentId(10L);
        match.setRound(1);
        match.setMatchNumber(1);
        match.setHomeTeamId(1L);
        match.setAwayTeamId(2L);
        match.setWinnerTeamId(1L);
        match.setStatus("PENDING");

        when(getFixturePort.getFixture(10L)).thenReturn(List.of(match));
        when(teamQueryPort.findTeamsByTournament(10L)).thenReturn(List.of(
                new TeamQueryPort.TeamSummary(1L, "A"),
                new TeamQueryPort.TeamSummary(2L, "B")));

        mockMvc.perform(get("/api/tournaments/10/fixture"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].homeTeam.id").value(1))
                .andExpect(jsonPath("$[0].awayTeam.name").value("B"))
                .andExpect(jsonPath("$[0].winnerTeam.name").value("A"));

        mockMvc.perform(post("/api/tournaments/10/fixture/elimination"))
                .andExpect(status().isCreated());
        verify(generateEliminationFixturePort).generate(10L);

        mockMvc.perform(post("/api/tournaments/10/fixture/league").param("doubleRound", "true"))
                .andExpect(status().isCreated());
        verify(generateLeagueFixturePort).generate(10L, true);
    }

    @Test
    void reportMatchResultAndLeagueResult_delegateToPorts() throws Exception {
        ReportMatchResultRequest matchReq = new ReportMatchResultRequest(1, 0, 1L);
        mockMvc.perform(post("/api/tournaments/10/matches/100/result")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(matchReq)))
                .andExpect(status().isOk());
        verify(reportMatchResultPort).reportResult(10L, 100L, 1, 0, 1L);

        LeagueMatchResultRequest leagueReq = new LeagueMatchResultRequest(2, 2);
        mockMvc.perform(post("/api/tournaments/10/matches/100/league-result")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(leagueReq)))
                .andExpect(status().isOk());
        verify(reportLeagueMatchResultPort).reportResult(10L, 100L, 2, 2);
    }

    @Test
    void reportRaceResults_mapsRequestToDomainAndUsesAuthenticationName() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken("org@example.com", "N/A");
        List<RaceResultRequest> body = List.of(
                new RaceResultRequest(1L, 1000L),
                new RaceResultRequest(2L, 2000L));

        mockMvc.perform(post("/api/tournaments/10/race/results")
                .principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        @SuppressWarnings("unchecked")
        org.mockito.ArgumentCaptor<List<RaceResult>> captor = org.mockito.ArgumentCaptor.forClass(List.class);
        verify(reportRaceResultsPort).report(eq(10L), eq("org@example.com"), captor.capture());
        assertThat(captor.getValue()).extracting(RaceResult::getTeamId).containsExactly(1L, 2L);
    }

    @Test
    void getRaceResults_mapsResponses() throws Exception {
        RaceResult r = new RaceResult();
        r.setTeamId(1L);
        r.setTeamName("A");
        r.setTimeMillis(1000L);
        r.setPosition(1);
        when(getRaceResultsPort.list(10L)).thenReturn(List.of(r));

        mockMvc.perform(get("/api/tournaments/10/race/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamId").value(1))
                .andExpect(jsonPath("$[0].teamName").value("A"))
                .andExpect(jsonPath("$[0].position").value(1));
    }

    @Test
    void removeTeamFromTournament_delegatesToPort() throws Exception {
        RemoveTeamFromTournamentRequest req = new RemoveTeamFromTournamentRequest(1L, 5L, "x");

        mockMvc.perform(post("/api/tournaments/10/remove-team")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(removeTeamFromTournamentPort).removeTeam(10L, 1L, 5L, "x");
    }

    @Test
    void listByStatus_delegatesToPort() throws Exception {
        Tournament t = new Tournament();
        t.setId(1L);
        t.setName("T");
        t.setFormat(new LeagueFormat(3, 1, 0, true));
        t.setStatus(TournamentStatus.ABIERTO);
        when(listTournamentsByStatusPort.listByStatus(TournamentStatus.ABIERTO)).thenReturn(List.of(t));

        mockMvc.perform(get("/api/tournaments/status").param("status", "ABIERTO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getTournamentById_returnsResponse() throws Exception {
        Tournament t = TestDataFactory.baseTournament(10L, 1L);
        t.setFormat(new EliminationFormat());
        t.setStatus(TournamentStatus.ABIERTO);
        t.setIsDoubleRound(false);
        when(getTournamentByIdPort.getTournamentById(10L)).thenReturn(t);

        mockMvc.perform(get("/api/tournaments/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void registerTeamToTournament_validatesBodyAndDelegates() throws Exception {
        RegisterTeamRequest req = new RegisterTeamRequest(
                2L,
                "Equipo",
                List.of(new com.example.demo.adapters.in.api.dto.ParticipantRequest("A", "12345678")));

        mockMvc.perform(post("/api/tournaments/10/register/team")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(registerTeamToTournamentPort).registerTeam(eq(10L), eq(2L), eq("Equipo"), any());
    }

    @Test
    void registerRunnerToTournament_usesAuthenticationNameAndAllowsNullBody() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken("runner@example.com", "N/A");

        mockMvc.perform(post("/api/tournaments/10/register/runner").principal(auth))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userEmail").value("runner@example.com"));

        verify(registerRunnerToTournamentPort).register(eq(10L), eq("runner@example.com"), eq(null));
    }

    @Test
    void standings_mapsLeagueStandingResponses() throws Exception {
        when(getLeagueStandingsPort.list(10L)).thenReturn(List.of(
                new com.example.demo.core.ports.in.models.LeagueStanding(1L, "A", 1, 1, 0, 0, 2, 0, 2, 3)));

        mockMvc.perform(get("/api/tournaments/10/standings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamId").value(1))
                .andExpect(jsonPath("$[0].points").value(3));
    }
}
