package com.pointtils.pointtils.src.application.dto.responses;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pointtils.pointtils.src.core.domain.entities.InterpreterDocuments;

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
        private UUID id;

        @JsonProperty("interpreter_id")
        private UUID interpreterId;

        private String document;
    }

    public static InterpreterDocumentResponseDTO fromEntity(InterpreterDocuments document) {
        InterpreterDocumentResponseDTO response = new InterpreterDocumentResponseDTO();
        response.success = true;
        response.message = "Documento enviado com sucesso";
        response.data = new DocumentData(
            document.getId(),
            document.getInterpreter().getId(),
            document.getDocument()
        );
        return response;
    }
}