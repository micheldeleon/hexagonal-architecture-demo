package com.tutorneo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tutorneo.core.domain.models.User;
import com.tutorneo.core.ports.out.UserRepositoryPort;
import com.tutorneo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class GetUserByIdAndEmailUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Test
    void getUserByIdAndEmail_throwsOnNullIdDueToCurrentImplementationOrder() {
        GetUserByIdAndEmailUseCase useCase = new GetUserByIdAndEmailUseCase(userRepositoryPort);
        assertThatThrownBy(() -> useCase.getUserByIdAndEmail(null, "ana@example.com"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void getUserByIdAndEmail_rejectsInvalidEmail() {
        GetUserByIdAndEmailUseCase useCase = new GetUserByIdAndEmailUseCase(userRepositoryPort);
        assertThatThrownBy(() -> useCase.getUserByIdAndEmail(1L, "bad"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getUserByIdAndEmail_rejectsWhenEmailDoesNotMatchId() {
        GetUserByIdAndEmailUseCase useCase = new GetUserByIdAndEmailUseCase(userRepositoryPort);
        User user = TestDataFactory.validUser(1L);
        when(userRepositoryPort.findById(1L)).thenReturn(user);

        assertThatThrownBy(() -> useCase.getUserByIdAndEmail(1L, "other@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se encontro");
    }
}

