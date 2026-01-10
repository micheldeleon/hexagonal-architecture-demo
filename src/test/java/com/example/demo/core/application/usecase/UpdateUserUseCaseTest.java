package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.Department;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.testsupport.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Test
    void completion_rejectsNullUser() {
        UpdateUserUseCase useCase = new UpdateUserUseCase(userRepositoryPort);
        assertThatThrownBy(() -> useCase.completion(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    void completion_updatesExistingUserProfile() {
        UpdateUserUseCase useCase = new UpdateUserUseCase(userRepositoryPort);

        User existing = TestDataFactory.validUser(1L);
        when(userRepositoryPort.findByEmail("ana@example.com")).thenReturn(Optional.of(existing));

        User patch = new User();
        patch.setEmail("ana@example.com");
        patch.setDateOfBirth(new Date(0));
        patch.setNationalId(TestDataFactory.validUruguayanId("1234567"));
        patch.setPhoneNumber("+598 91 234 567");
        patch.setAddress("Nueva dirección");
        patch.setDepartment(new Department(2L));

        useCase.completion(patch);

        ArgumentCaptor<User> updatedCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryPort).update(updatedCaptor.capture());
        assertThat(updatedCaptor.getValue().getAddress()).isEqualTo("Nueva dirección");
        assertThat(updatedCaptor.getValue().getDepartment().getId()).isEqualTo(2L);
    }
}

