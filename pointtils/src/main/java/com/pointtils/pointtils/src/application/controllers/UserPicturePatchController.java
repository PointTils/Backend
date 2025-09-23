package com.pointtils.pointtils.src.application.controllers;

import lombok.RequiredArgsConstructor;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

import com.pointtils.pointtils.src.application.dto.requests.UserPicturePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.application.services.UserPicturePatchService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserPicturePatchService userService;

    @PatchMapping(value = "/{id}/picture", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadPicture(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        try {
            UserPicturePatchRequestDTO request = new UserPicturePatchRequestDTO(id, file);
            UserResponseDTO response = userService.updatePicture(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro na validação: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado: " + e.getMessage());
        }
    }
}