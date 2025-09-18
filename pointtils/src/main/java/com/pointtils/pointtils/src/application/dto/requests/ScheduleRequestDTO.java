package com.pointtils.pointtils.src.application.dto.requests;

import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
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
    private UUID interpreterId;
    @NotNull
    private DayOfWeek day;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
}