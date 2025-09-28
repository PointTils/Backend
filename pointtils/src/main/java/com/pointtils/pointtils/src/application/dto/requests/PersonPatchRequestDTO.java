package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PersonPatchRequestDTO extends UserPatchRequestDTO {

    private String name;

    private Gender gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Pattern(regexp = "^\\d{11}$", message = "CPF inválido")
    @Size(min = 11, max = 11, message = "CPF deve ter exatamente 11 digitos")
    private String cpf;
}
