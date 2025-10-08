package com.pointtils.pointtils.src.application.dto.responses;

import java.util.UUID;

import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterpreterDocumentResponseDTO {
    private boolean success;
    private String message;
    private DocumentData data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DocumentData {
        private Long id;
        private Long interpreterId;
        private String document;
    }

    public static InterpreterDocumentResponseDTO fromEntity(Interpreter interpreter, UUID documentId, String documentUrl) {
        InterpreterDocumentResponseDTO response = new InterpreterDocumentResponseDTO();
        response.success = true;
        response.message = "Documento enviado com sucesso";
        response.data = new DocumentData(
            documentId,
            interpreter.getId(),
            documentUrl
        );
        return response;
    }
}