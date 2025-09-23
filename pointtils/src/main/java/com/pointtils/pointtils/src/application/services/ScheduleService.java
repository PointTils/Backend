package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.ScheduleRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ScheduleListRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.SchedulePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ScheduleResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.PaginatedScheduleResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.infrastructure.repositories.ScheduleRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final InterpreterRepository interpreterRepository;

    public ScheduleResponseDTO registerSchedule(ScheduleRequestDTO dto) {
        if (!interpreterRepository.existsById(dto.getInterpreterId())) {
            throw new EntityNotFoundException("Interpreter not found");
        }

        boolean hasConflict = scheduleRepository.existsByInterpreterIdAndDayAndStartTimeLessThanAndEndTimeGreaterThan(
            dto.getInterpreterId(),
            dto.getDay(),
            dto.getEndTime(),
            dto.getStartTime()
        );

        if (hasConflict) {
            throw new IllegalArgumentException("Já existe um horário conflitante para este intérprete neste dia da semana.");
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

    public PaginatedScheduleResponseDTO findAll(ScheduleListRequestDTO query, Pageable pageable) {
        Page<Schedule> schedules = scheduleRepository.findAllWithFilters(
            pageable,
            query.getInterpreterId(),
            query.getDay() != null ? query.getDay().name() : null,
            query.getDateFrom() != null ? query.getDateFrom().toString() : null,
            query.getDateTo() != null ? query.getDateTo().toString() : null
        );
        
        List<ScheduleResponseDTO> items = schedules.map(s -> ScheduleResponseDTO.builder()
            .id(s.getId())
            .interpreterId(s.getInterpreterId())
            .day(s.getDay())
            .startTime(s.getStartTime())
            .endTime(s.getEndTime())
            .build()).toList();

        return PaginatedScheduleResponseDTO.builder()
            .page(schedules.getNumber())
            .size(schedules.getSize())
            .total(schedules.getTotalElements())
            .items(items)
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

        if (!dto.getInterpreterId().equals(schedule.getInterpreterId())) {
            throw new IllegalArgumentException("Não é possível alterar o horário de outro intérprete");
        }

        boolean hasConflict = scheduleRepository.existsConflictForUpdate(
            schedule.getId(),
            schedule.getInterpreterId(),
            schedule.getDay(),
            schedule.getStartTime(),
            schedule.getEndTime()
        );

        if (hasConflict) {
            throw new IllegalArgumentException("Já existe um horário conflitante para este intérprete neste dia da semana");
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
