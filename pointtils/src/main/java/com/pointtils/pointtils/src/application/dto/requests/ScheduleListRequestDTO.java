package com.pointtils.pointtils.src.application.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;
// import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ScheduleListRequestDTO {
    @NotNull(message = "Parametro page é obrigatório")
    @Min(value = 0, message = "Parametro page deve ser maior ou igual a 0")
    private Integer page;

    @NotNull(message = "Parametro size é obrigatório")
    @Min(value = 1, message = "Parametro size deve ser maior que 0")
    private Integer size;

    private UUID interpreterId;
    // private DayOfWeek day;
    // private LocalTime dateFrom;
    // private LocalTime dateTo;
}