package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterprisePatchRequestDTO {
    @JsonProperty("corporate_reason")
    private String corporateReason;

    @Pattern(regexp = "^\\d{14}$", message = "CNPJ precisa ter 14 dígitos")
    private String cnpj;

    @Email(message = "Email inválido")
    private String email;

    private String password;

    @Pattern(regexp = "^\\d+$", message = "Número de telefone inválido")
    private String phone;

    private String picture;

    private String status;

    private String type;
}
