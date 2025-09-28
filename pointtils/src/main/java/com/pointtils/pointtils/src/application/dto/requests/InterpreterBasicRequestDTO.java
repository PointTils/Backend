package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InterpreterBasicRequestDTO extends PersonCreationRequestDTO {

    @Valid
    @JsonProperty("professional_data")
    private ProfessionalDataBasicRequestDTO professionalData;

    @Valid
    private List<LocationRequestDTO> locations;
}
