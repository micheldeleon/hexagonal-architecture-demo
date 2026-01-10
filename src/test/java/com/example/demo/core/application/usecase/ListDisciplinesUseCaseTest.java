package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.Discipline;
import com.example.demo.core.ports.out.DisciplineRepositoryPort;

@ExtendWith(MockitoExtension.class)
class ListDisciplinesUseCaseTest {

    @Mock
    private DisciplineRepositoryPort disciplineRepositoryPort;

    @Test
    void listAll_delegatesToRepository() {
        ListDisciplinesUseCase useCase = new ListDisciplinesUseCase(disciplineRepositoryPort);
        Discipline discipline = new Discipline(1L, true, "Futbol", null);
        when(disciplineRepositoryPort.findAll()).thenReturn(List.of(discipline));
        assertThat(useCase.listAll()).containsExactly(discipline);
    }
}

