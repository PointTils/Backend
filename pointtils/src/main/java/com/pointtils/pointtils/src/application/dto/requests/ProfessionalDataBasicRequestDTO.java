package com.pointtils.pointtils.src.application.dto.requests;

import java.math.BigDecimal;

import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalDataBasicRequestDTO {

    @Pattern(regexp = "^\\d{14}$", message = "CNPJ precisa ter 14 dígitos")
    @Size(min = 14, max = 14, message = "CNPJ deve ter exatamente 14 digitos")
    private String cnpj;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private Boolean imageRights;
    private InterpreterModality modality;
    private String description;
}
