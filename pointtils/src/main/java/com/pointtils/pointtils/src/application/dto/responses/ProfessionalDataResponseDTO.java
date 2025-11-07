package com.pointtils.pointtils.src.application.dto.responses;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalDataResponseDTO {
    private String cnpj;
    private BigDecimal rating;
    
    private InterpreterModality modality;
    private String description;
    
    @JsonProperty("image_rights")
    private Boolean imageRights;
}