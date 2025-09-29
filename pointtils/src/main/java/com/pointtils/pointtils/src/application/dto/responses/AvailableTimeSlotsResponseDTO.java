package com.pointtils.pointtils.src.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTimeSlotsResponseDTO {

    private Date date;
    @JsonProperty("interpreter_id")
    private UUID interpreterId;
    @JsonProperty("time_slots")
    private List<TimeSlotResponseDTO> timeSlots;
}
