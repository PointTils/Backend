package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.ScheduleRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ScheduleResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.infrastructure.repositories.ScheduleRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final InterpreterRepository interpreterRepository;

    public ScheduleResponseDTO registerSchedule(ScheduleRequestDTO dto) {
        if (!interpreterRepository.existsById(dto.getInterpreterId())) {
            throw new EntityNotFoundException("Interpreter not found");
        }

        Schedule schedule = Schedule.builder()
                .interpreterId(dto.getInterpreterId())
                .day(dto.getDay())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return ScheduleResponseDTO.builder()
                .id(savedSchedule.getId())
                .interpreterId(savedSchedule.getInterpreterId())
                .day(savedSchedule.getDay())
                .startTime(savedSchedule.getStartTime())
                .endTime(savedSchedule.getEndTime())
                .build();
    }
}
