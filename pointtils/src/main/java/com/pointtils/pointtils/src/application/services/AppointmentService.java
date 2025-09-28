package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.AppointmentPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.AppointmentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentResponseDTO;
import com.pointtils.pointtils.src.application.mapper.AppointmentMapper;
import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import com.pointtils.pointtils.src.infrastructure.repositories.AppointmentRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final InterpreterRepository interpreterRepository;
    private final UserRepository userRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO dto) {
        var interpreter = interpreterRepository.findById(dto.getInterpreterId())
                .orElseThrow(() -> new EntityNotFoundException("Interpreter não encontrado com o id: " + dto.getInterpreterId()));
        var user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User não encontrado com o id: " + dto.getUserId()));

        var appointment = appointmentMapper.toDomain(dto, interpreter, user);

        var savedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponseDTO(savedAppointment);
    }

    public List<AppointmentResponseDTO> findAll() {
        return appointmentRepository.findAll().stream()
                .map(appointmentMapper::toResponseDTO)
                .toList();
    }

    public AppointmentResponseDTO findById(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada com o id: " + id));
        return appointmentMapper.toResponseDTO(appointment);
    }

    public AppointmentResponseDTO updatePartial(UUID id, AppointmentPatchRequestDTO dto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada com o id: " + id));

        if (dto.getUf() != null) appointment.setUf(dto.getUf());
        if (dto.getCity() != null) appointment.setCity(dto.getCity());
        if (dto.getModality() != null) appointment.setModality(dto.getModality());
        if (dto.getDate() != null) appointment.setDate(dto.getDate());
        if (dto.getDescription() != null) appointment.setDescription(dto.getDescription());
        if (dto.getStatus() != null) appointment.setStatus(dto.getStatus());
        if (dto.getNeighborhood() != null) appointment.setNeighborhood(dto.getNeighborhood());
        if (dto.getStreet() != null) appointment.setStreet(dto.getStreet());
        if (dto.getStreetNumber() != null) appointment.setStreetNumber(dto.getStreetNumber());
        if (dto.getAddressDetails() != null) appointment.setAddressDetails(dto.getAddressDetails());
        if (dto.getInterpreterId() != null) {
            var interpreter = interpreterRepository.findById(dto.getInterpreterId())
                    .orElseThrow(() -> new EntityNotFoundException("Interpreter não encontrado com o id: " + dto.getInterpreterId()));
            appointment.setInterpreter(interpreter);
        }
        if (dto.getUserId() != null) {
            var user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User não encontrado com o id: " + dto.getUserId()));
            appointment.setUser(user);
        }
        if (dto.getStartTime() != null) appointment.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) appointment.setEndTime(dto.getEndTime());

        Appointment saved = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDTO(saved);
    }

    public void delete(UUID id) {
        if (!appointmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Solicitação não encontrada com o id: " + id);
        }
        appointmentRepository.deleteById(id);
    }

    /*Testar! */
    public List<AppointmentResponseDTO> searchAppointments(UUID interpreterId, UUID userId, AppointmentStatus status, AppointmentModality modality, LocalDateTime fromDateTime) {
        List<Appointment> appointments = appointmentRepository.findAll();

        return appointments.stream()
                .filter(appointment -> interpreterId == null || appointment.getInterpreter().getId().equals(interpreterId))
                .filter(appointment -> userId == null || appointment.getUser().getId().equals(userId))
                .filter(appointment -> status == null || appointment.getStatus().equals(status))
                .filter(appointment -> modality == null || appointment.getModality().equals(modality))
                .filter(appointment -> fromDateTime == null || isAfterDateTime(appointment, fromDateTime))
                .map(appointmentMapper::toResponseDTO)
                .toList();
    }

    private boolean isAfterDateTime(Appointment appointment, LocalDateTime fromDateTime) {
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getDate(), appointment.getStartTime());
        return appointmentDateTime.isAfter(fromDateTime);
    }
}
