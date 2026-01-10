package com.example.demo.adapters.in.api.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.core.domain.models.Discipline;
import com.example.demo.core.domain.models.SimpleFormat;
import com.example.demo.core.ports.in.ListDisciplinesPort;
import com.example.demo.core.ports.in.ListFormatsByDisciplinePort;

@WebMvcTest(DisciplineController.class)
@AutoConfigureMockMvc(addFilters = false)
class DisciplineControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListDisciplinesPort listDisciplinesPort;
    @MockBean
    private ListFormatsByDisciplinePort listFormatsByDisciplinePort;

    @Test
    void list_returnsMappedDisciplines() throws Exception {
        when(listDisciplinesPort.listAll()).thenReturn(List.of(new Discipline(1L, true, "Futbol", null)));

        mockMvc.perform(get("/api/disciplines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Futbol"));
    }

    @Test
    void listFormats_returnsMappedFormats() throws Exception {
        when(listFormatsByDisciplinePort.listByDisciplineId(1L)).thenReturn(List.of(new SimpleFormat(2L, "Liga", true)));

        mockMvc.perform(get("/api/disciplines/1/formats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Liga"));
    }
}

