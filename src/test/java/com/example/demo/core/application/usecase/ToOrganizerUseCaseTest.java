package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class ToOrganizerUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Test
    void toOrganizer_rejectsWhenUserNotFound() {
        ToOrganizerUseCase useCase = new ToOrganizerUseCase(userRepositoryPort);
        when(userRepositoryPort.findById(10L)).thenReturn(null);

        assertThatThrownBy(() -> useCase.toOrganizer(10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void toOrganizer_addsRole() {
        ToOrganizerUseCase useCase = new ToOrganizerUseCase(userRepositoryPort);
        User user = TestDataFactory.validUser(10L);
        when(userRepositoryPort.findById(10L)).thenReturn(user);

        useCase.toOrganizer(10L);
        verify(userRepositoryPort).addRole(10L, "ROLE_ORGANIZER");
    }
}

