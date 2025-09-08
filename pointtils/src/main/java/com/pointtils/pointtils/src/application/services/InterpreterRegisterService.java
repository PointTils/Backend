package com.pointtils.pointtils.src.application.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InterpreterRegisterService {

    private final InterpreterRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final InterpreterResponseMapper responseMapper;
    
    public InterpreterResponseDTO register(InterpreterRequestDTO request){
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
            .rating(0.0)
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
        
        InterpreterResponseDTO response = responseMapper.toResponseDTO(savedInterpreter);
        return response;
    }
}
