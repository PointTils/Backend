package com.pointtils.pointtils.src.application.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.LocationRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.PersonPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ProfessionalDataPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.mapper.InterpreterResponseMapper;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.enums.DaysOfWeek;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.spec.InterpreterSpecification;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InterpreterRegisterService {

    private final InterpreterRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final InterpreterResponseMapper responseMapper;

    public InterpreterResponseDTO register(InterpreterBasicRequestDTO request) {
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
                .cnpj(request.getProfessionalData().getCnpj())
                .rating(BigDecimal.ZERO)
                .minValue(request.getProfessionalData().getMinValue())
                .maxValue(request.getProfessionalData().getMaxValue())
                .imageRights(request.getProfessionalData().getImageRights())
                .modality(request.getProfessionalData().getModality())
                .description(request.getProfessionalData().getDescription())
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

    public List<InterpreterResponseDTO> findAll(
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
                .map(responseMapper::toResponseDTO)
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
        PersonPatchRequestDTO personalData = new PersonPatchRequestDTO();
        personalData.setName(dto.getName());
        personalData.setEmail(dto.getEmail());
        personalData.setPhone(dto.getPhone());
        personalData.setGender(dto.getGender());
        personalData.setBirthday(dto.getBirthday());
        personalData.setPicture(dto.getPicture());
        updatePersonalPatchRequest(personalData, interpreter);
        updateProfessionalPatchRequest(dto.getProfessionalData(), interpreter);
        updateLocation(dto.getLocations(), interpreter);

        Interpreter updatedInterpreter = repository.save(interpreter);
        return responseMapper.toResponseDTO(updatedInterpreter);
    }

    private void updatePersonalPatchRequest(PersonPatchRequestDTO personal, Interpreter interpreter) {
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
        if (personal.getGender() != null) {
            interpreter.setGender(personal.getGender());
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

    private void updateLocation(List<LocationRequestDTO> locationDTOs, Interpreter interpreter) {
        if (locationDTOs == null || locationDTOs.isEmpty()) {
            interpreter.setLocations(List.of());
            return;
        }

        List<Location> locations = new ArrayList<>();
        for (LocationRequestDTO dto : locationDTOs) {
            Location location = Location.builder()
                    .uf(dto.getUf())
                    .city(dto.getCity())
                    .neighborhood(dto.getNeighborhood())
                    .interpreter(interpreter)
                    .build();
            locations.add(location);
        }


        interpreter.setLocations(locations);
    }

    private Interpreter findInterpreterById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));
    }
}
