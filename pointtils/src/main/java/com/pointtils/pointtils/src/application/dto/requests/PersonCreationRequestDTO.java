package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pointtils.pointtils.src.application.dto.UserSpecialtyDTO;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PersonCreationRequestDTO extends UserRequestDTO {

    @NotBlank(message = "Nome deve ser preenchido")
    private String name;

    @NotNull(message = "Gênero deve ser preenchido")
    private Gender gender;

    @NotNull(message = "Data de nascimento deve ser preenchida")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @NotBlank(message = "CPF deve ser preenchido")
    @Pattern(regexp = "^\\d{11}$", message = "CPF inválido")
    @Size(min = 11, max = 11, message = "CPF deve ter exatamente 11 digitos")
    private String cpf;

    private List<UserSpecialtyDTO> specialties;
}