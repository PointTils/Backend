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
    
    @NotBlank(message = "CNPJ deve ser preenchido")
    @Pattern(regexp = "^\\d{14}$", message = "CNPJ precisa ter 14 dígitos")
    private String cnpj;
    
    @NotNull(message = "Valor mínimo deve ser preenchido")
    @DecimalMin(value = "0.0", message = "Valor mínimo precisa ser positivo")
    @JsonProperty("min_value")  // Mapeia snake_case para camelCase
    private BigDecimal minValue;
    
    @NotNull(message = "Valor máximo deve ser preenchido")
    @DecimalMin(value = "0.0", message = "Valor máximo precisa ser positivo")
    @JsonProperty("max_value")
    private BigDecimal maxValue;
    
    @NotBlank(message = "Modalidade de atendimento deve ser preenchida")
    @Pattern(regexp = "^(presencial|online|ambos)$", message = "Modalidade precisa ser 'presencial', 'online' or 'ambos'")
    private String modality;
    
    private String description;
    
    @NotNull(message = "Usuário deve informar se autoriza o direito de uso de imagem")
    @JsonProperty("image_rights")
    private Boolean imageRights;
}
