package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class ListUsersUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Test
    void listUsers_delegatesToRepository() {
        ListUsersUseCase useCase = new ListUsersUseCase(userRepositoryPort);
        User user = TestDataFactory.validUser(1L);
        when(userRepositoryPort.findAll()).thenReturn(List.of(user));
        assertThat(useCase.listUsers()).containsExactly(user);
    }
}

