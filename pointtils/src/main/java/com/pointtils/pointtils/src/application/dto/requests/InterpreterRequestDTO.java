package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pointtils.pointtils.src.application.dto.LocationDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterpreterRequestDTO {
    
    @NotNull(message = "Personal data is required")
    @Valid
    @JsonProperty("personal_data")
    private PersonalRequestDTO personalData;
    
    @NotNull(message = "Location is required")
    @Valid
    private LocationDTO location;
    
    @NotNull(message = "Professional data is required")
    @Valid
    @JsonProperty("professional_data")
    private ProfessionalRequestDTO professionalData;

}
