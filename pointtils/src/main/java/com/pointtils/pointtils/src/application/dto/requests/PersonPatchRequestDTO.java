package com.pointtils.pointtils.src.application.dto.requests;

import java.time.LocalDate;

import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PersonPatchRequestDTO {

    private String name;

    private Gender gender;

    private LocalDate birthday;

    @Email(message = "Email inválido")
    private String email;

    @Size(max = 11, message = "O telefone deve ter no máximo 11 dígitos")
    @Pattern(regexp = "^\\d+$", message = "Número de telefone inválido")
    private String phone;

    @NotBlank(message = "CPF deve ser preenchido")
    @Pattern(regexp = "^\\d{11}$", message = "CPF inválido")
    @Size(min = 11, max = 11, message = "CPF deve ter exatamente 11 digitos")
    private String cpf;

    private String picture;
}
