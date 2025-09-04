package com.pointtils.pointtils.src.application.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitialScheduleDTO {
    
    @NotBlank(message = "Day is required")
    @Pattern(regexp = "^(monday|tuesday|wednesday|thursday|friday|saturday|sunday)$", 
             message = "Day must be a valid weekday in English")
    private String day;
    
    @NotNull(message = "Start time is required")
    private LocalTime start;
    
    @NotNull(message = "End time is required")
    private LocalTime end;
}
