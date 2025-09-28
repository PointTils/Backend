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
public class InterpreterPatchRequestDTO extends PersonPatchRequestDTO {

    @Valid
    private List<LocationRequestDTO> locations;

    @Valid
    @JsonProperty("professional_data")
    private ProfessionalDataPatchRequestDTO professionalData;
}
