package com.example.demo.adapters.in.api.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.core.domain.models.TournamentModerationStatus;
import com.example.demo.core.ports.in.AdminDeactivateTournamentPort;
import com.example.demo.core.ports.in.AdminReactivateTournamentPort;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AdminTournamentModerationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminTournamentModerationControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminDeactivateTournamentPort adminDeactivateTournamentPort;

    @MockBean
    private AdminReactivateTournamentPort adminReactivateTournamentPort;

    @Test
    void deactivate_delegatesToPort() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken("admin@example.com", "N/A");
        var now = new Date();
        when(adminDeactivateTournamentPort.deactivate(10L, "admin@example.com", "spam"))
                .thenReturn(new AdminDeactivateTournamentPort.DeactivateResult(
                        10L,
                        TournamentModerationStatus.DEACTIVATED,
                        now,
                        99L,
                        "spam"));

        mockMvc.perform(post("/api/admin/tournaments/10/deactivate")
                .principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.Map.of("reason", "spam"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tournamentId").value(10))
                .andExpect(jsonPath("$.moderationStatus").value("DEACTIVATED"))
                .andExpect(jsonPath("$.moderatedByAdminId").value(99))
                .andExpect(jsonPath("$.reason").value("spam"));

        verify(adminDeactivateTournamentPort).deactivate(10L, "admin@example.com", "spam");
    }

    @Test
    void reactivate_delegatesToPort() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken("admin@example.com", "N/A");
        var now = new Date();
        when(adminReactivateTournamentPort.reactivate(10L, "admin@example.com", "ok"))
                .thenReturn(new AdminReactivateTournamentPort.ReactivateResult(
                        10L,
                        TournamentModerationStatus.ACTIVE,
                        now,
                        99L,
                        "ok"));

        mockMvc.perform(post("/api/admin/tournaments/10/reactivate")
                .principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.Map.of("reason", "ok"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tournamentId").value(10))
                .andExpect(jsonPath("$.moderationStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.moderatedByAdminId").value(99))
                .andExpect(jsonPath("$.reason").value("ok"));

        verify(adminReactivateTournamentPort).reactivate(10L, "admin@example.com", "ok");
    }

    @Test
    void deactivate_withoutAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/admin/tournaments/10/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.Map.of("reason", "spam"))))
                .andExpect(status().isUnauthorized());
    }
}

