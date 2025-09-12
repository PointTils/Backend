package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.DeafPatchRequestDTO;
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
        person.setType(UserTypeE.CLIENT);
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
        return deafResponseMapper.toResponseDTO(savedPerson);
    }

    public DeafResponseDTO findById(UUID id) {
        Person person = findPersonById(id);
        return deafResponseMapper.toResponseDTO(person);
    }

    public void delete(UUID id) {
        Person person = findPersonById(id);
        person.setStatus(UserStatus.INACTIVE);
        personRepository.save(person);
    }

    public List<DeafResponseDTO> findAll() {
        List<Person> persons = personRepository.findAllByType(UserTypeE.CLIENT);
        return persons.stream()
                .map(deafResponseMapper::toResponseDTO)
                .toList();
    }

    public DeafResponseDTO updateComplete(UUID id, DeafRequestDTO dto) {
        Person person = findPersonById(id);
        if (dto.getPersonalRequestDTO() != null) {
            var p = dto.getPersonalRequestDTO();
            person.setName(p.getName());
            person.setEmail(p.getEmail());
            person.setPhone(p.getPhone());
            person.setPicture(p.getPicture());
            person.setBirthday(p.getBirthday());
            person.setCpf(p.getCpf());
            person.setGender(Gender.fromString(p.getGender()));

            if (p.getPassword() != null) {
                person.setPassword(passwordEncoder.encode(p.getPassword()));
            }
        }

        if (dto.getLocation() != null) {
            var l = dto.getLocation();
            Location location = person.getLocation();
            if (location == null) {
                location = Location.builder().user(person).build();
            }
            location.setUf(l.getUf());
            location.setCity(l.getCity());
            person.setLocation(location);
        }

        Person updated = personRepository.save(person);
        return deafResponseMapper.toResponseDTO(updated);
    }

    public DeafResponseDTO updatePartial(UUID id, DeafPatchRequestDTO dto) {
        Person person = findPersonById(id);
        executePartialUpdateOfPersonalData(dto, person);

        if (dto.getLocation() != null) {
            var l = dto.getLocation();
            Location location = person.getLocation();
            if (location == null) {
                location = Location.builder().user(person).build();
            }
            if (l.getUf() != null) {
                location.setUf(l.getUf());
            }
            if (l.getCity() != null) {
                location.setCity(l.getCity());
            }
            person.setLocation(location);
        }

        Person updated = personRepository.save(person);
        return deafResponseMapper.toResponseDTO(updated);
    }

    private void executePartialUpdateOfPersonalData(DeafPatchRequestDTO dto, Person person) {
        if (dto.getPersonalRequestDTO() == null) {
            return;
        }
        var personalRequest = dto.getPersonalRequestDTO();
        if (personalRequest.getName() != null) {
            person.setName(personalRequest.getName());
        }
        if (personalRequest.getPassword() != null) {
            person.setPassword(passwordEncoder.encode(personalRequest.getPassword()));
        }
        if (personalRequest.getPhone() != null) {
            person.setPhone(personalRequest.getPhone());
        }
        if (personalRequest.getPicture() != null) {
            person.setPicture(personalRequest.getPicture());
        }
        if (personalRequest.getBirthday() != null) {
            person.setBirthday(personalRequest.getBirthday());
        }
        if (personalRequest.getCpf() != null) {
            person.setCpf(personalRequest.getCpf());
        }
        if (personalRequest.getEmail() != null) {
            person.setEmail(personalRequest.getEmail());
        }
        if (personalRequest.getGender() != null) {
            person.setGender(Gender.fromString(personalRequest.getGender()));
        }
    }

    private Person findPersonById(UUID id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }
}
