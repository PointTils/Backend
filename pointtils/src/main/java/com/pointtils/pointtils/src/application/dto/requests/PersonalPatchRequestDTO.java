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
    
    @Email(message = "Email must be valid")
    private String email;
    
    private String password;
    
    private String phone;
    
    @Pattern(regexp = "^[MFO]$", message = "Gender must be M,F or O")
    private String gender;
    
    private LocalDate birthday;
    
    private String cpf;
    
    private String picture;
}
