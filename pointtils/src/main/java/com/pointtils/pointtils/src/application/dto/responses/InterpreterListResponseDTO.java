package com.pointtils.pointtils.src.application.dto.responses;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterpreterListResponseDTO {
    private UUID id;
    private String name;
    private float rating;
    private float minValue;
    private float maxValue;
    private InterpreterModality modality;
    private List<LocationDTO> locations;
    @JsonProperty("profile_picture")
    private String profilePicture;
}
