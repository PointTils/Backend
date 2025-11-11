package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRecoveryRequestDTO {

    @JsonProperty("reset_token")
    @NotBlank(message = "Token de recuperação é obrigatório")
    private String resetToken;

    @JsonProperty("new_password")
    @NotBlank(message = "Nova senha é obrigatória")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+=-]{6,}$", 
             message = "A senha deve ter pelo menos 6 caracteres e pode conter letras, números e caracteres especiais")
    private String newPassword;
}
