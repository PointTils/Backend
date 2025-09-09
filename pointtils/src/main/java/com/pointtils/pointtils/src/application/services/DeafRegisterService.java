package com.pointtils.pointtils.src.application.services;


import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointtils.pointtils.src.application.dto.requests.DeafRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.DeafResponseDTO;
import com.pointtils.pointtils.src.application.mapper.DeafResponseMapper;
import com.pointtils.pointtils.src.core.domain.entities.Location;
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
    private final DeafResponseMapper deafResponseMapper;
    
    public DeafResponseDTO registerPerson(DeafRequestDTO dto) {
        Person person = new Person();

        person.setEmail(dto.getPersonalRequestDTO().getEmail());
        person.setPassword(passwordEncoder.encode(dto.getPersonalRequestDTO().getPassword()));
        person.setPhone(dto.getPersonalRequestDTO().getPhone());
        person.setPicture(dto.getPersonalRequestDTO().getPicture());
        person.setStatus(UserStatus.ACTIVE);
        person.setType(UserTypeE.PERSON);
        person.setName(dto.getPersonalRequestDTO().getName());
        person.setGender(Gender.fromString(dto.getPersonalRequestDTO().getGender()));
        person.setBirthday(dto.getPersonalRequestDTO().getBirthday());
        person.setCpf(dto.getPersonalRequestDTO().getCpf());
        
        if (dto.getLocation() != null) {
            Location location = Location.builder()
            .uf(dto.getLocation().getUf())
            .city(dto.getLocation().getCity())
            .user(person)
            .build();
        
            person.setLocation(location);
        }
        Person savedPerson = personRepository.save(person);
        DeafResponseDTO response = deafResponseMapper.toResponseDTO(savedPerson);
        return response;
    }


    public DeafResponseDTO findById(UUID id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        DeafResponseDTO response = deafResponseMapper.toResponseDTO(person);
        return response;
    }


    public void delete(UUID id) {
        if (!personRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        personRepository.deleteById(id);
    }

    public DeafResponseDTO updatePartial(UUID id, DeafRequestDTO dto) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (dto.getPersonalRequestDTO().getName() != null) {
            person.setName(dto.getPersonalRequestDTO().getName());
        }
        if (dto.getPersonalRequestDTO().getPassword() != null) {
            person.setPassword(passwordEncoder.encode(dto.getPersonalRequestDTO().getPassword()));
        }
        if (dto.getPersonalRequestDTO().getPhone() != null) {
            person.setPhone(dto.getPersonalRequestDTO().getPhone());
        }
        if (dto.getPersonalRequestDTO().getPicture() != null) {
            person.setPicture(dto.getPersonalRequestDTO().getPicture());
        }
        if (dto.getPersonalRequestDTO().getBirthday() != null) {
            person.setBirthday(dto.getPersonalRequestDTO().getBirthday());
        }
        if (dto.getPersonalRequestDTO().getCpf() != null) {
            person.setCpf(dto.getPersonalRequestDTO().getCpf());
        }
        Person updated = personRepository.save(person);

        DeafResponseDTO response = deafResponseMapper.toResponseDTO(updated);
        return response;
    }
}
