package com.pointtils.pointtils.src.application.controllers;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pointtils.pointtils.src.application.dto.requests.UserPicturePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.application.services.UserPicturePostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
public class UserPicturePostController {

    private final UserPicturePostService userService;

    @PostMapping(value = "/{id}/picture", consumes = "multipart/form-data")
    public ResponseEntity<UserResponseDTO> uploadPicture(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) throws IOException {
        UserPicturePatchRequestDTO request = new UserPicturePatchRequestDTO(id, file);
        UserResponseDTO response = userService.updatePicture(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Handler para quando o upload de fotos está desabilitado
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<String> handleUnsupportedOperation(UnsupportedOperationException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ex.getMessage());
    }
}
