package com.pointtils.pointtils.src.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pointtils.pointtils.src.application.dto.LocationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "id_interpreter",
        "user",
        "person",
        "professional_info",
        "location",
        "specialties",
        "created_at"
})
public class InterpreterResponseDTO {

    @JsonProperty("id_interpreter")
    private UUID idInterpreter;

    private UserResponseDTO user;
    private PersonResponseDTO person;

    @JsonProperty("professional_info")
    private ProfessionalInfoResponseDTO professionalInfo;

    private LocationDTO location;

    private List<SpecialtyResponseDTO> specialties;

    @JsonProperty("created_at")
    private LocalDate createdAt = LocalDate.now();

}
