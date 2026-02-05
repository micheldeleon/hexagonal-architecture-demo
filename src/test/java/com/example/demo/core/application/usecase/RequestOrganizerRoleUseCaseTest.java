package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
class RequestOrganizerRoleUseCaseTest {

    @Mock
    private OrganizerRoleRequestRepositoryPort requestRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Test
    void request_rejectsWhenUserAlreadyOrganizer() {
        RequestOrganizerRoleUseCase useCase = new RequestOrganizerRoleUseCase(requestRepositoryPort, userRepositoryPort);
        when(userRepositoryPort.hasRole(10L, "ROLE_ORGANIZER")).thenReturn(true);

        assertThatThrownBy(() -> useCase.request(10L, "msg"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already");
        verify(requestRepositoryPort, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void request_returnsExistingPending() {
        RequestOrganizerRoleUseCase useCase = new RequestOrganizerRoleUseCase(requestRepositoryPort, userRepositoryPort);
        when(userRepositoryPort.hasRole(10L, "ROLE_ORGANIZER")).thenReturn(false);

        OrganizerRoleRequest pending = new OrganizerRoleRequest(1L, 10L, OrganizerRoleRequestStatus.PENDING, "x", null, null, null, null, null);
        when(requestRepositoryPort.findPendingByUserId(10L)).thenReturn(Optional.of(pending));

        OrganizerRoleRequest out = useCase.request(10L, "ignored");
        assertThat(out.getId()).isEqualTo(1L);
        verify(requestRepositoryPort, never()).save(org.mockito.ArgumentMatchers.any());
    }
}

