package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.TimeSlotDTO;
import com.pointtils.pointtils.src.application.dto.requests.ScheduleListRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.SchedulePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ScheduleRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.AvailableTimeSlotsResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.PaginatedScheduleResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.ScheduleResponseDTO;
import com.pointtils.pointtils.src.application.mapper.TimeSlotMapper;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private static final String TIME_NOT_FOUND = "Horário não encontrado";

    private final ScheduleRepository scheduleRepository;
    private final InterpreterRepository interpreterRepository;
    private final TimeSlotMapper timeSlotMapper;

    public ScheduleResponseDTO registerSchedule(ScheduleRequestDTO dto) {
        Optional<Interpreter> foundInterpreter = interpreterRepository.findById(dto.getInterpreterId());
        if (foundInterpreter.isEmpty()) {
            throw new EntityNotFoundException("Intérprete não encontrado");
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
                .interpreter(foundInterpreter.get())
                .day(dto.getDay())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return ScheduleResponseDTO.builder()
                .id(savedSchedule.getId())
                .interpreterId(savedSchedule.getInterpreter().getId())
                .day(savedSchedule.getDay())
                .startTime(savedSchedule.getStartTime())
                .endTime(savedSchedule.getEndTime())
                .build();
    }

    public ScheduleResponseDTO findById(UUID scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException(TIME_NOT_FOUND));

        return ScheduleResponseDTO.builder()
                .id(schedule.getId())
                .interpreterId(schedule.getInterpreter().getId())
                .day(schedule.getDay())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .build();
    }

    public PaginatedScheduleResponseDTO findAll(ScheduleListRequestDTO query, Pageable pageable) {
        Page<Schedule> schedules = scheduleRepository.findAllWithFilters(
                pageable,
                query.getInterpreterId(),
                query.getDay(),
                query.getDateFrom(),
                query.getDateTo()
        );

        List<ScheduleResponseDTO> items = schedules.map(s -> ScheduleResponseDTO.builder()
                .id(s.getId())
                .interpreterId(s.getInterpreter().getId())
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

    public List<AvailableTimeSlotsResponseDTO> findAvailableSchedules(UUID interpreterId, LocalDate dateFrom, LocalDate dateTo) {
        List<Object[]> timeSlots = scheduleRepository.findAvailableTimeSlots(interpreterId, dateFrom, dateTo);

        List<TimeSlotDTO> foundTimeSlots = timeSlots.stream()
                .map(timeSlot -> new TimeSlotDTO(
                        (Date) timeSlot[1],
                        (UUID) timeSlot[0],
                        ((Time) timeSlot[2]).toLocalTime(),
                        ((Time) timeSlot[3]).toLocalTime()
                ))
                .toList();
        return timeSlotMapper.toAvailableTimeSlotsResponse(foundTimeSlots);
    }

    public ScheduleResponseDTO updateSchedule(UUID scheduleId, SchedulePatchRequestDTO dto) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException(TIME_NOT_FOUND));

        if (dto.getDay() != null) {
            schedule.setDay(dto.getDay());
        }

        if (dto.getStartTime() != null) {
            schedule.setStartTime(dto.getStartTime());
        }

        if (dto.getEndTime() != null) {
            schedule.setEndTime(dto.getEndTime());
        }

        if (!dto.getInterpreterId().equals(schedule.getInterpreter().getId())) {
            throw new IllegalArgumentException("Não é possível alterar o horário de outro intérprete");
        }

        boolean hasConflict = scheduleRepository.existsConflictForUpdate(
                schedule.getId(),
                schedule.getInterpreter().getId(),
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
                .interpreterId(saved.getInterpreter().getId())
                .day(saved.getDay())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .build();
    }

    public void deleteById(UUID scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new EntityNotFoundException(TIME_NOT_FOUND);
        }

        scheduleRepository.deleteById(scheduleId);
    }
}
