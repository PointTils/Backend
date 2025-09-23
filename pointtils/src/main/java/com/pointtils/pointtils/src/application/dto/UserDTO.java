package com.pointtils.pointtils.src.application.dto;

import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private UUID id;

    @Email(message = "Email inválido")
    @NotBlank(message = "O e-mail é obrigatório")
    private String email;

    @Size(max = 11, message = "O telefone deve ter no máximo 11 dígitos")
    @Pattern(regexp = "^\\d+$", message = "Número de telefone inválido")
    private String phone;

    private String picture;

    @NotNull(message = "O tipo de usuário é obrigatório")
    private UserTypeE type;

    @NotNull(message = "O status do usuário é obrigatório")
    private UserStatus status;
}
