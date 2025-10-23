package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ProfessionalDataPatchRequestDTO {

    @Pattern(regexp = "^\\d{14}$", message = "CNPJ precisa ter 14 dígitos")
    @Size(min = 14, max = 14, message = "CNPJ deve ter exatamente 14 digitos")
    private String cnpj;

    private InterpreterModality modality;

    private String description;

    @JsonProperty("image_rights")
    private Boolean imageRights;
}
