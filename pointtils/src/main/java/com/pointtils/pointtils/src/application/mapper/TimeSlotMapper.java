package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.TimeSlotDTO;
import com.pointtils.pointtils.src.application.dto.responses.AvailableTimeSlotsResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.TimeSlotResponseDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TimeSlotMapper {

    public List<AvailableTimeSlotsResponseDTO> toAvailableTimeSlotsResponse(List<TimeSlotDTO> timeSlots) {
        Map<String, AvailableTimeSlotsResponseDTO> availableTimeSlots = new HashMap<>();
        timeSlots.forEach(timeSlot -> {
            String key = timeSlot.getDate().toString() + timeSlot.getInterpreterId().toString();
            if (availableTimeSlots.containsKey(key)) {
                availableTimeSlots.get(key).getTimeSlots()
                        .add(new TimeSlotResponseDTO(timeSlot.getStartTime(), timeSlot.getEndTime()));
            } else {
                var response = new AvailableTimeSlotsResponseDTO(
                        timeSlot.getDate(),
                        timeSlot.getInterpreterId(),
                        new ArrayList<>(List.of(new TimeSlotResponseDTO(timeSlot.getStartTime(), timeSlot.getEndTime())))
                );
                availableTimeSlots.put(key, response);
            }
        });
        return availableTimeSlots.values().stream()
                .sorted(Comparator.comparing(AvailableTimeSlotsResponseDTO::getDate))
                .toList();
    }
}
