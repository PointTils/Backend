package com.pointtils.pointtils.src.application.controllers;

import lombok.RequiredArgsConstructor;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

import com.pointtils.pointtils.src.application.dto.requests.UserPicturePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.application.services.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.pointtils.pointtils.src.core.domain.entities.User;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/{id}/picture")
    public ResponseEntity<UserResponseDTO> uploadPicture(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        try {
            UserPicturePatchRequestDTO request = new UserPicturePatchRequestDTO(id, file);
            UserResponseDTO response = userService.updatePicture(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}