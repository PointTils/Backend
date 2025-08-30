package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointtils.pointtils.src.application.dto.DeafRequestDTO;
import com.pointtils.pointtils.src.application.dto.DeafResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.infrastructure.repositories.PersonRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeafRegisterService {
    
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DeafResponseDTO registerPerson(DeafRequestDTO dto) {
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

        return new DeafResponseDTO(savedPerson);
    }

    @Transactional(readOnly = true)
    public DeafResponseDTO findById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return new DeafResponseDTO(person);
    }
}
