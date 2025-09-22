package com.pointtils.pointtils.src.application.services;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.PersonalPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ProfessionalPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.mapper.InterpreterMapper;
import com.pointtils.pointtils.src.application.mapper.InterpreterResponseMapper;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.enums.DaysOfWeek;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.core.domain.exceptions.InvalidFilterException;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InterpreterRegisterService {

    private final InterpreterRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final InterpreterResponseMapper responseMapper;

    public InterpreterResponseDTO register(InterpreterRequestDTO request) {
        Interpreter interpreter = Interpreter.builder()
                .email(request.getPersonalData().getEmail())
                .password(passwordEncoder.encode(request.getPersonalData().getPassword()))
                .phone(request.getPersonalData().getPhone())
                .picture(request.getPersonalData().getPicture())
                .status(UserStatus.PENDING)
                .type(UserTypeE.INTERPRETER)
                .name(request.getPersonalData().getName())
                .gender(Gender.fromString(request.getPersonalData().getGender()))
                .birthday(request.getPersonalData().getBirthday())
                .cpf(request.getPersonalData().getCpf())
                .cnpj(request.getProfessionalData().getCnpj())
                .rating(BigDecimal.ZERO)
                .minValue(request.getProfessionalData().getMinValue())
                .maxValue(request.getProfessionalData().getMaxValue())
                .imageRights(request.getProfessionalData().getImageRights())
                .modality(InterpreterMapper.toInterpreterModality(request.getProfessionalData().getModality()))
                .description(request.getProfessionalData().getDescription())
                .build();

        if (request.getLocation() != null) {
            Location location = Location.builder()
                    .uf(request.getLocation().getUf())
                    .city(request.getLocation().getCity())
                    .user(interpreter)
                    .build();

            interpreter.setLocation(location);
        }

        Interpreter savedInterpreter = repository.save(interpreter);
        return responseMapper.toResponseDTO(savedInterpreter);
    }

    public InterpreterResponseDTO findById(UUID id) {
        Interpreter interpreter = findInterpreterById(id);
        return responseMapper.toResponseDTO(interpreter);
    }

    public void delete(UUID id) {
        Interpreter interpreter = findInterpreterById(id);
        interpreter.setStatus(UserStatus.INACTIVE);
        repository.save(interpreter);
    }

    public List<InterpreterResponseDTO> findAll(
            InterpreterModality modality,
            Gender gender,
            String city,
            String neighborhood,
            String specialty,
            String dateTimeStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);

        DayOfWeek javaDay = dateTime.getDayOfWeek();
        DaysOfWeek requestedDay = switch (javaDay) {
            case MONDAY -> DaysOfWeek.MON;
            case TUESDAY -> DaysOfWeek.TUE;
            case WEDNESDAY -> DaysOfWeek.WEN;
            case THURSDAY -> DaysOfWeek.THU;
            case FRIDAY -> DaysOfWeek.FRI;
            case SATURDAY -> DaysOfWeek.SAT;
            case SUNDAY -> DaysOfWeek.SUN;
        };

        LocalTime requestedStart = dateTime.toLocalTime();
        LocalTime requestedEnd = requestedStart.plusHours(1);

        if (city != null && city.isBlank()) {
            throw new InvalidFilterException("Filtros inválidos");
        }
        if (neighborhood != null && neighborhood.isBlank()) {
            throw new InvalidFilterException("Filtros inválidos");
        }
        if (specialty != null && specialty.isBlank()) {
            throw new InvalidFilterException("Filtros inválidos");
        }

        List<Interpreter> interpreters = repository.findAll(
                modality,
                gender,
                city,
                neighborhood,
                specialty,
                requestedDay,
                requestedStart,
                requestedEnd);
        return interpreters.stream()
                .map(responseMapper::toResponseDTO)
                .toList();
    }

    public InterpreterResponseDTO updateComplete(UUID id, InterpreterRequestDTO dto) {
        Interpreter interpreter = findInterpreterById(id);

        if (dto.getPersonalData() != null) {
            var personalData = dto.getPersonalData();
            interpreter.setName(personalData.getName());
            interpreter.setEmail(personalData.getEmail());
            interpreter.setPhone(personalData.getPhone());
            interpreter.setPicture(personalData.getPicture());
            interpreter.setBirthday(personalData.getBirthday());
            interpreter.setCpf(personalData.getCpf());
            interpreter.setGender(Gender.fromString(personalData.getGender()));

            if (personalData.getPassword() != null) {
                interpreter.setPassword(passwordEncoder.encode(personalData.getPassword()));
            }
        }

        if (dto.getProfessionalData() != null) {
            var professionalData = dto.getProfessionalData();
            interpreter.setCnpj(professionalData.getCnpj());
            interpreter.setMinValue(professionalData.getMinValue());
            interpreter.setMaxValue(professionalData.getMaxValue());
            interpreter.setImageRights(professionalData.getImageRights());
            interpreter.setModality(InterpreterMapper.toInterpreterModality(professionalData.getModality()));
            interpreter.setDescription(professionalData.getDescription());
        }

        updateLocation(dto.getLocation(), interpreter);

        Interpreter updatedInterpreter = repository.save(interpreter);
        return responseMapper.toResponseDTO(updatedInterpreter);
    }

    public InterpreterResponseDTO updatePartial(UUID id, InterpreterPatchRequestDTO dto) {
        Interpreter interpreter = findInterpreterById(id);
        updatePersonalPatchRequest(dto.getPersonalData(), interpreter);
        updateProfessionalPatchRequest(dto.getProfessionalData(), interpreter);
        updateLocation(dto.getLocation(), interpreter);

        Interpreter updatedInterpreter = repository.save(interpreter);
        return responseMapper.toResponseDTO(updatedInterpreter);
    }

    private void updatePersonalPatchRequest(PersonalPatchRequestDTO personal, Interpreter interpreter) {
        if (personal == null) {
            return;
        }
        if (personal.getName() != null) {
            interpreter.setName(personal.getName());
        }
        if (personal.getEmail() != null) {
            interpreter.setEmail(personal.getEmail());
        }
        if (personal.getPhone() != null) {
            interpreter.setPhone(personal.getPhone());
        }
        if (personal.getBirthday() != null) {
            interpreter.setBirthday(personal.getBirthday());
        }
        if (personal.getCpf() != null) {
            interpreter.setCpf(personal.getCpf());
        }
        if (personal.getGender() != null) {
            interpreter.setGender(Gender.fromString(personal.getGender()));
        }
    }

    private void updateProfessionalPatchRequest(ProfessionalPatchRequestDTO dto, Interpreter interpreter) {
        if (dto == null) {
            return;
        }
        if (dto.getCnpj() != null) {
            interpreter.setCnpj(dto.getCnpj());
        }
        if (dto.getMinValue() != null) {
            interpreter.setMinValue(dto.getMinValue());
        }
        if (dto.getMaxValue() != null) {
            interpreter.setMaxValue(dto.getMaxValue());
        }
        if (dto.getImageRights() != null) {
            interpreter.setImageRights(dto.getImageRights());
        }
        if (dto.getModality() != null) {
            interpreter.setModality(InterpreterMapper.toInterpreterModality(dto.getModality()));
        }
        if (dto.getDescription() != null) {
            interpreter.setDescription(dto.getDescription());
        }
    }

    private void updateLocation(LocationDTO locationDTO, Interpreter interpreter) {
        if (locationDTO == null) {
            return;
        }

        Location location = interpreter.getLocation();
        if (location == null) {
            location = Location.builder().user(interpreter).build();
        }
        location.setUf(locationDTO.getUf());
        location.setCity(locationDTO.getCity());
        interpreter.setLocation(location);
    }

    private Interpreter findInterpreterById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));
    }
}
