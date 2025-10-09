package com.pointtils.pointtils.src.application.controllers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterDocumentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterDocumentResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.application.services.InterpreterDocumentService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/users/interpreter-documents/")
@RequiredArgsConstructor
public class InterpreterDocumentController {

    private final InterpreterDocumentService interpreterDocumentService;

    @PostMapping(value = "/{id}/document", consumes = "multipart/form-data")
    public ResponseEntity<InterpreterDocumentResponseDTO> saveDocument(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) throws IOException {
        InterpreterDocumentRequestDTO request = new InterpreterDocumentRequestDTO(id, file);
        InterpreterDocumentResponseDTO response = interpreterDocumentService.saveDocument(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca todos os documentos de um usuario")
    public ResponseEntity<List<InterpreterDocumentResponseDTO>> getDocumentsByInterpreter(
            @PathVariable UUID interpreterId) {
        List<InterpreterDocumentResponseDTO> documents = interpreterDocumentService.getDocumentsByInterpreter(interpreterId);
        return ResponseEntity.ok(documents);
    }

    @PatchMapping("/{id}/{documentId}")
    @Operation(summary = "Atualiza um documento de um usuario")
    public ResponseEntity<InterpreterDocumentResponseDTO> uploadDocument(
            @PathVariable UUID id,
            @PathVariable UUID documentId,
            @RequestParam("file") MultipartFile file) throws IOException {
        InterpreterDocumentRequestDTO request = new InterpreterDocumentRequestDTO(id, documentId, file);
        InterpreterDocumentResponseDTO updatedDocument = interpreterDocumentService.updateDocument(request);
        return ResponseEntity.ok(updatedDocument);
    }
}
