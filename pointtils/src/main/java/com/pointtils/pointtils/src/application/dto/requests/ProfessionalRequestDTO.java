package com.pointtils.pointtils.src.application.dto.requests;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalRequestDTO {
    
    @NotBlank(message = "CNPJ is required")
    @Pattern(regexp = "\\d{14}", message = "CNPJ must have 14 digits")
    private String cnpj;
    
    @NotNull(message = "Minimum value is required")
    @DecimalMin(value = "0.0", message = "Minimum value must be positive")
    @JsonProperty("min_value")  // Mapeia snake_case para camelCase
    private BigDecimal minValue;
    
    @NotNull(message = "Maximum value is required")
    @DecimalMin(value = "0.0", message = "Maximum value must be positive")
    @JsonProperty("max_value")
    private BigDecimal maxValue;
    
    @NotBlank(message = "Modality is required")
    @Pattern(regexp = "^(presencial|online|ambos)$", message = "Modality must be 'presencial', 'online' or 'ambos'")
    private String modality;
    
    private String description;
    
    @NotNull(message = "Image rights agreement is required")
    @JsonProperty("image_rights")
    private Boolean imageRights;
}
