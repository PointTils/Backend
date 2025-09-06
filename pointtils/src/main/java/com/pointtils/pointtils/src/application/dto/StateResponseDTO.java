package com.pointtils.pointtils.src.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StateResponseDTO {

    private Long id;

    @JsonProperty("sigla")
    private String abbreviation;

    @JsonProperty("nome")
    private String name;
}
