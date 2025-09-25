package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.ProfessionalDataResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.SpecialtyResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Component
public class InterpreterResponseMapper {

    public InterpreterResponseDTO toResponseDTO(Interpreter interpreter) {
        InterpreterResponseDTO dto = InterpreterResponseDTO.builder()
                .id(interpreter.getId())
                .email(interpreter.getEmail())
                .type(interpreter.getType().name())
                .status(interpreter.getStatus().toString())
                .phone(interpreter.getPhone())
                .picture(interpreter.getPicture())
                .name(interpreter.getName())
                .gender(interpreter.getGender())
                .birthday(interpreter.getBirthday())
                .cpf(maskCpf(interpreter.getCpf()))
                .build();

        ProfessionalDataResponseDTO professionalDto = ProfessionalDataResponseDTO.builder()
                .cnpj(interpreter.getCnpj())
                .rating(interpreter.getRating() != null ? interpreter.getRating() : BigDecimal.ZERO)
                .minValue(interpreter.getMinValue())
                .maxValue(interpreter.getMaxValue())
                .modality(interpreter.getModality() != null ? interpreter.getModality().name() : null)
                .description(interpreter.getDescription())
                .imageRights(interpreter.getImageRights())
                .build();
        dto.setProfessionalData(professionalDto);

        if (interpreter.getLocations() != null) {
            List<LocationDTO> locationList = interpreter.getLocations().stream()
                    .map(LocationMapper::toDto)
                    .toList();
            dto.setLocations(locationList);
        } else {
            dto.setLocations(Collections.emptyList());
        }

        List<SpecialtyResponseDTO> specialtyDtos = interpreter.getSpecialties().stream()
                .map(specialty -> SpecialtyResponseDTO.builder()
                        .id(specialty.getId())
                        .name(specialty.getName())
                        .build())
                .toList();
        dto.setSpecialties(specialtyDtos);

        return dto;
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
    }
}
