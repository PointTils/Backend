package com.pointtils.pointtils.src.application.services;

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
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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

    public List<InterpreterResponseDTO> findAll() {
        List<Interpreter> interpreters = repository.findAll();
        return interpreters.stream()
                .map(responseMapper::toResponseDTO)
                .toList();
    }

    public InterpreterResponseDTO updateComplete(UUID id, InterpreterRequestDTO dto) {
        Interpreter interpreter = findInterpreterById(id);

        if (dto.getPersonalData() != null) {
            var p = dto.getPersonalData();
            interpreter.setName(p.getName());
            interpreter.setEmail(p.getEmail());
            interpreter.setPhone(p.getPhone());
            interpreter.setPicture(p.getPicture());
            interpreter.setBirthday(p.getBirthday());
            interpreter.setCpf(p.getCpf());
            interpreter.setGender(Gender.fromString(p.getGender()));

            if (p.getPassword() != null) {
                interpreter.setPassword(passwordEncoder.encode(p.getPassword()));
            }
        }

        if (dto.getProfessionalData() != null) {
            var prof = dto.getProfessionalData();
            interpreter.setCnpj(prof.getCnpj());
            interpreter.setMinValue(prof.getMinValue());
            interpreter.setMaxValue(prof.getMaxValue());
            interpreter.setImageRights(prof.getImageRights());
            interpreter.setModality(InterpreterMapper.toInterpreterModality(prof.getModality()));
            interpreter.setDescription(prof.getDescription());
        }

        updateLocation(dto.getLocation(), interpreter);

        Interpreter updated = repository.save(interpreter);
        return responseMapper.toResponseDTO(updated);
    }

    public InterpreterResponseDTO updatePartial(UUID id, InterpreterPatchRequestDTO dto) {
        Interpreter interpreter = findInterpreterById(id);
        updatePersonalPatchRequest(dto.getPersonalData(), interpreter);
        updateProfessionalPatchRequest(dto.getProfessionalData(), interpreter);
        updateLocation(dto.getLocation(), interpreter);

        Interpreter updated = repository.save(interpreter);
        return responseMapper.toResponseDTO(updated);
    }

    private void updatePersonalPatchRequest(PersonalPatchRequestDTO personal, Interpreter interpreter) {
        if (personal != null) {
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
    }

    private void updateProfessionalPatchRequest(ProfessionalPatchRequestDTO prof, Interpreter interpreter) {
        if (prof != null) {
            if (prof.getCnpj() != null) {
                interpreter.setCnpj(prof.getCnpj());
            }
            if (prof.getMinValue() != null) {
                interpreter.setMinValue(prof.getMinValue());
            }
            if (prof.getMaxValue() != null) {
                interpreter.setMaxValue(prof.getMaxValue());
            }
            if (prof.getImageRights() != null) {
                interpreter.setImageRights(prof.getImageRights());
            }
            if (prof.getModality() != null) {
                interpreter.setModality(InterpreterMapper.toInterpreterModality(prof.getModality()));
            }
            if (prof.getDescription() != null) {
                interpreter.setDescription(prof.getDescription());
            }
        }
    }

    private void updateLocation(LocationDTO locationDTO, Interpreter interpreter) {
        if (locationDTO != null) {
            Location location = interpreter.getLocation();
            if (location == null) {
                location = Location.builder().user(interpreter).build();
            }
            location.setUf(locationDTO.getUf());
            location.setCity(locationDTO.getCity());
            interpreter.setLocation(location);
        }
    }

    private Interpreter findInterpreterById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));
    }
}
