package com.pointtils.pointtils.src.application.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDataDTO {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotBlank(message = "Phone is required")
    private String phone;
    
    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^[MF]$", message = "Gender must be M or F")
    private String gender;
    
    @NotNull(message = "Birthday is required")
    private LocalDate birthday;
    
    @NotBlank(message = "CPF is required")
    private String cpf;
    
    private String picture; 
}
