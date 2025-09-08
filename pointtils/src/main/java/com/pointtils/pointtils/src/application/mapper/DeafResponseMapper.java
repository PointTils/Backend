package com.pointtils.pointtils.src.application.mapper;


import org.springframework.stereotype.Component;

import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.application.dto.responses.DeafResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Person;

@Component
public class DeafResponseMapper {
    
    public DeafResponseDTO toResponseDTO(Person person) {
        DeafResponseDTO dto = new DeafResponseDTO();
        dto.setId(person.getId());
        dto.setName(person.getName());
        dto.setEmail(person.getEmail());
        dto.setPhone(person.getPhone());
        dto.setPicture(person.getPicture());
        dto.setStatus(person.getStatus() != null ? person.getStatus().toString() : null);
        dto.setType(person.getType() != null ? person.getType().toString() : null);
        dto.setGender(person.getGender() != null ? person.getGender().toString() : null);
        dto.setBirthday(person.getBirthday());
        dto.setCpf(maskCpf(person.getCpf()));

        LocationDTO locationDto = LocationDTO.builder()
            .id(person.getLocation().getId())
            .uf(person.getLocation().getUf())
            .city(person.getLocation().getCity())
        .build();
        dto.setLocation(locationDto);
    
        return dto;
    }
    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
    }
}

