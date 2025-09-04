package com.pointtils.pointtils.src.application.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalDataDTO {
    
    @NotBlank(message = "CNPJ is required")
    @Pattern(regexp = "\\d{14}", message = "CNPJ must have 14 digits")
    private String cnpj;
    
    @NotNull(message = "Rating is required")
    @DecimalMin(value = "0.0", message = "Rating must be non-negative")
    private Double rating;
    
    @NotNull(message = "Minimum value is required")
    @DecimalMin(value = "0.0", message = "Minimum value must be positive")
    private BigDecimal minValue;
    
    @NotNull(message = "Maximum value is required")
    @DecimalMin(value = "0.0", message = "Maximum value must be positive")
    private BigDecimal maxValue;
    
    @NotBlank(message = "Modality is required")
    @Pattern(regexp = "^(presencial|online|ambos)$", message = "Modality must be 'presencial', 'online' or 'ambos'")
    private String modality;
    
    private String description;
    
    @NotNull(message = "Image rights agreement is required")
    private Boolean imageRights;
}
