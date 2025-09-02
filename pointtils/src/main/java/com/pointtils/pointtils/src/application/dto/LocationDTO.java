package com.pointtils.pointtils.src.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LocationDTO {
    
    @NotBlank(message = "UF is required")
    private String uf;
    
    @NotBlank(message = "City is required")
    private String city;

}
