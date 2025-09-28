package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.UserPicturePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.application.services.UserPicturePatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.cloud.aws.s3.enabled", havingValue = "true")
public class UserPicturePatchController {

    private final UserPicturePatchService userService;

    @PatchMapping(value = "/{id}/picture", consumes = "multipart/form-data")
    public ResponseEntity<UserResponseDTO> uploadPicture(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) throws IOException {
        UserPicturePatchRequestDTO request = new UserPicturePatchRequestDTO(id, file);
        UserResponseDTO response = userService.updatePicture(request);
        return ResponseEntity.ok(response);
    }
}