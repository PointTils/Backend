package com.pointtils.pointtils.src.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentFilterResponseDTO extends AppointmentResponseDTO {

    @JsonProperty("contact_data")
    private ContactDataResponseDTO contactData;
}
