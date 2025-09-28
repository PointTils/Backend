package com.pointtils.pointtils.src.application.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "Email deve ser preenchido")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha deve ser preenchida")
    @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
    private String password;

    @NotBlank(message = "Número de telefone deve ser preenchido")
    @Size(max = 11, message = "O telefone deve ter no máximo 11 dígitos")
    @Pattern(regexp = "^\\d+$", message = "Número de telefone inválido")
    private String phone;

    private String picture;
}
