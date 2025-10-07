package com.pointtils.pointtils.src.application.controllers;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterDocumentRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.UserPicturePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.application.services.InterpreterDocumentService;
import com.pointtils.pointtils.src.application.services.InterpreterService;
import com.pointtils.pointtils.src.application.services.UserPicturePatchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
public class InterpreterDocumentController {

     private final InterpreterDocumentService userService; 

    @PostMapping(value = "/{id}/document", consumes = "multipart/form-data")
    public ResponseEntity<UserResponseDTO> saveDocument(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) throws IOException {
        InterpreterDocumentRequestDTO request = new InterpreterDocumentRequestDTO(id, file);
        UserResponseDTO response = userService.saveDocument(request);
        return ResponseEntity.ok(response);
    }
}
