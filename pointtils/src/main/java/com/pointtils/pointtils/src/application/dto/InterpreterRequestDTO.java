package com.pointtils.pointtils.src.application.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterpreterRequestDTO {
    
    @NotNull(message = "Personal data is required")
    @Valid
    private PersonalDataDTO personalData;
    
    @NotNull(message = "Location is required")
    @Valid
    private LocationDTO location;
    
    @NotNull(message = "Professional data is required")
    @Valid
    private ProfessionalDataDTO professionalData;
    
    private List<Integer> specialties;
    
    private List<InitialScheduleDTO> initialSchedule;
}
