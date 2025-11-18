package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.requests.AppointmentRequestDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class AppointmentMapperTest {

    private final AppointmentMapper appointmentMapper = new AppointmentMapper(new UserSpecialtyMapper());

    @Test
    void shouldMapAppointmentRequestToDomainWithoutModality() {
        LocalDate mockDate = LocalDate.of(2025, 11, 22);
        LocalTime mockStartTime = LocalTime.of(9, 30);
        LocalTime mockEndTime = LocalTime.of(10, 30);
        AppointmentRequestDTO requestDTO = AppointmentRequestDTO.builder()
                .date(mockDate)
                .description("Reunião de condomínio")
                .startTime(mockStartTime)
                .endTime(mockEndTime)
                .build();
        User user = mock(User.class);
        Interpreter interpreter = mock(Interpreter.class);

        var result = appointmentMapper.toDomain(requestDTO, interpreter, user);
        assertEquals(AppointmentModality.ONLINE, result.getModality());
        assertEquals(AppointmentStatus.PENDING, result.getStatus());
        assertEquals(mockDate, result.getDate());
        assertEquals(mockStartTime, result.getStartTime());
        assertEquals(mockEndTime, result.getEndTime());
        assertEquals(user, result.getUser());
        assertEquals("Reunião de condomínio", result.getDescription());
        assertEquals(interpreter, result.getInterpreter());
    }

    @Test
    void shouldMapAppointmentRequestToDomainWithModality() {
        LocalDate mockDate = LocalDate.of(2025, 11, 22);
        LocalTime mockStartTime = LocalTime.of(9, 30);
        LocalTime mockEndTime = LocalTime.of(10, 30);
        AppointmentRequestDTO requestDTO = AppointmentRequestDTO.builder()
                .date(mockDate)
                .description("Reunião de condomínio")
                .startTime(mockStartTime)
                .endTime(mockEndTime)
                .modality(AppointmentModality.PERSONALLY)
                .build();
        User user = mock(User.class);
        Interpreter interpreter = mock(Interpreter.class);

        var result = appointmentMapper.toDomain(requestDTO, interpreter, user);
        assertEquals(AppointmentModality.PERSONALLY, result.getModality());
        assertEquals(AppointmentStatus.PENDING, result.getStatus());
        assertEquals(mockDate, result.getDate());
        assertEquals(mockStartTime, result.getStartTime());
        assertEquals(mockEndTime, result.getEndTime());
        assertEquals(user, result.getUser());
        assertEquals("Reunião de condomínio", result.getDescription());
        assertEquals(interpreter, result.getInterpreter());
    }
}
