package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.LocationRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ProfessionalDataPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterListResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.mapper.InterpreterResponseMapper;
import com.pointtils.pointtils.src.application.mapper.LocationMapper;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.enums.DaysOfWeek;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.spec.InterpreterSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class InterpreterService {

    private final InterpreterRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final InterpreterResponseMapper responseMapper;
    private final LocationMapper locationMapper;

    public InterpreterResponseDTO registerBasic(InterpreterBasicRequestDTO request) {
        Interpreter interpreter = Interpreter.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .picture(request.getPicture())
                .status(UserStatus.PENDING)
                .type(UserTypeE.INTERPRETER)
                .name(request.getName())
                .gender(request.getGender())
                .birthday(request.getBirthday())
                .cpf(request.getCpf())
                .cnpj(Objects.nonNull(request.getProfessionalData()) ? request.getProfessionalData().getCnpj() : null)
                .rating(BigDecimal.ZERO)
                .minValue(BigDecimal.ZERO)
                .maxValue(BigDecimal.ZERO)
                .imageRights(false)
                .modality(InterpreterModality.ALL)
                .description("")
                .build();

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

    public List<InterpreterListResponseDTO> findAll(
            String modality,
            String gender,
            String city,
            String uf,
            String neighborhood,
            String specialty,
            String availableDate) {

        InterpreterModality modalityEnum = null;
        if (modality != null) {
            modalityEnum = InterpreterModality.valueOf(modality.toUpperCase());
        }

        Gender genderEnum = null;
        if (gender != null) {
            genderEnum = Gender.valueOf(gender.toUpperCase());
        }

        DaysOfWeek dayOfWeek = null;
        LocalTime requestedStart = null;
        LocalTime requestedEnd = null;

        if (availableDate != null) {
            LocalDateTime dateTime = LocalDateTime.parse(availableDate,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            dayOfWeek = DaysOfWeek.valueOf(dateTime.getDayOfWeek().name().substring(0, 3));
            requestedStart = dateTime.toLocalTime();
            requestedEnd = requestedStart.plusHours(1);
        }

        return repository.findAll(
                        InterpreterSpecification.filter(modalityEnum, uf, city, neighborhood, specialty, genderEnum, dayOfWeek,
                                requestedStart, requestedEnd))
                .stream()
                .map(responseMapper::toListResponseDTO)
                .toList();
    }

    public InterpreterResponseDTO updateComplete(UUID id, InterpreterBasicRequestDTO dto) {
        Interpreter interpreter = findInterpreterById(id);

        if (dto != null) {
            interpreter.setName(dto.getName());
            interpreter.setEmail(dto.getEmail());
            interpreter.setPhone(dto.getPhone());
            interpreter.setPicture(dto.getPicture());
            interpreter.setBirthday(dto.getBirthday());
            interpreter.setCpf(dto.getCpf());
            interpreter.setGender(dto.getGender());

            if (dto.getPassword() != null) {
                interpreter.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
        }

        if (dto != null && dto.getProfessionalData() != null) {
            var professionalData = dto.getProfessionalData();
            interpreter.setCnpj(professionalData.getCnpj());
            interpreter.setMinValue(professionalData.getMinValue());
            interpreter.setMaxValue(professionalData.getMaxValue());
            interpreter.setImageRights(professionalData.getImageRights());
            interpreter.setModality(professionalData.getModality());
            interpreter.setDescription(professionalData.getDescription());
        }

        if (dto != null) {
            updateLocation(dto.getLocations(), interpreter);
        }

        Interpreter updatedInterpreter = repository.save(interpreter);
        return responseMapper.toResponseDTO(updatedInterpreter);
    }

    public InterpreterResponseDTO updatePartial(UUID id, InterpreterPatchRequestDTO dto) {
        Interpreter interpreter = findInterpreterById(id);
        updatePersonalPatchRequest(dto, interpreter);
        updateProfessionalPatchRequest(dto.getProfessionalData(), interpreter);
        updateLocation(dto.getLocations(), interpreter);

        Interpreter updatedInterpreter = repository.save(interpreter);
        return responseMapper.toResponseDTO(updatedInterpreter);
    }

    private void updatePersonalPatchRequest(InterpreterPatchRequestDTO requestDto, Interpreter interpreter) {
        if (requestDto.getName() != null) {
            interpreter.setName(requestDto.getName());
        }
        if (requestDto.getEmail() != null) {
            interpreter.setEmail(requestDto.getEmail());
        }
        if (requestDto.getPhone() != null) {
            interpreter.setPhone(requestDto.getPhone());
        }
        if (requestDto.getBirthday() != null) {
            interpreter.setBirthday(requestDto.getBirthday());
        }
        if (requestDto.getGender() != null) {
            interpreter.setGender(requestDto.getGender());
        }
    }

    private void updateProfessionalPatchRequest(ProfessionalDataPatchRequestDTO dto, Interpreter interpreter) {
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
            interpreter.setModality(dto.getModality());
        }
        if (dto.getDescription() != null) {
            interpreter.setDescription(dto.getDescription());
        }
    }

    private void updateLocation(List<LocationRequestDTO> locations, Interpreter interpreter) {
        if (locations == null) {
            return;
        }

        interpreter.getLocations().clear();
        locations.stream()
                .map(locationDTO -> locationMapper.toDomain(locationDTO, interpreter))
                .forEach(location -> interpreter.getLocations().add(location));
    }

    private Interpreter findInterpreterById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));
    }
}
