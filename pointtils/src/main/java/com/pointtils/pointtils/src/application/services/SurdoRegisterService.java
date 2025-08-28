package com.pointtils.pointtils.src.application.services;


import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pointtils.pointtils.src.application.dto.SurdoRequestDTO;
import com.pointtils.pointtils.src.application.dto.SurdoResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.infrastructure.repositories.PersonRepository;

@Service
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
        person.setPhone(dto.getEmail());
        person.setPicture(dto.getEmail());
        person.setStatus(UserStatus.ACTIVE);
        person.setType(UserTypeE.CLIENT);

        person.setName(dto.getName());
        person.setBirthday(dto.getBirthday());
        person.setCpf(dto.getCpf());

        Person savedPerson = personRepository.save(person);

        return new SurdoResponseDTO(savedPerson);

    }
    
}
