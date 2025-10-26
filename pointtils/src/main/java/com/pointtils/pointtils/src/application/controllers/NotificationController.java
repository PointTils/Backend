package com.pointtils.pointtils.src.application.controllers;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.services.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notifications")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notification Controller", description = "Endpoints para envio de notificações push")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDTO<String> sendPushNotificationToToken(@RequestParam String token,
                                                              @RequestParam String title,
                                                              @RequestParam String body) throws FirebaseMessagingException {
        notificationService.sendNotification(token, title, body);
        return ApiResponseDTO.success("Notificação enviada com sucesso", null);
    }

    @PostMapping("/send-to-user")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDTO<String> sendPushNotificationToUser(@RequestParam UUID userId,
                                                             @RequestParam String title,
                                                             @RequestParam String body) {
        notificationService.sendNotificationToUser(userId, title, body);
        return ApiResponseDTO.success("Notificação enviada com sucesso", null);
    }
}
