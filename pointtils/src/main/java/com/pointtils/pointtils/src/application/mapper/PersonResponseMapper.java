package com.pointtils.pointtils.src.application.mapper;


import com.pointtils.pointtils.src.application.dto.PersonDTO;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonResponseMapper {

    public PersonDTO toResponseDTO(Person person) {
        PersonDTO dto = new PersonDTO();
        dto.setName(person.getName());
        dto.setGender(person.getGender());
        dto.setBirthday(person.getBirthday());
        dto.setCpf(maskCpf(person.getCpf()));
        dto.setEmail(person.getEmail());
        dto.setPhone(person.getPhone());
        dto.setPicture(person.getPicture());
        dto.setStatus(person.getStatus());
        dto.setType(person.getType());
        return dto;
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
    }
}

