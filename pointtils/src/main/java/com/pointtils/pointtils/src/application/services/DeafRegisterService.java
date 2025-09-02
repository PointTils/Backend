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

    @Transactional
    public void delete(Long id) {
        if (!personRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        personRepository.deleteById(id);
    }
    @Transactional
    public DeafResponseDTO updatePartial(Long id, DeafRequestDTO dto) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (dto.getName() != null) {
            person.setName(dto.getName());
        }
        if (dto.getPassword() != null) {
            person.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getPhone() != null) {
            person.setPhone(dto.getPhone());
        }
        if (dto.getPicture() != null) {
            person.setPicture(dto.getPicture());
        }
        if (dto.getBirthday() != null) {
            person.setBirthday(dto.getBirthday());
        }
        if (dto.getCpf() != null) {
            person.setCpf(dto.getCpf());
        }

        Person updated = personRepository.save(person);
        return new DeafResponseDTO(updated);
    }
}
