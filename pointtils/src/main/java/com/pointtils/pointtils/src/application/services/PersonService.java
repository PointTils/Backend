package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.PersonCreationRequestDTO;
import com.pointtils.pointtils.src.application.dto.PersonDTO;
import com.pointtils.pointtils.src.application.dto.requests.PersonPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.PersonResponseDTO;
import com.pointtils.pointtils.src.application.mapper.PersonResponseMapper;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.infrastructure.repositories.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final PersonResponseMapper personResponseMapper;

    public PersonResponseDTO registerPerson(PersonCreationRequestDTO dto) {
        Person person = new Person();

        person.setId(person.getId());
        person.setEmail(dto.getEmail());
        person.setPassword(passwordEncoder.encode(dto.getPassword()));
        person.setPhone(dto.getPhone());
        person.setPicture(dto.getPicture());
        person.setStatus(UserStatus.ACTIVE);
        person.setType(UserTypeE.PERSON);
        person.setName(dto.getName());
        person.setGender(dto.getGender());
        person.setBirthday(dto.getBirthday());
        person.setCpf(dto.getCpf());

        Person savedPerson = personRepository.save(person);
        return personResponseMapper.toResponseDTO(savedPerson);
    }

    public PersonResponseDTO findById(UUID id) {
        Person person = findPersonById(id);
        return personResponseMapper.toResponseDTO(person);
    }

    public void delete(UUID id) {
        Person person = findPersonById(id);
        person.setStatus(UserStatus.INACTIVE);
        personRepository.save(person);
    }

    public List<PersonResponseDTO> findAll() {
        List<Person> persons = personRepository.findAllByType(UserTypeE.PERSON);
        return persons.stream()
                .map(personResponseMapper::toResponseDTO)
                .toList();
    }

    public PersonResponseDTO updateComplete(UUID id, PersonDTO dto) {
        Person person = findPersonById(id);

        person.setName(dto.getName());
        person.setEmail(dto.getEmail());
        person.setPhone(dto.getPhone());
        person.setPicture(dto.getPicture());
        person.setBirthday(dto.getBirthday());
        person.setCpf(dto.getCpf());
        person.setGender(dto.getGender());

        Person updated = personRepository.save(person);
        return personResponseMapper.toResponseDTO(updated);
    }

    public PersonResponseDTO updatePartial(UUID id, PersonPatchRequestDTO dto) {
        Person person = findPersonById(id);

        if (dto.getName() != null) {
            person.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            person.setEmail(dto.getEmail());
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
        if (dto.getGender() != null) {
            person.setGender(dto.getGender());
        }

        Person updated = personRepository.save(person);
        return personResponseMapper.toResponseDTO(updated);
    }

    private Person findPersonById(UUID id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }
}
