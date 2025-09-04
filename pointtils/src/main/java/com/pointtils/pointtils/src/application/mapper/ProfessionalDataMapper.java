package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.ProfessionalDataDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;

public class ProfessionalDataMapper {
    
    public static void mapToInterpreter(ProfessionalDataDTO dto, Interpreter interpreter) {
        interpreter.setCnpj(dto.getCnpj());
        interpreter.setRating(dto.getRating());
        interpreter.setMinValue(dto.getMinValue());
        interpreter.setMaxValue(dto.getMaxValue());
        interpreter.setModality(InterpreterModality.fromString(dto.getModality()));
        interpreter.setDescription(dto.getDescription());
        interpreter.setImageRights(dto.getImageRights());
    }
    
    public static InterpreterModality toInterpreterModality(String modality) {
        return InterpreterModality.fromString(modality);
    }
    
    public static ProfessionalDataDTO toDto(Interpreter interpreter) {
        ProfessionalDataDTO dto = new ProfessionalDataDTO();
        dto.setCnpj(interpreter.getCnpj());
        dto.setRating(interpreter.getRating());
        dto.setMinValue(interpreter.getMinValue());
        dto.setMaxValue(interpreter.getMaxValue());
        dto.setModality(interpreter.getModality() != null ? interpreter.getModality().name().toLowerCase() : null);
        dto.setDescription(interpreter.getDescription());
        dto.setImageRights(interpreter.getImageRights());
        return dto;
    }
}
