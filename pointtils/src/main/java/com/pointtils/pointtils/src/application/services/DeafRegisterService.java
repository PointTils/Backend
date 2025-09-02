package com.pointtils.pointtils.src.application.services;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointtils.pointtils.src.application.dto.DeafRequestDTO;
import com.pointtils.pointtils.src.application.dto.DeafResponseDTO;
import com.pointtils.pointtils.src.application.mapper.AccessibilityMapper;
import com.pointtils.pointtils.src.application.mapper.LocationMapper;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.infrastructure.repositories.PersonRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
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
        person.setGender(Gender.fromString(dto.getGender()));
        person.setBirthday(dto.getBirthday());
        person.setCpf(dto.getCpf());
        person.setLocation(LocationMapper.toDomain(dto.getLocation()));
        person.setAp(AccessibilityMapper.toDomain(dto.getAccessibility()));

        Person savedPerson = personRepository.save(person);

        return new DeafResponseDTO(savedPerson);
    }


    public DeafResponseDTO findById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return new DeafResponseDTO(person);
    }
}
