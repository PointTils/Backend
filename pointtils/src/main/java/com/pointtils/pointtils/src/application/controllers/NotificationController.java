package com.pointtils.pointtils.src.application.controllers;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.services.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseDTO<String> sendPush(@RequestParam String token,
                                           @RequestParam String title,
                                           @RequestParam String body) throws FirebaseMessagingException {
        notificationService.sendNotification(token, title, body);
        return ApiResponseDTO.success("Notificação enviada com sucesso", null);
    }
}
