package com.tutorneo.adapters.in.api.controllers;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.tutorneo.adapters.in.api.dto.ContactMessageRequest;
import com.tutorneo.core.ports.in.SendContactMessagePort;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ContactController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContactControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SendContactMessagePort sendContactMessagePort;

    @Test
    void send_validRequest_delegatesToPort() throws Exception {
        var dto = new ContactMessageRequest("Juan Perez", "juan@example.com", "Hola, tengo una consulta.");

        mockMvc.perform(post("/api/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Mensaje enviado"));

        verify(sendContactMessagePort).send("Juan Perez", "juan@example.com", "Hola, tengo una consulta.");
    }

    @Test
    void send_missingFields_returnsBadRequest() throws Exception {
        var dto = new ContactMessageRequest("", "not-an-email", "");

        mockMvc.perform(post("/api/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}

