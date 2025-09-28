package com.pointtils.pointtils.src.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class EnterpriseResponseDTO extends UserResponseDTO {

    @JsonProperty("corporate_reason")
    private String corporateReason;
    private String cnpj;
}
