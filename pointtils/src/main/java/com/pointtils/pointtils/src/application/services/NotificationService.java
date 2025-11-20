package com.pointtils.pointtils.src.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.pointtils.pointtils.src.core.domain.entities.enums.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final String ZONE_ID = "America/Sao_Paulo";
    private final UserAppService userAppService;
    private final ParametersService parametersService;
    private final TaskScheduler notificationTaskScheduler;
    private final ObjectMapper objectMapper;

    public void sendNotificationToUser(UUID userId, NotificationType type) {
        var userApps = userAppService.getUserAppsByUserId(userId);
        for (var app : userApps) {
            try {
                sendNotification(app.getToken(), type);
            } catch (Exception ex) {
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
        ObjectNode notificationContent = getNotificationContent(type);
        Message message = Message.builder()
                .setToken(token)
                .setNotification(
                        Notification.builder()
                                .setTitle(notificationContent.get("title").asText())
                                .setBody(notificationContent.get("body").asText())
                                .build()
                )
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        log.info("Notificação {} enviada com sucesso - resposta: {}", type.name(), response);
    }

    private ObjectNode getNotificationContent(NotificationType type) {
        String key = switch (type) {
            case APPOINTMENT_REQUESTED -> "NOTIFICATION_APPOINTMENT_REQUESTED";
            case APPOINTMENT_ACCEPTED -> "NOTIFICATION_APPOINTMENT_ACCEPTED";
            case APPOINTMENT_CANCELED -> "NOTIFICATION_APPOINTMENT_CANCELED";
            case APPOINTMENT_REMINDER -> "NOTIFICATION_APPOINTMENT_REMINDER";
        };
        try {
            var foundParameter = parametersService.findByKey(key);
            return (ObjectNode) objectMapper.readTree(foundParameter.getValue());
        } catch (EntityNotFoundException | ClassCastException | JsonProcessingException ex) {
            return objectMapper.createObjectNode();
        }
    }
}