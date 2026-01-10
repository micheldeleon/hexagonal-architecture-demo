package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.domain.models.Notification;
import com.example.demo.core.ports.out.NotificationPort;

@ExtendWith(MockitoExtension.class)
class MarkNotificationAsReadUseCaseTest {

    @Mock
    private NotificationPort notificationPort;

    @Test
    void markAsRead_validatesAndChecksOwnership() {
        MarkNotificationAsReadUseCase useCase = new MarkNotificationAsReadUseCase(notificationPort);

        assertThatThrownBy(() -> useCase.markAsRead(null, 1L)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.markAsRead(1L, null)).isInstanceOf(IllegalArgumentException.class);

        when(notificationPort.findById(10L)).thenReturn(null);
        assertThatThrownBy(() -> useCase.markAsRead(1L, 10L)).isInstanceOf(IllegalArgumentException.class);

        Notification notification = new Notification();
        notification.setId(10L);
        notification.setUserId(2L);
        when(notificationPort.findById(10L)).thenReturn(notification);
        assertThatThrownBy(() -> useCase.markAsRead(1L, 10L)).isInstanceOf(SecurityException.class);
    }

    @Test
    void markAsRead_marksNotification() {
        MarkNotificationAsReadUseCase useCase = new MarkNotificationAsReadUseCase(notificationPort);
        Notification notification = new Notification();
        notification.setId(10L);
        notification.setUserId(1L);
        when(notificationPort.findById(10L)).thenReturn(notification);

        useCase.markAsRead(1L, 10L);
        verify(notificationPort).markAsRead(10L);
    }

    @Test
    void markAllAsRead_delegates() {
        MarkNotificationAsReadUseCase useCase = new MarkNotificationAsReadUseCase(notificationPort);
        useCase.markAllAsRead(1L);
        verify(notificationPort).markAllAsRead(1L);
    }
}

