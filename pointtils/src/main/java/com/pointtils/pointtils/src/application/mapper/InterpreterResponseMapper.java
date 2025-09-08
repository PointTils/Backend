package com.pointtils.pointtils.src.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.PersonResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.ProfessionalInfoResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.SpecialtyResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;

@Component
public class InterpreterResponseMapper {
    
    public InterpreterResponseDTO toResponseDTO(Interpreter interpreter) {
        InterpreterResponseDTO dto = new InterpreterResponseDTO();
        
        dto.setId_interpreter(interpreter.getId());
        
        if (interpreter != null) {
            UserResponseDTO userDto = UserResponseDTO.builder()
                .id(interpreter.getId())
                .email(interpreter.getEmail())
                .type(interpreter.getType().toString().toLowerCase())
                .status(interpreter.getStatus().toString().toLowerCase())
                .phone(interpreter.getPhone())
                .picture(interpreter.getPicture())
            .build();
            dto.setUser(userDto);
        
            PersonResponseDTO personDto = PersonResponseDTO.builder()
                .name(interpreter.getName())
                .gender(interpreter.getGender().name())
                .birthday(interpreter.getBirthday())
                .cpf(maskCpf(interpreter.getCpf()))
            .build();
            dto.setPerson(personDto);
            
            ProfessionalInfoResponseDTO professionalDto = ProfessionalInfoResponseDTO.builder()
                .cnpj(interpreter.getCnpj())
                .rating(interpreter.getRating() != null ? interpreter.getRating() : 0.0)
                .minValue(interpreter.getMinValue())
                .maxValue(interpreter.getMaxValue())
                .modality(interpreter.getModality().name())
                .description(interpreter.getDescription())
                .imageRights(interpreter.getImageRights())
            .build();
            dto.setProfessionalInfo(professionalDto);
            
            LocationDTO locationDto = LocationDTO.builder()
                .id(interpreter.getLocation().getId())
                .uf(interpreter.getLocation().getUf())
                .city(interpreter.getLocation().getCity())
            .build();
            dto.setLocation(locationDto);
            
        
            List<SpecialtyResponseDTO> specialtyDtos = interpreter.getSpecialties()
                .stream()
                .map(specialty -> SpecialtyResponseDTO.builder().id(specialty.getId()).name(specialty.getName()).build())
                .collect(Collectors.toList());
            dto.setSpecialties(specialtyDtos);
            
        }
        return dto;
    }
    
    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
    }
}