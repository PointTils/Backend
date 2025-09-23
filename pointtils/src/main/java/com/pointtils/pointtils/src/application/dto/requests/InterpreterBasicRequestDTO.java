package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterpreterBasicRequestDTO {

    @NotBlank(message = "Nome deve ser preenchido")
    private String name;

    @NotBlank(message = "Email deve ser preenchido")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha deve ser preenchida")
    private String password;

    @NotBlank(message = "Número de telefone deve ser preenchido")
    @Size(max = 11, message = "O telefone deve ter no máximo 11 dígitos")
    @Pattern(regexp = "^\\d+$", message = "Número de telefone inválido")
    private String phone;

    @NotNull(message = "Gênero deve ser preenchido")
    private Gender gender;

    @NotNull(message = "Data de nascimento deve ser preenchida")
    private LocalDate birthday;

    @NotBlank(message = "CPF deve ser preenchido")
    @Pattern(regexp = "^\\d{11}$", message = "CPF inválido")
    @Size(min = 11, max = 11, message = "CPF deve ter exatamente 11 digitos")
    private String cpf;

    private String picture;

    @Valid
    @JsonProperty("professional_data")
    private ProfessionalDataBasicRequestDTO professionalData;
}
