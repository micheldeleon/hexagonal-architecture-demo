package com.example.demo.core.application.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationHeartbeatSchedulerTest {

    @Mock
    private NotificationSseService notificationSseService;

    @Test
    void sendHeartbeat_sendsOnlyWhenThereAreActiveConnections() {
        NotificationHeartbeatScheduler scheduler = new NotificationHeartbeatScheduler(notificationSseService);

        when(notificationSseService.getActiveConnectionsCount()).thenReturn(0);
        scheduler.sendHeartbeat();
        verify(notificationSseService, never()).sendHeartbeatToAll();

        when(notificationSseService.getActiveConnectionsCount()).thenReturn(2);
        scheduler.sendHeartbeat();
        verify(notificationSseService).sendHeartbeatToAll();
    }
}

