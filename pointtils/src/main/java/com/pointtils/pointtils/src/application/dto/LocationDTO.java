package com.pointtils.pointtils.src.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LocationDTO {

    private UUID id;

    @NotBlank(message = "UF is required")
    private String uf;

    @NotBlank(message = "City is required")
    private String city;

    public LocationDTO(String uf, String city) {
        this.uf = uf;
        this.city = city;
    }
}
