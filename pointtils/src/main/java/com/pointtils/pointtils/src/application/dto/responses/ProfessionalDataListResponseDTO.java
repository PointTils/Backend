package com.pointtils.pointtils.src.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalDataListResponseDTO {

    private BigDecimal rating;
    @JsonProperty("min_value")
    private BigDecimal minValue;
    @JsonProperty("max_value")
    private BigDecimal maxValue;
    private String modality;
}
