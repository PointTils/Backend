package com.pointtils.pointtils.src.application.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.pointtils.pointtils.src.core.domain.entities.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserAppService userAppService;

    public void sendNotificationToUser(UUID userId, NotificationType type, Map<String, String> data) {
        var userApps = userAppService.getUserAppsByUserId(userId);
        for (var app : userApps) {
            try {
                sendNotification(app.getToken(), type, data);
            } catch (FirebaseMessagingException ex) {
                log.error("Erro ao enviar notificação {} para o usuário {} e dispositivo {}",
                        type.name(), userId, app.getDeviceId(), ex);
            }
        }
    }

    public void sendNotificationToUser(UUID userId, String title, String body) {
        var userApps = userAppService.getUserAppsByUserId(userId);
        for (var app : userApps) {
            try {
                sendNotification(app.getToken(), title, body);
            } catch (FirebaseMessagingException ex) {
                log.error("Erro ao enviar notificação para o usuário {} e dispositivo {}: {}",
                        userId, app.getDeviceId(), title, ex);
            }
        }
    }

    public void sendNotification(String token, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        log.info("Notificação enviada com sucesso: {}", response);
    }

    private void sendNotification(String token, NotificationType type, Map<String, String> data) throws FirebaseMessagingException {
        data.put("type", type.name());
        Message message = Message.builder()
                .setToken(token)
                .putAllData(data)
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        log.info("Notificação {} enviada com sucesso para dados {} - resposta: {}", type.name(), data, response);
    }
}