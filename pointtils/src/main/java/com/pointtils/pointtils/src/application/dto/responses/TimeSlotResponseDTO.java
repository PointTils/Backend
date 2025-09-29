package com.pointtils.pointtils.src.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotResponseDTO {

    @JsonProperty("start_time")
    private LocalTime startTime;
    @JsonProperty("end_time")
    private LocalTime endTime;
}
