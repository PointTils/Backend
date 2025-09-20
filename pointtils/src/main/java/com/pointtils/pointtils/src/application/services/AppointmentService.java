package com.pointtils.pointtils.src.application.services;

import org.springframework.stereotype.Service;

import com.pointtils.pointtils.src.application.dto.requests.AppointmentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentResponseDTO;
import com.pointtils.pointtils.src.application.mapper.AppointmentMapper;
import com.pointtils.pointtils.src.infrastructure.repositories.AppointmentRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AppointmentService {

    private AppointmentRepository appointmentRepository;
    private final InterpreterRepository interpreterRepository;
    private final UserRepository userRepository;

    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO dto) {
        var interpreter = interpreterRepository.findById(dto.getInterpreterId())
                .orElseThrow(() -> new EntityNotFoundException("Interpreter not found with id: " + dto.getInterpreterId()));
        var user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + dto.getUserId()));

        var appointment = AppointmentMapper.toDomain(dto, interpreter, user);

        var savedAppointment = appointmentRepository.save(appointment);
        
        return AppointmentMapper.toResponseDTO(savedAppointment);
    }
}
