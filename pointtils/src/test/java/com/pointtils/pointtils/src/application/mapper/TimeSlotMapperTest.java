package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.TimeSlotDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TimeSlotMapperTest {

    private final TimeSlotMapper timeSlotMapper = new TimeSlotMapper();

    @Test
    void shouldGroupTimeSlotsByDateAndInterpreter() {
        UUID interpreterId = UUID.randomUUID();
        Date firstDate = Date.from(LocalDate.of(2025, 10, 5).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date secondDate = Date.from(LocalDate.of(2025, 11, 24).atStartOfDay(ZoneId.systemDefault()).toInstant());
        TimeSlotDTO firstTimeSlot = new TimeSlotDTO(firstDate, interpreterId, LocalTime.of(10, 30), LocalTime.of(11, 30));
        TimeSlotDTO secondTimeSlot = new TimeSlotDTO(secondDate, interpreterId, LocalTime.of(10, 30), LocalTime.of(11, 30));
        TimeSlotDTO thirdTimeSlot = new TimeSlotDTO(firstDate, interpreterId, LocalTime.of(11, 0), LocalTime.of(12, 0));
        TimeSlotDTO fourthTimeSlot = new TimeSlotDTO(firstDate, interpreterId, LocalTime.of(15, 0), LocalTime.of(16, 0));

        var actualResponse = timeSlotMapper.toAvailableTimeSlotsResponse(List
                .of(firstTimeSlot, secondTimeSlot, thirdTimeSlot, fourthTimeSlot));
        assertThat(actualResponse).hasSize(2)
                .anyMatch(dto -> dto.getDate().equals(firstDate)
                        && dto.getInterpreterId().equals(interpreterId)
                        && dto.getTimeSlots().size() == 3)
                .anyMatch(dto -> dto.getDate().equals(secondDate)
                        && dto.getInterpreterId().equals(interpreterId)
                        && dto.getTimeSlots().size() == 1);
    }
}
