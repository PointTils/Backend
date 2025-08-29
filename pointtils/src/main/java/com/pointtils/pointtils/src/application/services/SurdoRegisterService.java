package com.pointtils.pointtils.src.application.services;


import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;

import org.springframework.security.access.method.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointtils.pointtils.src.application.dto.PointResponseDTO;
import com.pointtils.pointtils.src.application.dto.SurdoRequestDTO;
import com.pointtils.pointtils.src.application.dto.SurdoResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.infrastructure.repositories.PersonRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurdoRegisterService {
    
    private PersonRepository personRepository;
    private PasswordEncoder passwordEncoder;
    
    public SurdoRegisterService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public SurdoResponseDTO registerPerson(SurdoRequestDTO dto){
        Person person = new Person();

        person.setEmail(dto.getEmail());
        person.setPassword(passwordEncoder.encode(dto.getPassword()));
        person.setPhone(dto.getPhone());
        person.setPicture(dto.getPicture());
        person.setStatus(UserStatus.ACTIVE);
        person.setType(UserTypeE.CLIENT);

        person.setName(dto.getName());
        person.setBirthday(dto.getBirthday());
        person.setCpf(dto.getCpf());

        Person savedPerson = personRepository.save(person);

        return new SurdoResponseDTO(savedPerson);

    }

    @Transactional(readOnly = true)
    public SurdoResponseDTO findById(Long id) {

        Person person = personRepository.findById(id).orElse(null);
        if (person == null) {
            throw new EntityNotFoundException("Usuário não encontrado");
            
        }
        SurdoResponseDTO response = new SurdoResponseDTO(person);
        
        return response;
    }
    
}
