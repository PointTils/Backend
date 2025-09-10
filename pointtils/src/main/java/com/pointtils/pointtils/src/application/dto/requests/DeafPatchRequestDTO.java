package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.pointtils.pointtils.src.application.dto.LocationDTO;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeafPatchRequestDTO {
    
    @JsonUnwrapped
    @Valid
    private PersonalPatchRequestDTO personalRequestDTO;

    @Valid
    private LocationDTO location;
    
}
