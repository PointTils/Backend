package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.responses.PersonResponseDTO;
import com.pointtils.pointtils.src.application.util.MaskUtil;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonResponseMapper {

    private final UserSpecialtyMapper userSpecialtyMapper;

    public PersonResponseDTO toResponseDTO(Person person) {
        return PersonResponseDTO.builder()
                .id(person.getId())
                .name(person.getName())
                .gender(person.getGender())
                .birthday(person.getBirthday())
                .cpf(MaskUtil.maskCpf(person.getCpf()))
                .email(person.getEmail())
                .phone(person.getPhone())
                .picture(person.getPicture())
                .status(person.getStatus().name())
                .type(person.getType().name())
                .specialties(userSpecialtyMapper.toDtoList(person.getSpecialties()))
                .build();
    }
}

