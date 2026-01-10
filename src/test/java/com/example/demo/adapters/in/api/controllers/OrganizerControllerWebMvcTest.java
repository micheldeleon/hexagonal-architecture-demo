package com.example.demo.adapters.in.api.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.adapters.in.api.dto.RateOrganizerRequest;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.GetOrganizerReputationPort;
import com.example.demo.core.ports.in.RateOrganizerPort;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(OrganizerController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrganizerControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RateOrganizerPort rateOrganizerPort;
    @MockBean
    private GetOrganizerReputationPort getOrganizerReputationPort;
    @MockBean
    private UserRepositoryPort userRepositoryPort;

    @Test
    void rateOrganizer_returns401WhenNoAuthentication() throws Exception {
        RateOrganizerRequest req = new RateOrganizerRequest(10L, 5, "ok");
        mockMvc.perform(post("/api/organizers/2/rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rateOrganizer_returnsCreatedWhenOk() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken("ana@example.com", "N/A");
        User user = TestDataFactory.validUser(1L);
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(rateOrganizerPort.rate(eq(1L), eq(2L), eq(10L), eq(5), any()))
                .thenReturn(new RateOrganizerPort.RateOrganizerResult(99L, 2L, 5, 4.5, 3));

        RateOrganizerRequest req = new RateOrganizerRequest(10L, 5, "ok");
        mockMvc.perform(post("/api/organizers/2/rate")
                .principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.organizerId").value(2))
                .andExpect(jsonPath("$.scoreGiven").value(5))
                .andExpect(jsonPath("$.totalRatings").value(3));
    }

    @Test
    void getOrganizerReputation_returnsMappedResponse() throws Exception {
        when(getOrganizerReputationPort.getReputation(2L)).thenReturn(new GetOrganizerReputationPort.OrganizerReputationResult(
                2L,
                "Org",
                4.0,
                2,
                new GetOrganizerReputationPort.RatingDistribution(1, 1, 0, 0, 0),
                List.of()));

        mockMvc.perform(get("/api/organizers/2/reputation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organizerId").value(2))
                .andExpect(jsonPath("$.averageScore").value(4.0));
    }
}
