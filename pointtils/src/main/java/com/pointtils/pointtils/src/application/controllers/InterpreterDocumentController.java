package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterDocumentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterDocumentResponseDTO;
import com.pointtils.pointtils.src.application.services.InterpreterDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/interpreter-documents/")
@RequiredArgsConstructor
public class InterpreterDocumentController {

    private final InterpreterDocumentService interpreterDocumentService;

    @PostMapping(value = "/{id}", consumes = "multipart/form-data")
    @Operation(summary = "Salva múltiplos documentos para um usuário")
    public ResponseEntity<InterpreterDocumentResponseDTO> saveDocuments(
            @PathVariable UUID id,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("replace_existing") Boolean replaceExisting) {
        InterpreterDocumentResponseDTO response = interpreterDocumentService.saveDocuments(id, files, replaceExisting);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Busca todos os documentos de um usuario")
    public ResponseEntity<InterpreterDocumentResponseDTO> getDocumentsByInterpreter(
            @PathVariable("id") UUID interpreterId) {
        InterpreterDocumentResponseDTO documents = interpreterDocumentService.getDocumentsByInterpreter(interpreterId);
        return ResponseEntity.ok(documents);
    }

    @PatchMapping("/{id}/{documentId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualiza um documento de um usuario")
    public ResponseEntity<InterpreterDocumentResponseDTO> updateDocument(
            @PathVariable UUID id,
            @PathVariable UUID documentId,
            @RequestParam("file") MultipartFile file) {
        InterpreterDocumentRequestDTO convertedRequest = new InterpreterDocumentRequestDTO(id, file);
        InterpreterDocumentResponseDTO updatedDocument = interpreterDocumentService.updateDocument(documentId, convertedRequest);
        return ResponseEntity.ok(updatedDocument);
    }
}
