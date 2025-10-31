package com.pointtils.pointtils.src.application.controllers;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pointtils.pointtils.src.application.dto.requests.UserPicturePostRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.application.services.UserPictureService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Picture Controller", description = "Endpoints para gerenciamento de fotos de usuários")
public class UserPictureController {

    private final UserPictureService userPictureService;

    @PostMapping(value = "/{id}/picture", consumes = "multipart/form-data")
    @Operation(summary = "Upload de foto de perfil do usuário")
    public ResponseEntity<UserResponseDTO> uploadPicture(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) throws IOException {
        UserPicturePostRequestDTO request = new UserPicturePostRequestDTO(id, file);
        UserResponseDTO response = userPictureService.updatePicture(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/picture")
    @Operation(summary = "Deleta foto de perfil do usuário")
    public ResponseEntity<Void> deletePicture(@PathVariable UUID id) {
        userPictureService.deletePicture(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Handler para quando o upload de fotos está desabilitado
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<String> handleUnsupportedOperation(UnsupportedOperationException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ex.getMessage());
    }

    /**
     * Handler para quando o usuário não é encontrado
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
}
