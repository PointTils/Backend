package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.ScheduleListRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.SchedulePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ScheduleRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.PaginatedScheduleResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.ScheduleResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScheduleServiceTest {
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private InterpreterRepository interpreterRepository;
    @InjectMocks
    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerSchedule_shouldSaveAndReturnResponseDTO() {
        UUID interpreterId = UUID.randomUUID();
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setInterpreterId(interpreterId);
        dto.setDay(DayOfWeek.MON);
        dto.setStartTime(LocalTime.of(9, 0));
        dto.setEndTime(LocalTime.of(17, 0));

        when(interpreterRepository.findById(interpreterId))
                .thenReturn(Optional.of(Interpreter.builder().id(interpreterId).build()));
        when(scheduleRepository.existsByInterpreterIdAndDayAndStartTimeLessThanAndEndTimeGreaterThan(
                dto.getInterpreterId(), dto.getDay(), dto.getEndTime(), dto.getStartTime())).thenReturn(false);
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(i -> i.getArgument(0));

        ScheduleResponseDTO response = scheduleService.registerSchedule(dto);
        assertEquals(dto.getInterpreterId(), response.getInterpreterId());
        assertEquals(dto.getDay(), response.getDay());
        assertEquals(dto.getStartTime(), response.getStartTime());
        assertEquals(dto.getEndTime(), response.getEndTime());
    }

    @Test
    void registerSchedule_shouldThrowEntityNotFoundWhenInterpreterNotExists() {
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setInterpreterId(UUID.randomUUID());
        when(interpreterRepository.existsById(dto.getInterpreterId())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> scheduleService.registerSchedule(dto));
    }

    @Test
    void registerSchedule_shouldThrowIllegalArgumentWhenConflict() {
        UUID interpreterId = UUID.randomUUID();
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setInterpreterId(interpreterId);
        dto.setDay(DayOfWeek.MON);
        dto.setStartTime(LocalTime.of(9, 0));
        dto.setEndTime(LocalTime.of(17, 0));
        when(interpreterRepository.findById(interpreterId))
                .thenReturn(Optional.of(Interpreter.builder().id(interpreterId).build()));
        when(scheduleRepository.existsByInterpreterIdAndDayAndStartTimeLessThanAndEndTimeGreaterThan(
                dto.getInterpreterId(), dto.getDay(), dto.getEndTime(), dto.getStartTime())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> scheduleService.registerSchedule(dto));
    }

    @Test
    void findById_shouldReturnScheduleResponseDTO() {
        UUID id = UUID.randomUUID();
        Schedule schedule = Schedule.builder()
                .id(id)
                .interpreter(new Interpreter())
                .day(DayOfWeek.MON)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();
        when(scheduleRepository.findById(id)).thenReturn(Optional.of(schedule));
        ScheduleResponseDTO result = scheduleService.findById(id);
        assertEquals(id, result.getId());
        assertEquals(DayOfWeek.MON, result.getDay());
    }

    @Test
    void findById_shouldThrowEntityNotFound() {
        UUID id = UUID.randomUUID();
        when(scheduleRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> scheduleService.findById(id));
    }

    @Test
    void findAll_shouldReturnPaginatedResponse() {
        ScheduleListRequestDTO query = new ScheduleListRequestDTO();
        query.setPage(0);
        query.setSize(10);
        Pageable pageable = PageRequest.of(0, 10);
        Schedule schedule = Schedule.builder()
                .id(UUID.randomUUID())
                .interpreter(new Interpreter())
                .day(DayOfWeek.MON)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();
        Page<Schedule> page = new PageImpl<>(List.of(schedule), pageable, 1);
        when(scheduleRepository.findAllWithFilters(
                any(), any(), any(), any(), any()
        )).thenReturn(page);
        PaginatedScheduleResponseDTO result = scheduleService.findAll(query, pageable);
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void updateSchedule_shouldUpdateAndReturnResponseDTO() {
        UUID id = UUID.randomUUID();
        Interpreter interpreter = new Interpreter();
        interpreter.setId(UUID.randomUUID());
        Schedule schedule = Schedule.builder()
                .id(id)
                .interpreter(interpreter)
                .day(DayOfWeek.MON)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();
        SchedulePatchRequestDTO dto = new SchedulePatchRequestDTO();
        dto.setInterpreterId(schedule.getInterpreter().getId());
        dto.setDay(DayOfWeek.TUE);
        dto.setStartTime(LocalTime.of(10, 0));
        dto.setEndTime(LocalTime.of(18, 0));
        when(scheduleRepository.findById(id)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.existsConflictForUpdate(any(UUID.class), any(UUID.class), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(i -> i.getArgument(0));
        ScheduleResponseDTO result = scheduleService.updateSchedule(id, dto);
        assertEquals(DayOfWeek.TUE, result.getDay());
        assertEquals(LocalTime.of(10, 0), result.getStartTime());
    }

    @Test
    void updateSchedule_shouldThrowEntityNotFound() {
        UUID id = UUID.randomUUID();
        SchedulePatchRequestDTO dto = new SchedulePatchRequestDTO();
        when(scheduleRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> scheduleService.updateSchedule(id, dto));
    }

    @Test
    void updateSchedule_shouldThrowIllegalArgumentWhenDifferentInterpreter() {
        UUID id = UUID.randomUUID();
        Schedule schedule = Schedule.builder()
                .id(id)
                .interpreter(new Interpreter())
                .day(DayOfWeek.MON)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();
        SchedulePatchRequestDTO dto = new SchedulePatchRequestDTO();
        dto.setInterpreterId(UUID.randomUUID()); // different
        when(scheduleRepository.findById(id)).thenReturn(Optional.of(schedule));
        assertThrows(IllegalArgumentException.class, () -> scheduleService.updateSchedule(id, dto));
    }

    @Test
    void updateSchedule_shouldThrowIllegalArgumentWhenConflict() {
        UUID id = UUID.randomUUID();
        Interpreter interpreter = new Interpreter();
        interpreter.setId(UUID.randomUUID());
        Schedule schedule = Schedule.builder()
                .id(id)
                .interpreter(interpreter)
                .day(DayOfWeek.MON)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();
        SchedulePatchRequestDTO dto = new SchedulePatchRequestDTO();
        dto.setInterpreterId(schedule.getInterpreter().getId());
        dto.setDay(DayOfWeek.TUE);
        when(scheduleRepository.findById(id)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.existsConflictForUpdate(any(UUID.class), any(UUID.class), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> scheduleService.updateSchedule(id, dto));
    }

    @Test
    void deleteById_shouldDeleteWhenExists() {
        UUID id = UUID.randomUUID();
        when(scheduleRepository.existsById(id)).thenReturn(true);
        scheduleService.deleteById(id);
        verify(scheduleRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteById_shouldThrowEntityNotFound() {
        UUID id = UUID.randomUUID();
        when(scheduleRepository.existsById(id)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> scheduleService.deleteById(id));
    }
}