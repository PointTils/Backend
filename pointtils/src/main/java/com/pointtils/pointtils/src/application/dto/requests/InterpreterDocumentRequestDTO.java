package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterpreterDocumentRequestDTO {

    @NotNull(message = "O ID do usuário é obrigatório")
    @JsonProperty("user_id")
    private UUID interpreter_Id;

    @NotNull(message = "O documento é obrigatório")
    private MultipartFile file;
}