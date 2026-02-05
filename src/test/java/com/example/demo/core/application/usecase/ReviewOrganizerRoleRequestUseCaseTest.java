package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.OrganizerRoleRequest;
import com.example.demo.core.domain.models.OrganizerRoleRequestStatus;
import com.example.demo.core.ports.out.OrganizerRoleRequestRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;

@ExtendWith(MockitoExtension.class)
class ReviewOrganizerRoleRequestUseCaseTest {

    @Mock
    private OrganizerRoleRequestRepositoryPort requestRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Test
    void approve_addsOrganizerRole() {
        ReviewOrganizerRoleRequestUseCase useCase = new ReviewOrganizerRoleRequestUseCase(requestRepositoryPort, userRepositoryPort);
        OrganizerRoleRequest pending = new OrganizerRoleRequest(
                5L, 10L, OrganizerRoleRequestStatus.PENDING, "msg",
                OffsetDateTime.now(), null, null, null, null);
        when(requestRepositoryPort.findById(5L)).thenReturn(Optional.of(pending));
        when(requestRepositoryPort.save(org.mockito.ArgumentMatchers.any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.approve(5L, 99L, "ok");

        verify(userRepositoryPort).addRole(10L, "ROLE_ORGANIZER");
        verify(requestRepositoryPort).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void reject_requiresReason() {
        ReviewOrganizerRoleRequestUseCase useCase = new ReviewOrganizerRoleRequestUseCase(requestRepositoryPort, userRepositoryPort);
        assertThatThrownBy(() -> useCase.reject(1L, 99L, "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("required");
    }
}

