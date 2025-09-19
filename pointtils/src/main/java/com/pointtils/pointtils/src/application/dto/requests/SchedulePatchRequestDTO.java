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
public class SchedulePatchRequestDTO {
    @NotNull
    private UUID interpreterId;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
}
