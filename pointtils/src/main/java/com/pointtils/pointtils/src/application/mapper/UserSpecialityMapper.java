package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.UserSpecialtyDTO;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.Specialty;
import com.pointtils.pointtils.src.core.domain.entities.UserSpecialty;

public class UserSpecialityMapper {

    public class UserSpecialtyMapper {
 public static UserSpecialty toEntity(UserSpecialtyDTO dto, Person person) {
        UserSpecialty us = new UserSpecialty();

        // cria um Specialty apenas com o ID
        Specialty specialty = new Specialty();
        specialty.setId(dto.getSpecialtyId());

        us.setSpecialty(specialty);
        us.setUser(person);

        return us;
    }

    public static UserSpecialtyDTO toDTO(UserSpecialty entity) {
    return new UserSpecialtyDTO(
        entity.getId(),
        entity.getUser() != null ? entity.getUser().getId() : null,
        entity.getSpecialty() != null ? entity.getSpecialty().getId() : null,
        entity.getSpecialty() != null ? entity.getSpecialty().getName() : null
    );
}


}
}

