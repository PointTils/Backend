package com.pointtils.pointtils.src.application.dto.requests;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentPatchRequestDTO {
    private String uf;
    private String city;
    private String neighborhood;
    private String street;
    private Integer streetNumber;
    private String addressDetails;
    private AppointmentModality modality;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String description;
    private AppointmentStatus status;
    private UUID interpreterId;
    private UUID userId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;
}
