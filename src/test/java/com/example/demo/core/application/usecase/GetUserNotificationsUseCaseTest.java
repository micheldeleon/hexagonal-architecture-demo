package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.Notification;
import com.example.demo.core.ports.out.NotificationPort;

@ExtendWith(MockitoExtension.class)
class GetUserNotificationsUseCaseTest {

    @Mock
    private NotificationPort notificationPort;

    @Test
    void methods_validateUserId() {
        GetUserNotificationsUseCase useCase = new GetUserNotificationsUseCase(notificationPort);
        assertThatThrownBy(() -> useCase.getUserNotifications(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.getUnreadNotifications(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.getUnreadCount(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getUserNotifications_delegatesToPort() {
        GetUserNotificationsUseCase useCase = new GetUserNotificationsUseCase(notificationPort);
        Notification n = new Notification();
        when(notificationPort.findByUserId(1L)).thenReturn(List.of(n));

        assertThat(useCase.getUserNotifications(1L)).containsExactly(n);
        verify(notificationPort).findByUserId(1L);
    }
}

