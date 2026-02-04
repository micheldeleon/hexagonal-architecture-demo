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
class DeactivateUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Test
    void deactivate_rejectsSelfDeactivation() {
        DeactivateUserUseCase useCase = new DeactivateUserUseCase(userRepositoryPort);
        assertThatThrownBy(() -> useCase.deactivate(10L, 10L, "reason"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot deactivate their own");
    }

    @Test
    void deactivate_callsRepositoryWhenActive() {
        DeactivateUserUseCase useCase = new DeactivateUserUseCase(userRepositoryPort);
        User target = TestDataFactory.validUser(10L);
        when(userRepositoryPort.findByIdIncludingDeleted(10L)).thenReturn(target);

        useCase.deactivate(10L, 99L, "  spam  ");

        verify(userRepositoryPort).deactivate(10L, 99L, "spam");
    }
}

