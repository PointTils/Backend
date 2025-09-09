package com.pointtils.pointtils.src.application.dto.responses;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalInfoResponseDTO {
    private String cnpj;
    private BigDecimal rating;
    
    @JsonProperty("min_value")
    private BigDecimal minValue;
    
    @JsonProperty("max_value")
    private BigDecimal maxValue;
    
    private String modality;
    private String description;
    
    @JsonProperty("image_rights")
    private Boolean imageRights;
}