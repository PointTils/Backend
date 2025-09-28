package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.responses.SpecialtyResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Specialty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class UserSpecialtyMapper {

    public List<SpecialtyResponseDTO> toDtoList(Set<Specialty> specialties) {
        return specialties.stream()
                .map(specialty -> SpecialtyResponseDTO.builder()
                        .id(specialty.getId())
                        .name(specialty.getName())
                        .build())
                .toList();
    }
}
