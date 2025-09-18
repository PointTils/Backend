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
public class InterpreterBasicRequestDTO {
    
    @NotNull(message = "Dados pessoais devem ser preenchidos")
    @Valid
    @JsonProperty("personal_data")
    private PersonalRequestDTO personalData;
    
    @NotNull(message = "Localização deve ser preenchida")
    @Valid
    private LocationDTO location;
}
