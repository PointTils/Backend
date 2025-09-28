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
public class LocationRequestDTO {

    @NotBlank(message = "UF deve ser preenchida")
    private String uf;

    @NotBlank(message = "Cidade deve ser preenchida")
    private String city;

    @NotBlank(message = "Bairro deve ser preenchido")
    private String neighborhood;
}
