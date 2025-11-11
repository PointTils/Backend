package com.pointtils.pointtils.src.application.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.pointtils.pointtils.src.core.domain.entities.UserApp;
import com.pointtils.pointtils.src.core.domain.entities.enums.NotificationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
    @InjectMocks
    private NotificationService notificationService;

    @Test
    void shouldSendNotificationToAllUserApps() throws FirebaseMessagingException {
        mockUserApps();

        try (MockedStatic<FirebaseMessaging> mockedFirebaseMessaging = Mockito.mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging mockedMessagingInstance = mock(FirebaseMessaging.class);
            when(mockedMessagingInstance.send(any(Message.class)))
                    .thenThrow(FirebaseMessagingException.class)
                    .thenReturn("response");
            mockedFirebaseMessaging.when(FirebaseMessaging::getInstance).thenReturn(mockedMessagingInstance);

            assertDoesNotThrow(() -> notificationService.sendNotificationToUser(userId, NotificationType.APPOINTMENT_REQUESTED));
            verify(mockedMessagingInstance, times(2)).send(any(Message.class));
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
