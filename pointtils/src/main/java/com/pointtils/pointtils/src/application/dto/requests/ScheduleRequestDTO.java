package com.pointtils.pointtils.src.application.dto.requests;

import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequestDTO {
    @NotNull
    @JsonProperty("interpreter_id")
    private UUID interpreterId;
    @NotNull
    private DayOfWeek day;
    @NotNull
    @JsonProperty("start_time")
    private LocalTime startTime;
    @NotNull
    @JsonProperty("end_time")
    private LocalTime endTime;
}