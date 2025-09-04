package com.pointtils.pointtils.src.application.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointtils.pointtils.src.application.dto.InterpreterRequestDTO;
import com.pointtils.pointtils.src.application.dto.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.mapper.ProfessionalDataMapper;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
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
    private final LocationService locationService;
    private final PasswordEncoder passwordEncoder;
    
    public InterpreterResponseDTO register(InterpreterRequestDTO request){
        Interpreter interpreter = new Interpreter();
        
        interpreter.setEmail(request.getPersonalData().getEmail());
        interpreter.setPassword(passwordEncoder.encode(request.getPersonalData().getPassword()));
        interpreter.setPhone(request.getPersonalData().getPhone());
        interpreter.setPicture(request.getPersonalData().getPicture());
        interpreter.setStatus(UserStatus.ACTIVE);
        interpreter.setType(UserTypeE.INTERPRETER);
        
        interpreter.setName(request.getPersonalData().getName());
        interpreter.setGender(Gender.fromString(request.getPersonalData().getGender()));
        interpreter.setBirthday(request.getPersonalData().getBirthday());
        interpreter.setCpf(request.getPersonalData().getCpf());
        
        interpreter.setCnpj(request.getProfessionalData().getCnpj());
        interpreter.setRating(request.getProfessionalData().getRating());
        interpreter.setMinValue(request.getProfessionalData().getMinValue());
        interpreter.setMaxValue(request.getProfessionalData().getMaxValue());
        interpreter.setImageRights(request.getProfessionalData().getImageRights());
        interpreter.setModality(ProfessionalDataMapper.toInterpreterModality(request.getProfessionalData().getModality()));
        interpreter.setDescription(request.getProfessionalData().getDescription());
        
        Interpreter savedInterpreter = repository.save(interpreter);
        
        if (request.getLocation() != null) {
            locationService.saveLocation(request.getLocation(), savedInterpreter);
        }
        
        return new InterpreterResponseDTO(savedInterpreter);
    }
}