package com.example.demo.adapters.in.api.controllers;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.core.ports.out.EmailSenderPort;

@SuppressWarnings("removal")
@WebMvcTest(PruebaController.class)
@AutoConfigureMockMvc(addFilters = false)
class PruebaControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailSenderPort emailSenderPort;

    @Test
    void sendTestEmail_sendsEmailAndReturnsOk() throws Exception {
        mockMvc.perform(post("/api/mail/test").param("to", "x@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("x@example.com")));
        verify(emailSenderPort).sendEmail(org.mockito.ArgumentMatchers.eq("x@example.com"), org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}

