package com.pointtils.pointtils.src.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterpreterResponseDTO {

    // Dados do usuario
    private UUID id;
    private String email;
    private String type;
    private String status;
    private String phone;
    private String picture;

    // Dados da pessoa
    private String name;
    private Gender gender;
    private LocalDate birthday;
    private String cpf;

    private List<LocationDTO> locations;

    private List<SpecialtyResponseDTO> specialties;

    @JsonProperty("professional_data")
    private ProfessionalDataResponseDTO professionalData;
}
