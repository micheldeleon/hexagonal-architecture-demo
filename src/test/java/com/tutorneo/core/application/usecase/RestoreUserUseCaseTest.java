package com.tutorneo.core.application.usecase;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tutorneo.core.domain.models.User;
import com.tutorneo.core.ports.out.UserRepositoryPort;
import com.tutorneo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class RestoreUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Test
    void restore_noopsWhenUserIsNotDeleted() {
        RestoreUserUseCase useCase = new RestoreUserUseCase(userRepositoryPort);
        User target = TestDataFactory.validUser(10L);
        when(userRepositoryPort.findByIdIncludingDeleted(10L)).thenReturn(target);

        useCase.restore(10L, 99L);

        verify(userRepositoryPort, never()).restore(10L, 99L);
    }

    @Test
    void restore_callsRepositoryWhenDeleted() {
        RestoreUserUseCase useCase = new RestoreUserUseCase(userRepositoryPort);
        User target = TestDataFactory.validUser(10L);
        target.setDeletedAt(Instant.now());
        when(userRepositoryPort.findByIdIncludingDeleted(10L)).thenReturn(target);

        useCase.restore(10L, 99L);

        verify(userRepositoryPort).restore(10L, 99L);
    }
}

