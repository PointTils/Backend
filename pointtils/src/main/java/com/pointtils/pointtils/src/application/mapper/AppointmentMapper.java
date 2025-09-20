package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.requests.AppointmentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;

public class AppointmentMapper {
    
    public static AppointmentRequestDTO toDTO(Appointment appointament) {
        return AppointmentRequestDTO.builder()
                .uf(appointament.getUf())
                .city(appointament.getCity())
                .modality(appointament.getModality().name())
                .date(appointament.getDate())
                .description(appointament.getDescription())
                .status(appointament.getStatus().name())
                .interpreterId(appointament.getInterpreter().getId())
                .userId(appointament.getUser().getId())
                .startTime(appointament.getStartTime())
                .endTime(appointament.getEndTime())
                .build();
    }

    public static Appointment toDomain(AppointmentRequestDTO dto, Interpreter interpreter, User user) {
        return Appointment.builder()
                .uf(dto.getUf())
                .city(dto.getCity())
                .modality(AppointmentModality.fromString(dto.getModality()))
                .date(dto.getDate())
                .description(dto.getDescription())
                .status(AppointmentStatus.fromString(dto.getStatus()))
                .interpreter(interpreter)
                .user(user)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();
    }

    public static AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .uf(appointment.getUf())
                .city(appointment.getCity())
                .modality(appointment.getModality().name())
                .date(appointment.getDate().toString())
                .description(appointment.getDescription())
                .status(appointment.getStatus().name())
                .interpreterId(appointment.getInterpreter().getId())
                .userId(appointment.getUser().getId())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .build();
    }
}
