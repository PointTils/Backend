package com.pointtils.pointtils.src.application.dto.requests;

import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PersonPatchRequestDTO {

    private String name;

    private Gender gender;

    private LocalDate birthday;

    @Email(message = "Formato de e-mail inválido")
    private String email;

    @Size(max = 11, message = "O telefone deve ter no máximo 11 dígitos")
    private String phone;

    private String picture;
}
