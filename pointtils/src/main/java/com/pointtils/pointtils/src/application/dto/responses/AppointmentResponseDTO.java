package com.pointtils.pointtils.src.application.dto.responses;

import java.time.LocalTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppointmentResponseDTO {
    
    private String uf;
    private String city;
    // private String neighborhood;
    // private String street;
    // private Integer streetNumber;
    // private String addressDetails;
    private String modality;
    private String date;
    private String description;
    private String status;
    private UUID interpreterId;
    private UUID userId;
    private LocalTime startTime;
    private LocalTime endTime;

}
