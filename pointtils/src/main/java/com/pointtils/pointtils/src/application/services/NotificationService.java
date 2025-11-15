package com.pointtils.pointtils.src.application.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.pointtils.pointtils.src.core.domain.entities.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final String ZONE_ID = "America/Sao_Paulo";
    private final UserAppService userAppService;
    private final TaskScheduler notificationTaskScheduler;

    public void sendNotificationToUser(UUID userId, NotificationType type) {
        var userApps = userAppService.getUserAppsByUserId(userId);
        for (var app : userApps) {
            try {
                sendNotification(app.getToken(), type);
            } catch (FirebaseMessagingException ex) {
                log.error("Erro ao enviar notificação {} para o usuário {} e dispositivo {}",
                        type.name(), userId, app.getDeviceId(), ex);
            }
        }
    }

    public void scheduleNotificationForUser(UUID userId, NotificationType type, LocalDateTime scheduledTime) {
        if (scheduledTime.isAfter(LocalDateTime.now(ZoneId.of(ZONE_ID)))) {
            Runnable notificationTask = () -> this.sendNotificationToUser(userId, type);
            notificationTaskScheduler.schedule(notificationTask, scheduledTime.atZone(ZoneId.of(ZONE_ID)).toInstant());
        } else {
            log.info("Notificacao {} nao foi enviada por estar agendada para um horario anterior ao atual", type.name());
        }
    }

    private void sendNotification(String token, NotificationType type) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(token)
                .putData("type", type.name())
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        log.info("Notificação {} enviada com sucesso - resposta: {}", type.name(), response);
    }
}