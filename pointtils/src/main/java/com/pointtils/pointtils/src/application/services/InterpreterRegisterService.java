package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterRequestDTO;
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
        Interpreter interpreter = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return responseMapper.toResponseDTO(interpreter);
    }


    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        repository.deleteById(id);
    }

    public List<InterpreterResponseDTO> findAll() {
        List<Interpreter> interpreters = repository.findAll();
        return interpreters.stream()
                .map(responseMapper::toResponseDTO)
                .toList();
    }

    public InterpreterResponseDTO updateComplete(UUID id, InterpreterRequestDTO dto) {
        Interpreter interpreter = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));

        // Atualizar todos os campos obrigatórios
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

        if (dto.getLocation() != null) {
            var l = dto.getLocation();
            Location location = interpreter.getLocation();
            if (location == null) {
                location = Location.builder().user(interpreter).build();
            }
            location.setUf(l.getUf());
            location.setCity(l.getCity());
            interpreter.setLocation(location);
        }

        Interpreter updated = repository.save(interpreter);
        return responseMapper.toResponseDTO(updated);
    }

    public InterpreterResponseDTO updatePartial(UUID id, InterpreterRequestDTO dto) {
        Interpreter interpreter = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));
        // (personalData)
        if (dto.getPersonalData() != null) {
            var p = dto.getPersonalData();

            if (p.getName() != null) {
                interpreter.setName(p.getName());
            }
            if (p.getPassword() != null) {
                interpreter.setPassword(passwordEncoder.encode(p.getPassword()));
            }
            if (p.getPhone() != null) {
                interpreter.setPhone(p.getPhone());
            }
            if (p.getPicture() != null) {
                interpreter.setPicture(p.getPicture());
            }
            if (p.getBirthday() != null) {
                interpreter.setBirthday(p.getBirthday());
            }
            if (p.getCpf() != null) {
                interpreter.setCpf(p.getCpf());
            }
            if (p.getEmail() != null) {
                interpreter.setEmail(p.getEmail());
            }
            if (p.getGender() != null) {
                interpreter.setGender(Gender.fromString(p.getGender()));
            }
        }
        //(location) 
        if (dto.getLocation() != null) {
            var l = dto.getLocation();
            Location loc = interpreter.getLocation();
            if (loc == null) {
                loc = Location.builder().user(interpreter).build();
            }
            if (l.getUf() != null) {
                loc.setUf(l.getUf());
            }
            if (l.getCity() != null) {
                loc.setCity(l.getCity());
            }
            interpreter.setLocation(loc);
        }
        //professionalData)
        if (dto.getProfessionalData() != null) {
            var prof = dto.getProfessionalData();

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

        Interpreter updated = repository.save(interpreter);
        return responseMapper.toResponseDTO(updated);
    }
}
