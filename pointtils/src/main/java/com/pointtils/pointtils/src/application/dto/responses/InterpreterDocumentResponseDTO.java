package com.pointtils.pointtils.src.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pointtils.pointtils.src.core.domain.entities.InterpreterDocuments;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterpreterDocumentResponseDTO {
    private boolean success;
    private String message;
    private List<DocumentData> data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DocumentData {
        private UUID id;

        @JsonProperty("interpreter_id")
        private UUID interpreterId;

        private String document;
    }

    public static InterpreterDocumentResponseDTO fromEntity(List<InterpreterDocuments> documents) {
        InterpreterDocumentResponseDTO response = new InterpreterDocumentResponseDTO();
        response.success = true;
        response.message = "Documento enviado com sucesso";
        response.data = documents.stream()
                .map(document -> new DocumentData(
                        document.getId(),
                        document.getInterpreter().getId(),
                        document.getDocument()
                )).toList();
        return response;
    }
}