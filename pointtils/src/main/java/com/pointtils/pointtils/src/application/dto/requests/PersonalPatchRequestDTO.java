package com.pointtils.pointtils.src.application.dto.requests;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalPatchRequestDTO {
    
    private String name;
    
    @Email(message = "Email inválido")
    private String email;
    
    private String password;

    @Pattern(regexp = "^\\d+$", message = "Número de telefone inválido")
    private String phone;
    
    @Pattern(regexp = "^[MFO]$", message = "Gênero deve ser M,F ou O")
    private String gender;
    
    private LocalDate birthday;

    @Pattern(regexp = "^\\d{11}$", message = "CPF inválido")
    private String cpf;
    
    private String picture;
}
