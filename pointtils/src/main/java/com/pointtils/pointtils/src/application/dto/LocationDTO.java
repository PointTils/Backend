package com.pointtils.pointtils.src.application.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LocationDTO {

    private UUID id;

    @NotBlank(message = "UF deve ser preenchida")
    private String uf;

    @NotBlank(message = "Cidade deve ser preenchida")
    private String city;

    @NotBlank(message = "Bairro deve ser preenchido")
    private String neighborhood;

    public LocationDTO(String uf, String city, String neighborhood) {
        this.uf = uf;
        this.city = city;
        this.neighborhood = neighborhood;
    }
}
