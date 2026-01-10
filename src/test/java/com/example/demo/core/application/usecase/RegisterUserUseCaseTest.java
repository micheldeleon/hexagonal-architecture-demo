package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.CreateNotificationPort;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private CreateNotificationPort createNotificationPort;

    @Test
    void registerUser_savesUserAndCreatesWelcomeNotification() {
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepositoryPort, createNotificationPort);

        User input = new User(null, "Ana", "Pérez", "ana@example.com", "password123");
        User saved = TestDataFactory.validUser(1L);

        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(saved));

        useCase.registerUser(input);

        verify(userRepositoryPort).save(any(User.class));

        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<NotificationType> typeCaptor = ArgumentCaptor.forClass(NotificationType.class);
        verify(createNotificationPort).createNotification(userIdCaptor.capture(), typeCaptor.capture(), any(), any(),
                eq(null));
        assertThat(userIdCaptor.getValue()).isEqualTo(1L);
        assertThat(typeCaptor.getValue()).isEqualTo(NotificationType.WELCOME);
    }

    @Test
    void registerUser_wrapsExceptionsWithContext() {
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepositoryPort, createNotificationPort);
        User input = new User(null, "Ana", "Pérez", "ana@example.com", "password123");

        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.registerUser(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to register user");
    }
}
