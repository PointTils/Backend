package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.requests.AppointmentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentFilterResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.ContactDataResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentMapper {

    private final UserSpecialtyMapper userSpecialtyMapper;

    public AppointmentRequestDTO toDTO(Appointment appointament) {
        return AppointmentRequestDTO.builder()
                .uf(appointament.getUf())
                .city(appointament.getCity())
                .neighborhood(appointament.getNeighborhood())
                .street(appointament.getStreet())
                .streetNumber(appointament.getStreetNumber())
                .addressDetails(appointament.getAddressDetails())
                .modality(appointament.getModality())
                .date(appointament.getDate())
                .description(appointament.getDescription())
                .interpreterId(appointament.getInterpreter().getId())
                .userId(appointament.getUser().getId())
                .startTime(appointament.getStartTime())
                .endTime(appointament.getEndTime())
                .build();
    }

    public Appointment toDomain(AppointmentRequestDTO dto, Interpreter interpreter, User user) {
        return Appointment.builder()
                .uf(dto.getUf())
                .city(dto.getCity())
                .neighborhood(dto.getNeighborhood())
                .street(dto.getStreet())
                .streetNumber(dto.getStreetNumber())
                .addressDetails(dto.getAddressDetails())
                .modality(dto.getModality() == null ? AppointmentModality.ONLINE : dto.getModality())
                .date(dto.getDate())
                .description(dto.getDescription())
                .status(AppointmentStatus.PENDING)
                .interpreter(interpreter)
                .user(user)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();
    }

    public AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .uf(appointment.getUf())
                .city(appointment.getCity())
                .neighborhood(appointment.getNeighborhood())
                .street(appointment.getStreet())
                .streetNumber(appointment.getStreetNumber())
                .addressDetails(appointment.getAddressDetails())
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

    public AppointmentFilterResponseDTO toFilterResponseDTO(Appointment appointment, User user) {
        return AppointmentFilterResponseDTO.builder()
                .id(appointment.getId())
                .uf(appointment.getUf())
                .city(appointment.getCity())
                .neighborhood(appointment.getNeighborhood())
                .street(appointment.getStreet())
                .streetNumber(appointment.getStreetNumber())
                .addressDetails(appointment.getAddressDetails())
                .modality(appointment.getModality().name())
                .date(appointment.getDate().toString())
                .description(appointment.getDescription())
                .status(appointment.getStatus().name())
                .interpreterId(appointment.getInterpreter().getId())
                .userId(appointment.getUser().getId())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .contactData(toContactDataResponseDto(user))
                .build();
    }

    private ContactDataResponseDTO toContactDataResponseDto(User user) {
        ContactDataResponseDTO.ContactDataResponseDTOBuilder builder = ContactDataResponseDTO.builder()
                .id(user.getId())
                .name(user.getDisplayName())
                .document(user.getDocument())
                .picture(user.getPicture())
                .specialties(userSpecialtyMapper.toDtoList(user.getSpecialties()));

        if (user instanceof Interpreter interpreter) {
            builder.rating(interpreter.getRating());
        }

        return builder.build();
    }
}
