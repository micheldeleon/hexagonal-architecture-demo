package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class GetUserByIdUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Test
    void getUserById_nullIdThrowsDueToCurrentImplementationOrder() {
        GetUserByIdUseCase useCase = new GetUserByIdUseCase(userRepositoryPort);
        assertThatThrownBy(() -> useCase.getUserById(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void getUserById_delegatesToRepository() {
        GetUserByIdUseCase useCase = new GetUserByIdUseCase(userRepositoryPort);
        User user = TestDataFactory.validUser(1L);
        when(userRepositoryPort.findById(1L)).thenReturn(user);
        assertThat(useCase.getUserById(1L)).isSameAs(user);
    }
}

