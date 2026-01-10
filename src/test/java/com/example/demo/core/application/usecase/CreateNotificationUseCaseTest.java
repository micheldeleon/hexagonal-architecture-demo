package com.example.demo.core.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.core.application.service.NotificationSseService;
import com.example.demo.core.domain.models.Notification;
import com.example.demo.core.domain.models.NotificationType;
import com.example.demo.core.ports.out.NotificationPort;

@ExtendWith(MockitoExtension.class)
class CreateNotificationUseCaseTest {

    @Mock
    private NotificationPort notificationPort;
    @Mock
    private NotificationSseService notificationSseService;

    @Test
    void createNotification_validatesRequiredFields() {
        CreateNotificationUseCase useCase = new CreateNotificationUseCase(notificationPort, notificationSseService);

        assertThatThrownBy(() -> useCase.createNotification(null, NotificationType.WELCOME, "t", "m", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.createNotification(1L, null, "t", "m", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.createNotification(1L, NotificationType.WELCOME, " ", "m", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createNotification_persistsAndPushesViaSse() {
        CreateNotificationUseCase useCase = new CreateNotificationUseCase(notificationPort, notificationSseService);

        Notification saved = new Notification(1L, NotificationType.WELCOME, "t", "m", null);
        saved.setId(99L);
        when(notificationPort.save(any(Notification.class))).thenReturn(saved);

        Notification result = useCase.createNotification(1L, NotificationType.WELCOME, "t", "m", null);
        assertThat(result).isSameAs(saved);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationPort).save(captor.capture());
        assertThat(captor.getValue().isRead()).isFalse();

        verify(notificationSseService).sendNotificationToUser(eq(1L), eq(saved));
    }
}

