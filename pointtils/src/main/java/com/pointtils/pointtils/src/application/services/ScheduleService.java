package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.ScheduleRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.SchedulePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ScheduleResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.infrastructure.repositories.ScheduleRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

    public ScheduleResponseDTO findById(UUID scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado"));
        return ScheduleResponseDTO.builder()
                .id(schedule.getId())
                .interpreterId(schedule.getInterpreterId())
                .day(schedule.getDay())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .build();
    }

    public ScheduleResponseDTO updateSchedule(UUID scheduleId, SchedulePatchRequestDTO dto) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado"));

        if (dto.getDay() != null) {
            schedule.setDay(dto.getDay());
        }
        
        if (dto.getStartTime() != null) {
            schedule.setStartTime(dto.getStartTime());
        }

        if (dto.getEndTime() != null) {
            schedule.setEndTime(dto.getEndTime());
        }

        Schedule saved = scheduleRepository.save(schedule);

        return ScheduleResponseDTO.builder()
            .id(saved.getId())
            .interpreterId(saved.getInterpreterId())
            .day(saved.getDay())
            .startTime(saved.getStartTime())
            .endTime(saved.getEndTime())
            .build();
    }

    public void deleteById(UUID scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new EntityNotFoundException("Horário não encontrado");
        }

        scheduleRepository.deleteById(scheduleId);
    }
}
