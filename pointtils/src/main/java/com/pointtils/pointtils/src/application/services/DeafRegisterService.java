package com.pointtils.pointtils.src.application.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
@Transactional
@RequiredArgsConstructor
public class DeafRegisterService {

    private static final String USER_NOT_FOUND = "Usuário não encontrado";
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
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        return deafResponseMapper.toResponseDTO(person);
    }


    public void delete(UUID id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        try {
            personRepository.delete(person);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("fk_appointment_user")) {
                throw new RuntimeException("Não é possível deletar este usuário pois ele possui agendamentos associados. " +
                        "Delete primeiro os agendamentos relacionados a este usuário.");
            } else if (errorMessage.contains("user_specialties")) {
                throw new RuntimeException("Não é possível deletar este usuário pois ele possui especialidades associadas. " +
                        "Delete primeiro as especialidades relacionadas a este usuário.");
            } else {
                throw new RuntimeException("Não é possível deletar este usuário pois ele possui dependências no sistema. " +
                        "Erro: " + errorMessage);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao deletar usuário: " + e.getMessage(), e);
        }
    }

    public List<DeafResponseDTO> findAll() {
        List<Person> persons = personRepository.findAllByType(UserTypeE.CLIENT);
        return persons.stream()
                .map(deafResponseMapper::toResponseDTO)
                .toList();
    }

    public DeafResponseDTO updateComplete(UUID id, DeafRequestDTO dto) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

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
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        if (dto.getPersonalRequestDTO() != null) {
            var p = dto.getPersonalRequestDTO();

            if (p.getName() != null) {
                person.setName(p.getName());
            }
            if (p.getPassword() != null) {
                person.setPassword(passwordEncoder.encode(p.getPassword()));
            }
            if (p.getPhone() != null) {
                person.setPhone(p.getPhone());
            }
            if (p.getPicture() != null) {
                person.setPicture(p.getPicture());
            }
            if (p.getBirthday() != null) {
                person.setBirthday(p.getBirthday());
            }
            if (p.getCpf() != null) {
                person.setCpf(p.getCpf());
            }
            if (p.getEmail() != null) {
                person.setEmail(p.getEmail());
            }
            if (p.getGender() != null) {
                person.setGender(Gender.fromString(p.getGender()));
            }
        }

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
}
