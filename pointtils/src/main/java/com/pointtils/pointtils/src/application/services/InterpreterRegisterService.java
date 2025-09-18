package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.PersonalPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ProfessionalPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.mapper.InterpreterMapper;
import com.pointtils.pointtils.src.application.mapper.InterpreterResponseMapper;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
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


    public InterpreterResponseDTO registerBasic(InterpreterBasicRequestDTO request) {
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
                .cnpj(null)
                .rating(BigDecimal.ZERO)
                .minValue(BigDecimal.ZERO)
                .maxValue(BigDecimal.ZERO)
                .imageRights(false)
                .modality(InterpreterModality.ALL)
                .description("")
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
