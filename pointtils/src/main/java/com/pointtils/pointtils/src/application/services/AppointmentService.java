package com.pointtils.pointtils.src.application.services;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import com.pointtils.pointtils.src.application.dto.requests.AppointmentPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.AppointmentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentFilterResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentResponseDTO;
import com.pointtils.pointtils.src.application.mapper.AppointmentMapper;
import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import com.pointtils.pointtils.src.infrastructure.repositories.AppointmentRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.RatingRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final InterpreterRepository interpreterRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final AppointmentMapper appointmentMapper;
    private final EmailService emailService;

    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO dto) {
        var interpreter = interpreterRepository.findById(dto.getInterpreterId())
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado com o id: " + dto.getInterpreterId()));
        var user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + dto.getUserId()));

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

        // Capturar status anterior para detectar mudanças
        AppointmentStatus previousStatus = appointment.getStatus();

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

        // Enviar email quando o status mudar
        if (dto.getStatus() != null && !dto.getStatus().equals(previousStatus)) {
            sendStatusChangeEmail(saved, dto.getStatus(), previousStatus);
        }

        return appointmentMapper.toResponseDTO(saved);
    }

    public void delete(UUID id) {
        if (!appointmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Solicitação não encontrada com o id: " + id);
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

    /**
     * Envia email de notificação quando o status do agendamento muda
     * 
     * @param appointment Agendamento atualizado
     * @param newStatus Novo status
     * @param previousStatus Status anterior
     */
    private void sendStatusChangeEmail(Appointment appointment, AppointmentStatus newStatus, AppointmentStatus previousStatus) {
        // Preparar dados comuns do email
        String userName = appointment.getUser().getDisplayName();
        String userEmail = appointment.getUser().getEmail();
        String interpreterName = appointment.getInterpreter().getDisplayName();
        String interpreterEmail = appointment.getInterpreter().getEmail();
        String appointmentDate = formatAppointmentDateTime(appointment);
        
        // Enviar email baseado no novo status
        switch (newStatus) {
            case ACCEPTED:
                // Quando aceito, notificar o usuário que solicitou
                String location = buildLocationString(appointment);
                String modality = appointment.getModality().toString();
                emailService.sendAppointmentAcceptedEmail(
                    userEmail,
                    userName,
                    appointmentDate,
                    interpreterName,
                    location,
                    modality
                );
                break;
                
            case CANCELED:
                // Quando cancelado, notificar ambas as partes
                String cancelReason = appointment.getDescription() != null ? 
                    appointment.getDescription() : "Não especificado";
                    
                // Notificar usuário
                emailService.sendAppointmentCanceledEmail(
                    userEmail,
                    userName,
                    appointmentDate,
                    interpreterName,
                    cancelReason
                );
                
                // Notificar intérprete
                emailService.sendAppointmentCanceledEmail(
                    interpreterEmail,
                    interpreterName,
                    appointmentDate,
                    userName,
                    cancelReason
                );
                break;
                
            case PENDING:
                // Se voltou para PENDING de outro status, pode ser uma negação implícita
                // Verificar se mudou de ACCEPTED para PENDING (possível negação)
                if (previousStatus == AppointmentStatus.ACCEPTED) {
                    emailService.sendAppointmentDeniedEmail(
                        userEmail,
                        userName,
                        appointmentDate,
                        interpreterName
                    );
                }
                break;
                
            default:
                // Outros status não precisam de notificação automática
                break;
        }
    }

    /**
     * Formata a data e hora do agendamento para exibição no email
     * 
     * @param appointment Agendamento
     * @return String formatada com data e hora
     */
    private String formatAppointmentDateTime(Appointment appointment) {
        return String.format("%s às %s",
            appointment.getDate().toString(),
            appointment.getStartTime().toString()
        );
    }

    /**
     * Constrói string com o endereço completo do agendamento
     * 
     * @param appointment Agendamento
     * @return String com endereço formatado
     */
    private String buildLocationString(Appointment appointment) {
        if (appointment.getModality() == AppointmentModality.ONLINE) {
            return "Online";
        }
        
        StringBuilder location = new StringBuilder();
        if (appointment.getStreet() != null) {
            location.append(appointment.getStreet());
        }
        if (appointment.getStreetNumber() != null) {
            location.append(", ").append(appointment.getStreetNumber());
        }
        if (appointment.getNeighborhood() != null) {
            location.append(" - ").append(appointment.getNeighborhood());
        }
        if (appointment.getCity() != null) {
            location.append(", ").append(appointment.getCity());
        }
        if (appointment.getUf() != null) {
            location.append("/").append(appointment.getUf());
        }
        
        return location.toString();
    }
}
