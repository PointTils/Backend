package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterListResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.ProfessionalDataListResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.ProfessionalDataResponseDTO;
import com.pointtils.pointtils.src.application.util.MaskUtil;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InterpreterResponseMapper {

    private final UserSpecialtyMapper userSpecialtyMapper;
    private final LocationMapper locationMapper;

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
                .cpf(MaskUtil.maskCpf(interpreter.getCpf()))
                .professionalData(toProfessionalDataResponseDTO(interpreter))
                .specialties(userSpecialtyMapper.toDtoList(interpreter.getSpecialties()))
                .build();

        if (interpreter.getLocations() != null) {
            List<LocationDTO> locationList = interpreter.getLocations().stream()
                    .map(locationMapper::toDto)
                    .toList();
            dto.setLocations(locationList);
        } else {
            dto.setLocations(Collections.emptyList());
        }
        return dto;
    }

    public InterpreterListResponseDTO toListResponseDTO(Interpreter interpreter) {
        return InterpreterListResponseDTO.builder()
                .id(interpreter.getId())
                .name(interpreter.getName())
                .locations(interpreter.getLocations() != null
                        ? interpreter.getLocations().stream()
                        .map(locationMapper::toDto)
                        .toList()
                        : Collections.emptyList())
                .picture(interpreter.getPicture())
                .specialties(userSpecialtyMapper.toDtoList(interpreter.getSpecialties()))
                .professionalData(toProfessionalDataListResponseDTO(interpreter))
                .build();
    }

    private ProfessionalDataResponseDTO toProfessionalDataResponseDTO(Interpreter interpreter) {
        return ProfessionalDataResponseDTO.builder()
                .cnpj(interpreter.getCnpj())
                .rating(interpreter.getRating() != null ? interpreter.getRating() : BigDecimal.ZERO)
                .modality(interpreter.getModality())
                .description(interpreter.getDescription())
                .imageRights(interpreter.getImageRights())
                .build();
    }

    private ProfessionalDataListResponseDTO toProfessionalDataListResponseDTO(Interpreter interpreter) {
        return ProfessionalDataListResponseDTO.builder()
                .rating(interpreter.getRating() != null ? interpreter.getRating() : BigDecimal.ZERO)
                .modality(interpreter.getModality())
                .build();
    }
}
