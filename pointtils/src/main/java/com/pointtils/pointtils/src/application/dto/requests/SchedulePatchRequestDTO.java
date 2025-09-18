package com.pointtils.pointtils.src.application.dto.requests;

import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePatchRequestDTO {
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
}
