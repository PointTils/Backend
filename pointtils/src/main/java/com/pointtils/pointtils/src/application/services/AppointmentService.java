package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.AppointmentPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.AppointmentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentFilterResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentResponseDTO;
import com.pointtils.pointtils.src.application.mapper.AppointmentMapper;
import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.NotificationType;
import com.pointtils.pointtils.src.infrastructure.repositories.AppointmentRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.RatingRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final String SOLICITATION_NOT_FOUND = "Solicitação não encontrada com o id: ";

    private final AppointmentRepository appointmentRepository;
    private final InterpreterRepository interpreterRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final AppointmentMapper appointmentMapper;
    private final NotificationService notificationService;

    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO dto) {
        var interpreter = interpreterRepository.findById(dto.getInterpreterId())
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado com o id: " + dto.getInterpreterId()));
        var user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + dto.getUserId()));

        var appointment = appointmentMapper.toDomain(dto, interpreter, user);

        var savedAppointment = appointmentRepository.save(appointment);
        notificationService.sendNotificationToUser(dto.getInterpreterId(), NotificationType.APPOINTMENT_REQUESTED);

        return appointmentMapper.toResponseDTO(savedAppointment);
    }

    public List<AppointmentResponseDTO> findAll() {
        return appointmentRepository.findAll().stream()
                .map(appointmentMapper::toResponseDTO)
                .toList();
    }

    public AppointmentResponseDTO findById(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SOLICITATION_NOT_FOUND + id));
        return appointmentMapper.toResponseDTO(appointment);
    }

    public AppointmentResponseDTO updatePartial(UUID id, AppointmentPatchRequestDTO dto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SOLICITATION_NOT_FOUND + id));

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
                    .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado com o id: " + dto.getInterpreterId()));
            appointment.setInterpreter(interpreter);
        }
        if (dto.getUserId() != null) {
            var user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + dto.getUserId()));
            appointment.setUser(user);
        }
        if (dto.getStartTime() != null) appointment.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) appointment.setEndTime(dto.getEndTime());

        Appointment saved = appointmentRepository.save(appointment);
        notifyAppointmentStatusUpdate(saved, dto.getLoggedUserEmail());

        return appointmentMapper.toResponseDTO(saved);
    }

    public void delete(UUID id) {
        if (!appointmentRepository.existsById(id)) {
            throw new EntityNotFoundException(SOLICITATION_NOT_FOUND + id);
        }
        appointmentRepository.deleteById(id);
    }

    public List<AppointmentFilterResponseDTO> searchAppointments(UUID interpreterId, UUID userId, AppointmentStatus status,
                                                                 AppointmentModality modality, LocalDateTime fromDateTime, Boolean hasRating, int dayLimit) {
        List<Appointment> appointments = appointmentRepository.findAll();

        return appointments.stream()
                .filter(appointment -> interpreterId == null || appointment.getInterpreter().getId().equals(interpreterId))
                .filter(appointment -> userId == null || appointment.getUser().getId().equals(userId))
                .filter(appointment -> status == null || appointment.getStatus().equals(status))
                .filter(appointment -> modality == null || appointment.getModality().equals(modality))
                .filter(appointment -> fromDateTime == null || isAfterDateTime(appointment, fromDateTime))
                .filter(appointment -> hasRating == null || hasRatingAssigned(appointment, hasRating))
                .filter(appointment -> dayLimit == -1 || isBeforeDateLimit(appointment, dayLimit))
                .sorted(Comparator.comparing(
                        (Appointment appointment) -> LocalDateTime.of(appointment.getDate(), appointment.getEndTime())
                ).reversed())
                .map(appointment -> {
                    if (interpreterId != null) {
                        return appointmentMapper.toFilterResponseDTO(appointment, appointment.getUser());
                    } else {
                        return appointmentMapper.toFilterResponseDTO(appointment, appointment.getInterpreter());
                    }
                })
                .toList();
    }

    private void notifyAppointmentStatusUpdate(Appointment appointment, String loggedUserEmail) {
        var loggedUserIsInterpreter = appointment.getInterpreter().getEmail().equals(loggedUserEmail);
        var userToNotifyId = loggedUserIsInterpreter
                ? appointment.getUser().getId()
                : appointment.getInterpreter().getId();

        if (Objects.requireNonNull(appointment.getStatus()) == AppointmentStatus.CANCELED) {
            notificationService.sendNotificationToUser(userToNotifyId, NotificationType.APPOINTMENT_CANCELED);
        } else if (appointment.getStatus() == AppointmentStatus.ACCEPTED) {
            LocalDateTime scheduledTime = LocalDateTime.of(appointment.getDate(), appointment.getStartTime()).minusHours(24);

            notificationService.sendNotificationToUser(userToNotifyId, NotificationType.APPOINTMENT_ACCEPTED);
            notificationService.scheduleNotificationForUser(appointment.getUser().getId(),
                    NotificationType.APPOINTMENT_REMINDER, scheduledTime);
            notificationService.scheduleNotificationForUser(appointment.getInterpreter().getId(),
                    NotificationType.APPOINTMENT_REMINDER, scheduledTime);
        }
    }

    private boolean hasRatingAssigned(Appointment appointment, Boolean hasRating) {
        boolean ratingExists = ratingRepository.existsByAppointment(appointment);
        return hasRating == ratingExists;
    }

    private boolean isAfterDateTime(Appointment appointment, LocalDateTime fromDateTime) {
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getDate(), appointment.getStartTime());
        return appointmentDateTime.isAfter(fromDateTime);
    }

    private boolean isBeforeDateLimit(Appointment appointment, int dayLimit) {
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getDate(), appointment.getEndTime());
        LocalDateTime limitDateTime = appointmentDateTime.plusDays(dayLimit);
        return LocalDateTime.now().isBefore(limitDateTime);
    }
}
