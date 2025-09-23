package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterpreterPatchRequestDTO {

    private String name;

    @Email(message = "Email inválido")
    private String email;

    @Size(max = 11, message = "O telefone deve ter no máximo 11 dígitos")
    @Pattern(regexp = "^\\d+$", message = "Número de telefone inválido")
    private String phone;

    @Pattern(regexp = "^[MFO]$", message = "Gênero deve ser M,F ou O")
    private String gender;

    private LocalDate birthday;

    private String picture;

    @Valid
    private List<LocationRequestDTO> locations;

    @Valid
    @JsonProperty("professional_data")
    private ProfessionalDataPatchRequestDTO professionalData;
}
