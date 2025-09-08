package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.requests.ProfessionalRequestDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;

public class InterpreterMapper {
    
    public static void toDomain(ProfessionalRequestDTO dto, Interpreter interpreter) {
        interpreter.setCnpj(dto.getCnpj());
        interpreter.setMinValue(dto.getMinValue());
        interpreter.setMaxValue(dto.getMaxValue());
        interpreter.setModality(InterpreterModality.fromString(dto.getModality()));
        interpreter.setDescription(dto.getDescription());
        interpreter.setImageRights(dto.getImageRights());
    }
    
    public static InterpreterModality toInterpreterModality(String modality) {
        return InterpreterModality.fromString(modality);
    }
    
    public static ProfessionalRequestDTO toDto(Interpreter interpreter) {
        ProfessionalRequestDTO dto = ProfessionalRequestDTO.builder()
            .cnpj(interpreter.getCnpj())
            .minValue(interpreter.getMinValue())
            .maxValue(interpreter.getMaxValue())
            .modality(interpreter.getModality() != null ? interpreter.getModality().name().toLowerCase() : null)
            .description(interpreter.getDescription())
            .imageRights(interpreter.getImageRights())
        .build();
        return dto;
    }
}
