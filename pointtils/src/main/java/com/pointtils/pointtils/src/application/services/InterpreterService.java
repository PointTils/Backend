package com.pointtils.pointtils.src.application.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.LocationRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ProfessionalDataPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterListResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.mapper.InterpreterResponseMapper;
import com.pointtils.pointtils.src.application.mapper.LocationMapper;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.spec.InterpreterSpecification;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InterpreterService {

    private final InterpreterRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final InterpreterResponseMapper responseMapper;
    private final LocationMapper locationMapper;
    private final EmailService emailService;

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
                .videoUrl(Objects.nonNull(request.getProfessionalData()) ? request.getProfessionalData().getVideoUrl() : null)
                .rating(BigDecimal.ZERO)
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

    public List<InterpreterListResponseDTO> findAll(String modality,
            String gender,
            String city,
            String uf,
            String neighborhood,
            String specialty,
            String availableDate,
            String name) {
        InterpreterModality modalityEnum = null;
        if (modality != null) {
            modalityEnum = InterpreterModality.valueOf(modality.toUpperCase());
        }

        Gender genderEnum = null;
        if (gender != null) {
            genderEnum = Gender.valueOf(gender.toUpperCase());
        }

        LocalDateTime dateTime = null;
        if (availableDate != null) {
            dateTime = LocalDateTime.parse(availableDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }

        List<UUID> specialtyList = null;
        if (specialty != null) {
            specialtyList = Arrays.stream(specialty.split(","))
                    .map(UUID::fromString)
                    .toList();
        }

        return repository.findAll(InterpreterSpecification.filter(modalityEnum, uf, city, neighborhood, specialtyList,
                genderEnum, dateTime, name))
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
            interpreter.setImageRights(professionalData.getImageRights());
            interpreter.setModality(professionalData.getModality());
            interpreter.setDescription(professionalData.getDescription());
            interpreter.setVideoUrl(professionalData.getVideoUrl());
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

    /**
     * Aprova o cadastro de um intérprete
     *
     * @param id ID do intérprete
     * @return true se o cadastro foi aprovado com sucesso, false caso contrário
     */
    public boolean approveInterpreter(UUID id) {
        try {
            Interpreter interpreter = findInterpreterById(id);
            if (interpreter.getStatus() != UserStatus.PENDING) {
                throw new IllegalArgumentException("Cadastro do intérprete já foi verificado anteriormente.");
            }

            interpreter.setStatus(UserStatus.ACTIVE);
            repository.save(interpreter);

            // Enviar email de feedback para o intérprete
            boolean emailSent = emailService.sendInterpreterFeedbackEmail(
                    interpreter.getEmail(),
                    interpreter.getName(),
                    true // approved
            );

            if (emailSent) {
                log.info("Cadastro do intérprete {} aprovado e email enviado com sucesso", interpreter.getName());
            } else {
                log.warn("Cadastro do intérprete {} aprovado, mas falha ao enviar email", interpreter.getName());
            }

            return true;

        } catch (Exception e) {
            log.error("Erro ao aprovar cadastro do intérprete {}: {}", id, e.getMessage());
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            return false;
        }
    }

    /**
     * Recusa o cadastro de um intérprete
     *
     * @param id ID do intérprete
     * @return true se o cadastro foi recusado com sucesso, false caso contrário
     */
    public boolean rejectInterpreter(UUID id) {
        try {
            Interpreter interpreter = findInterpreterById(id);
            if (interpreter.getStatus() != UserStatus.PENDING) {
                throw new IllegalArgumentException("Cadastro do intérprete já foi verificado anteriormente.");
            }

            interpreter.setStatus(UserStatus.INACTIVE);
            repository.save(interpreter);

            // Enviar email de feedback para o intérprete
            boolean emailSent = emailService.sendInterpreterFeedbackEmail(
                    interpreter.getEmail(),
                    interpreter.getName(),
                    false // not approved
            );

            if (emailSent) {
                log.info("Cadastro do intérprete {} recusado e email enviado com sucesso", interpreter.getName());
            } else {
                log.warn("Cadastro do intérprete {} recusado, mas falha ao enviar email", interpreter.getName());
            }

            return true;

        } catch (Exception e) {
            log.error("Erro ao recusar cadastro do intérprete {}: {}", id, e.getMessage());
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            return false;
        }
    }

    protected Interpreter findInterpreterById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));
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
        if (dto.getImageRights() != null) {
            interpreter.setImageRights(dto.getImageRights());
        }
        if (dto.getModality() != null) {
            interpreter.setModality(dto.getModality());
        }
        if (dto.getDescription() != null) {
            interpreter.setDescription(dto.getDescription());
        }
        if (dto.getVideoUrl() != null) {
            interpreter.setVideoUrl(dto.getVideoUrl());
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
}
