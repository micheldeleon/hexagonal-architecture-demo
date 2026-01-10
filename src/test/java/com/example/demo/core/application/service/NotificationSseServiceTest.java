package com.example.demo.core.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.core.domain.models.Notification;
import com.example.demo.core.domain.models.NotificationType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class NotificationSseServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void createEmitter_registersEmitterAndSendsConnectedEvent() {
        NotificationSseService service = new NotificationSseService(new ObjectMapper());
        SseEmitter emitter = service.createEmitter(1L);
        assertThat(emitter).isNotNull();
        assertThat(service.getActiveConnectionsCount()).isEqualTo(1);
    }

    @Test
    void sendNotificationToUser_removesEmitterWhenSerializationFails() throws JsonProcessingException {
        NotificationSseService service = new NotificationSseService(objectMapper);
        service.createEmitter(1L);
        assertThat(service.getActiveConnectionsCount()).isEqualTo(1);

        doThrow(new JsonProcessingException("boom") {
        }).when(objectMapper).writeValueAsString(org.mockito.ArgumentMatchers.any());

        Notification notification = new Notification(1L, NotificationType.WELCOME, "t", "m", 10L);
        notification.setId(1L);
        service.sendNotificationToUser(1L, notification);
        assertThat(service.getActiveConnectionsCount()).isEqualTo(0);
    }

    @Test
    void sendHeartbeatToAll_removesEmitterWhenSendFails() {
        NotificationSseService service = new NotificationSseService(new ObjectMapper());

        @SuppressWarnings("unchecked")
        Map<Long, SseEmitter> emitters = (ConcurrentHashMap<Long, SseEmitter>) ReflectionTestUtils.getField(service,
                "emitters");
        SseEmitter failing = new SseEmitter();
        SseEmitter spy = org.mockito.Mockito.spy(failing);
        try {
            doThrow(new IOException("send failed")).when(spy).send(org.mockito.ArgumentMatchers.any(SseEmitter.SseEventBuilder.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        emitters.put(99L, spy);

        assertThat(service.getActiveConnectionsCount()).isEqualTo(1);
        service.sendHeartbeatToAll();
        assertThat(service.getActiveConnectionsCount()).isEqualTo(0);
    }
}
