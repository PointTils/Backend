package com.pointtils.pointtils.src.application.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParametersBasicRequestDTO {
	
    @NotBlank(message = "Key deve ser preenchida")
    private String key;

    @NotBlank(message = "Value deve ser preenchido")
    private String value;
}
