package com.pointtils.pointtils.src.application.dto.requests;


import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.pointtils.pointtils.src.application.dto.LocationDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeafRequestDTO {
    
    @JsonUnwrapped
    @Valid
    private PersonalRequestDTO personalRequestDTO;

    @NotNull(message = "Location is required")
    @Valid
    private LocationDTO location;
    
}
