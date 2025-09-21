package com.pointtils.pointtils.src.application.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^\\d+$", message = "Número de telefone inválido")
    private String phone;

    @NotBlank(message = "Gênero deve ser preenchido")
    @Pattern(regexp = "^[MFO]$", message = "Gênero deve ser M,F ou O")
    private String gender;

    @NotNull(message = "Data de nascimento deve ser preenchida")
    private LocalDate birthday;

    @NotBlank(message = "CPF deve ser preenchido")
    @Pattern(regexp = "^\\d{11}$", message = "CPF inválido")
    private String cpf;

    @Pattern(regexp = "^\\d{14}$", message = "CNPJ precisa ter 14 dígitos")
    private String cnpj;

    private String picture;
}
