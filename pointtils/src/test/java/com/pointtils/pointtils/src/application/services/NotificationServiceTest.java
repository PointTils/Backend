package com.pointtils.pointtils.src.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.pointtils.pointtils.src.application.dto.responses.ParametersResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.UserApp;
import com.pointtils.pointtils.src.core.domain.entities.enums.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    private static final UUID userId = UUID.fromString("5c81cdb8-db52-4264-93c8-d010e1511464");
    @Mock
    private TaskScheduler taskScheduler;
    @Mock
    private UserAppService userAppService;
    @Mock
    private ParametersService parametersService;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();
    @InjectMocks
    private NotificationService notificationService;

    @ParameterizedTest
    @CsvSource(value = {
            "APPOINTMENT_REQUESTED,NOTIFICATION_APPOINTMENT_REQUESTED",
            "APPOINTMENT_CANCELED,NOTIFICATION_APPOINTMENT_CANCELED",
            "APPOINTMENT_ACCEPTED,NOTIFICATION_APPOINTMENT_ACCEPTED"
    })
    void shouldSendNotificationToAllUserApps(String notificationTypeStr, String parameterKey) throws FirebaseMessagingException {
        mockUserApps();
        String mockedParameterValue = "{\"title\":\"Nova notificação\"," +
                "\"body\":\"Você recebeu uma atualização importante. Clique para mais detalhes.\"}";
        ParametersResponseDTO mockResponse = ParametersResponseDTO.builder()
                .id(UUID.randomUUID())
                .key(parameterKey)
                .value(mockedParameterValue)
                .build();
        when(parametersService.findByKey(parameterKey)).thenReturn(mockResponse);

        NotificationType notificationType = NotificationType.valueOf(notificationTypeStr);

        try (MockedStatic<FirebaseMessaging> mockedFirebaseMessaging = Mockito.mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging mockedMessagingInstance = mock(FirebaseMessaging.class);
            when(mockedMessagingInstance.send(any(Message.class)))
                    .thenThrow(FirebaseMessagingException.class)
                    .thenReturn("response");
            mockedFirebaseMessaging.when(FirebaseMessaging::getInstance).thenReturn(mockedMessagingInstance);

            assertDoesNotThrow(() -> notificationService.sendNotificationToUser(userId, notificationType));
            verify(mockedMessagingInstance, times(2)).send(any(Message.class));
            verify(parametersService, times(2)).findByKey(parameterKey);
        }
    }

    @Test
    void shouldNotSendNotificationToAllUserAppsIfParameterNotFound() throws FirebaseMessagingException {
        mockUserApps();
        when(parametersService.findByKey("NOTIFICATION_APPOINTMENT_ACCEPTED")).thenThrow(new EntityNotFoundException());

        try (MockedStatic<FirebaseMessaging> mockedFirebaseMessaging = Mockito.mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging mockedMessagingInstance = mock(FirebaseMessaging.class);
            mockedFirebaseMessaging.when(FirebaseMessaging::getInstance).thenReturn(mockedMessagingInstance);

            assertDoesNotThrow(() -> notificationService.sendNotificationToUser(userId, NotificationType.APPOINTMENT_ACCEPTED));
            verify(mockedMessagingInstance, never()).send(any(Message.class));
            verify(parametersService, times(2)).findByKey("NOTIFICATION_APPOINTMENT_ACCEPTED");
        }
    }

    @Test
    void shouldNotSendNotificationIfUserHasNoApps() {
        when(userAppService.getUserAppsByUserId(userId)).thenReturn(Collections.emptyList());

        try (MockedStatic<FirebaseMessaging> mockedFirebaseMessaging = Mockito.mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging mockedMessagingInstance = mock(FirebaseMessaging.class);
            mockedFirebaseMessaging.when(FirebaseMessaging::getInstance).thenReturn(mockedMessagingInstance);

            assertDoesNotThrow(() -> notificationService.sendNotificationToUser(userId, NotificationType.APPOINTMENT_REQUESTED));
            verifyNoInteractions(mockedMessagingInstance);
        }
    }

    @Test
    void shouldScheduleNotificationTaskForFutureTime() {
        NotificationType type = NotificationType.APPOINTMENT_REQUESTED;
        LocalDateTime futureTime = LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).plusHours(1);

        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);

        notificationService.scheduleNotificationForUser(userId, type, futureTime);

        verify(taskScheduler).schedule(taskCaptor.capture(), eq(futureTime.atZone(ZoneId.of("America/Sao_Paulo")).toInstant()));
        Runnable scheduledTask = taskCaptor.getValue();
        assertNotNull(scheduledTask);

        scheduledTask.run();
        verify(userAppService).getUserAppsByUserId(userId);
    }

    @Test
    void shouldLogMessageForPastTime() {
        NotificationType type = NotificationType.APPOINTMENT_REQUESTED;
        LocalDateTime pastTime = LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).minusHours(1);

        notificationService.scheduleNotificationForUser(userId, type, pastTime);

        verifyNoInteractions(taskScheduler);
    }

    private void mockUserApps() {
        UserApp firstUserApp = UserApp.builder()
                .deviceId("629c3fbd-770a-49b4-930d-68d8de4492ce")
                .platform("android")
                .token("token1")
                .build();
        UserApp secondUserApp = UserApp.builder()
                .deviceId("365c3fbd-770a-49b4-930d-68d8de4492ce")
                .platform("android")
                .token("token2")
                .build();
        when(userAppService.getUserAppsByUserId(userId)).thenReturn(List.of(firstUserApp, secondUserApp));
    }
}
