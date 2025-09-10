package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pointtils.pointtils.src.application.dto.LocationDTO;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterpreterPatchRequestDTO {
    
    @Valid
    @JsonProperty("personal_data")
    private PersonalPatchRequestDTO personalData;
    
    @Valid
    private LocationDTO location;
    
    @Valid
    @JsonProperty("professional_data")
    private ProfessionalPatchRequestDTO professionalData;

}
