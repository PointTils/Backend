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
import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private InterpreterRepository interpreterRepository;
    @Mock
    private TimeSlotMapper timeSlotMapper;
    @InjectMocks
    private ScheduleService scheduleService;

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
        when(interpreterRepository.findById(dto.getInterpreterId())).thenReturn(Optional.empty());
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

    @Test
    @SuppressWarnings("unchecked")
    void findAvailableSchedules_shouldReturnTimeSlotList() {
        UUID interpreterId = UUID.randomUUID();
        LocalDate startLocalDate = LocalDate.of(2025, 9, 30);
        LocalDate endLocalDate = LocalDate.of(2025, 10, 10);
        Date startDate = Date.from(startLocalDate.atStartOfDay(ZoneId.of("America/Sao_Paulo")).toInstant());
        Date endDate = Date.from(endLocalDate.atStartOfDay(ZoneId.of("America/Sao_Paulo")).toInstant());
        List<Object[]> mockData = List.of(
                new Object[]{interpreterId, startDate, new Time(32400000), new Time(36000000)},
                new Object[]{interpreterId, endDate, new Time(39600000), new Time(43200000)}
        );

        ArgumentCaptor<List<TimeSlotDTO>> timeSlotsCaptor = ArgumentCaptor.forClass(List.class);
        AvailableTimeSlotsResponseDTO mockMappedResponse = new AvailableTimeSlotsResponseDTO();
        when(scheduleRepository.findAvailableTimeSlots(interpreterId, startLocalDate, endLocalDate)).thenReturn(mockData);
        when(timeSlotMapper.toAvailableTimeSlotsResponse(timeSlotsCaptor.capture())).thenReturn(List.of(mockMappedResponse));

        assertThat(scheduleService.findAvailableSchedules(interpreterId, startLocalDate, endLocalDate))
                .hasSize(1)
                .contains(mockMappedResponse);
        assertEquals(2, timeSlotsCaptor.getValue().size());
        assertEquals("Tue Sep 30 00:00:00 BRT 2025", timeSlotsCaptor.getValue().get(0).getDate().toString());
        assertEquals(interpreterId, timeSlotsCaptor.getValue().get(0).getInterpreterId());
        assertEquals("06:00", timeSlotsCaptor.getValue().get(0).getStartTime().toString());
        assertEquals("07:00", timeSlotsCaptor.getValue().get(0).getEndTime().toString());
        assertEquals("Fri Oct 10 00:00:00 BRT 2025", timeSlotsCaptor.getValue().get(1).getDate().toString());
        assertEquals(interpreterId, timeSlotsCaptor.getValue().get(1).getInterpreterId());
        assertEquals("08:00", timeSlotsCaptor.getValue().get(1).getStartTime().toString());
        assertEquals("09:00", timeSlotsCaptor.getValue().get(1).getEndTime().toString());
    }
}