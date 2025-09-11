package com.pointtils.pointtils.src.application.dto.requests;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalPatchRequestDTO {
    
    @Pattern(regexp = "\\d{14}", message = "CNPJ must have 14 digits")
    private String cnpj;
    
    @DecimalMin(value = "0.0", message = "Minimum value must be positive")
    @JsonProperty("min_value")
    private BigDecimal minValue;
    
    @DecimalMin(value = "0.0", message = "Maximum value must be positive")
    @JsonProperty("max_value")
    private BigDecimal maxValue;
    
    @Pattern(regexp = "^(presencial|online|ambos)$", message = "Modality must be 'presencial', 'online' or 'ambos'")
    private String modality;
    
    private String description;
    
    @JsonProperty("image_rights")
    private Boolean imageRights;
}
