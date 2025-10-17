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
import com.pointtils.pointtils.src.application.dto.requests.InterpreterPatchDocumentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterDocumentResponseDTO;
import com.pointtils.pointtils.src.application.services.InterpreterDocumentService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/interpreter-documents/")
@RequiredArgsConstructor
public class InterpreterDocumentController {

    private final InterpreterDocumentService interpreterDocumentService;

    @PostMapping(value = "/{id}/", consumes = "multipart/form-data")
    @Operation(summary = "Salva múltiplos documentos para um usuário")
    public ResponseEntity<List<InterpreterDocumentResponseDTO>> saveDocuments(
        @PathVariable UUID id,
        @RequestParam("files") List<MultipartFile> files) throws IOException {
    List<InterpreterDocumentResponseDTO> response = interpreterDocumentService.saveDocuments(id, files);
    return ResponseEntity.ok(response);
}

    @GetMapping("/{id}")
    @Operation(summary = "Busca todos os documentos de um usuario")
    public ResponseEntity<List<InterpreterDocumentResponseDTO>> getDocumentsByInterpreter(
            @PathVariable ("id") UUID interpreterId) {
        List<InterpreterDocumentResponseDTO> documents = interpreterDocumentService.getDocumentsByInterpreter(interpreterId);
        return ResponseEntity.ok(documents);
    }

    @PatchMapping("/{id}/{documentId}")
    @Operation(summary = "Atualiza um documento de um usuario")
    public ResponseEntity<InterpreterDocumentResponseDTO> uploadDocument(
        @PathVariable UUID id,
        @PathVariable UUID documentId,
        @RequestParam("file") MultipartFile file) throws IOException {
    InterpreterPatchDocumentRequestDTO request = new InterpreterPatchDocumentRequestDTO(id, documentId, file);
    InterpreterDocumentRequestDTO convertedRequest = new InterpreterDocumentRequestDTO(request.getInterpreterId(), request.getFile());
    InterpreterDocumentResponseDTO updatedDocument = interpreterDocumentService.updateDocument(documentId, convertedRequest);
    return ResponseEntity.ok(updatedDocument);
}
}
