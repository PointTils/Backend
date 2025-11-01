package com.pointtils.pointtils.src.application.dto.requests;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterpreterPatchDocumentRequestDTO {

    @NotNull(message = "O ID do usuário é obrigatório")
    @JsonProperty("user_id")
    private UUID interpreterId;

    @NotNull(message = "O ID do documento é obrigatório")
    @JsonProperty("document_id")
    private UUID documentId;

    @NotNull(message = "O arquivo é obrigatório")
    private MultipartFile file;
}