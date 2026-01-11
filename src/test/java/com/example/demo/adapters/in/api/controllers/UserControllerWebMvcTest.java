package com.example.demo.adapters.in.api.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.adapters.in.api.dto.UserFullDto;
import com.example.demo.adapters.in.api.dto.UserRegisterDto;
import com.example.demo.core.domain.models.Department;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.GetAllTournamentsPort;
import com.example.demo.core.ports.in.GetTournamentPort;
import com.example.demo.core.ports.in.GetUserByIdAndEmailPort;
import com.example.demo.core.ports.in.GetUserByIdPort;
import com.example.demo.core.ports.in.ListUsersPort;
import com.example.demo.core.ports.in.RegisterUserPort;
import com.example.demo.core.ports.in.ToOrganizerPort;
import com.example.demo.core.ports.in.UpdateProfilePort;
import com.example.demo.core.application.service.ImageUploadService;
import com.example.demo.testsupport.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("removal")
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ListUsersPort listUsersPort;
    @MockBean
    private RegisterUserPort registerUserPort;
    @MockBean
    private UpdateProfilePort updateProfilePort;
    @MockBean
    private GetUserByIdAndEmailPort getUserByIdAndEmailPort;
    @MockBean
    private GetUserByIdPort getUserByIdPort;
    @MockBean
    private ToOrganizerPort toOrganizerPort;
    @MockBean
    private GetAllTournamentsPort getAllTournamentsPort;
    @MockBean
    private GetTournamentPort getTournamentPort;

    @Test
    void getAllUsers_returnsMappedList() throws Exception {
        User user = TestDataFactory.validUser(1L);
        when(listUsersPort.listUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()));
    }

    @Test
    void register_returnsOkOnSuccess_andBadRequestOnFailure() throws Exception {
        UserRegisterDto dto = new UserRegisterDto("Ana", "Pérez", "ana@example.com", "password123");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(registerUserPort).registerUser(any(User.class));

        doThrow(new RuntimeException("fail")).when(registerUserPort).registerUser(any(User.class));
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void completeProfile_validatesAndUpdates() throws Exception {
        UserFullDto dto = new UserFullDto(
                1L,
                "Ana",
                "Pérez",
                "ana@example.com",
                new Date(0),
                TestDataFactory.validUruguayanId("1234567"),
                "+598 91 234 567",
                "Calle 123",
                1L,
                0.0,
                null);

        mockMvc.perform(put("/api/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(updateProfilePort).completion(any(User.class));
    }

    @Test
    void getUserByIdAndEmail_returnsFullDto() throws Exception {
        User user = TestDataFactory.validUser(1L);
        user.setDepartment(new Department(5L));
        when(getUserByIdAndEmailPort.getUserByIdAndEmail(1L, "ana@example.com")).thenReturn(user);

        mockMvc.perform(get("/api/users")
                .param("id", "1")
                .param("email", "ana@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("ana@example.com"));
    }

    @Test
    void getTournamentsOrganized_filtersByOrganizerId() throws Exception {
        User user = TestDataFactory.validUser(1L);
        when(getUserByIdAndEmailPort.getUserByIdAndEmail(1L, "ana@example.com")).thenReturn(user);

        Tournament t1 = new Tournament();
        t1.setId(10L);
        t1.setName("A");
        t1.setOrganizer(user);

        Tournament t2 = new Tournament();
        t2.setId(20L);
        t2.setName("B");
        t2.setOrganizer(TestDataFactory.validUser(2L));

        when(getAllTournamentsPort.getAllTournaments()).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/users/tournaments/organized")
                .param("id", "1")
                .param("email", "ana@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    void toOrganizer_callsPort() throws Exception {
        mockMvc.perform(post("/api/users/organizer").param("id", "1"))
                .andExpect(status().isOk());
        verify(toOrganizerPort).toOrganizer(1L);
    }
}

