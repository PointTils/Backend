package com.pointtils.pointtils.src.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pointtils.pointtils.src.application.dto.LocationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterpreterListResponseDTO {

    private UUID id;
    private String name;
    private String picture;
    private List<SpecialtyResponseDTO> specialties;
    private List<LocationDTO> locations;
    @JsonProperty("professional_data")
    private ProfessionalDataListResponseDTO professionalData;
}
