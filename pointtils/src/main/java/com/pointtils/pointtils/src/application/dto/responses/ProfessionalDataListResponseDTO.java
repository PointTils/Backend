package com.pointtils.pointtils.src.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
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
    private InterpreterModality modality;
}
