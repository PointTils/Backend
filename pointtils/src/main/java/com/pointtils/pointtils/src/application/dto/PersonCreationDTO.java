package com.pointtils.pointtils.src.application.dto;

import java.time.LocalDate;
import java.util.List;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PersonCreationDTO {
    @Email(message = "Email inválido")
    @NotBlank(message = "O e-mail é obrigatório")
    private String email;

    @NotBlank(message = "A senha é obrigatório")
    @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
    private String password;

    @Size(max = 11, message = "O telefone deve ter no máximo 11 dígitos")
    @Pattern(regexp = "^\\d+$", message = "Número de telefone inválido")
    private String phone;

    private String picture;

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @NotNull(message = "O gênero é obrigatório")
    private Gender gender;

    @NotNull(message = "A data de nascimento é obrigatório")
    private LocalDate birthday;

    @NotBlank(message = "CPF é obrigatório")
    @Size(min = 11, max = 11, message = "CPF deve ter exatamente 11 digitos")
    private String cpf;

    private List<UserSpecialtyDTO> specialties;

}