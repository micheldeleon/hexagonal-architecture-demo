package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.SimpleFormat;
import com.example.demo.core.ports.out.FormatRepositoryPort;

@ExtendWith(MockitoExtension.class)
class ListFormatsByDisciplineUseCaseTest {

    @Mock
    private FormatRepositoryPort formatRepositoryPort;

    @Test
    void listByDisciplineId_delegatesToRepository() {
        ListFormatsByDisciplineUseCase useCase = new ListFormatsByDisciplineUseCase(formatRepositoryPort);
        SimpleFormat format = new SimpleFormat(1L, "Liga", true);
        when(formatRepositoryPort.findByDisciplineId(99L)).thenReturn(List.of(format));
        assertThat(useCase.listByDisciplineId(99L)).containsExactly(format);
    }
}

