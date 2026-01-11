package com.example.demo.adapters.in.api.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
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

import com.example.demo.adapters.in.api.dto.CreateNotificationRequest;
import com.example.demo.core.application.service.NotificationSseService;
import com.example.demo.core.domain.models.Notification;
import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.CreateNotificationPort;
import com.example.demo.core.ports.in.GetUserNotificationsPort;
import com.example.demo.core.ports.in.MarkNotificationAsReadPort;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("removal")
@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetUserNotificationsPort getUserNotificationsPort;
    @MockBean
    private MarkNotificationAsReadPort markNotificationAsReadPort;
    @MockBean
    private CreateNotificationPort createNotificationPort;
    @MockBean
    private UserRepositoryPort userRepositoryPort;
    @MockBean
    private NotificationSseService notificationSseService;

    @Test
    void createNotification_returnsCreatedDto() throws Exception {
        CreateNotificationRequest req = new CreateNotificationRequest(1L, NotificationType.WELCOME, "Hola", "Mensaje", 10L);
        Notification n = new Notification(1L, NotificationType.WELCOME, "Hola", "Mensaje", 10L);
        n.setId(99L);
        n.setCreatedAt(new Date(0));
        when(createNotificationPort.createNotification(eq(1L), eq(NotificationType.WELCOME), eq("Hola"), eq("Mensaje"),
                eq(10L))).thenReturn(n);

        mockMvc.perform(post("/api/notifications/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.type").value("WELCOME"));
    }

    @Test
    void unreadCount_usesAuthenticatedEmailToResolveUser() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken("ana@example.com", "N/A");
        User user = TestDataFactory.validUser(1L);
        user.setEmail("ana@example.com");
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(getUserNotificationsPort.getUnreadCount(1L)).thenReturn(5);

        mockMvc.perform(get("/api/notifications/unread/count").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }

    @Test
    void markAsRead_callsPortWithResolvedUser() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken("ana@example.com", "N/A");
        User user = TestDataFactory.validUser(1L);
        user.setEmail("ana@example.com");
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(put("/api/notifications/99/read").principal(auth))
                .andExpect(status().isOk());

        verify(markNotificationAsReadPort).markAsRead(1L, 99L);
    }

    @Test
    void getUnreadNotifications_returnsResponse() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken("ana@example.com", "N/A");
        User user = TestDataFactory.validUser(1L);
        user.setEmail("ana@example.com");
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(user));

        Notification n = new Notification(1L, NotificationType.WELCOME, "t", "m", 10L);
        n.setId(1L);
        when(getUserNotificationsPort.getUnreadNotifications(1L)).thenReturn(List.of(n));

        mockMvc.perform(get("/api/notifications/unread").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.unreadCount").value(1))
                .andExpect(jsonPath("$.notifications[0].id").value(1));
    }
}

