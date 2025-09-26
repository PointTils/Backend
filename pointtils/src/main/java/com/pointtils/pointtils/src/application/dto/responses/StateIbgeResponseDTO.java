package com.pointtils.pointtils.src.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StateIbgeResponseDTO {

    private Long id;

    @JsonProperty("sigla")
    private String abbreviation;

    @JsonProperty("nome")
    private String name;
}
