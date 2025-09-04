package com.pointtils.pointtils.src.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.pointtils.pointtils.src.core.domain.entities.Interpreter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterpreterResponseDTO {
    
    private Long id;
    private String email;
    private String phone;
    private String picture;
    private String status;
    private String type;
    private String name;
    private String gender;
    private LocalDate birthday;
    private String cpf;
    private LocationDTO location;
    
    private String cnpj;
    private Double rating;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private String modality;
    private String description;
    private Boolean imageRights;
    
    private List<String> specialties;
    private List<InitialScheduleDTO> initialSchedule;
    
    private LocalDate createdAt = LocalDate.now();
    
    public InterpreterResponseDTO(Interpreter interpreter) {
        this.id = interpreter.getId();
        this.email = interpreter.getEmail();
        this.phone = interpreter.getPhone();
        this.picture = interpreter.getPicture();
        this.status = interpreter.getStatus() != null ? interpreter.getStatus().name() : null;
        this.type = interpreter.getType() != null ? interpreter.getType().name() : null;
        
        this.name = interpreter.getName();
        this.gender = interpreter.getGender() != null ? interpreter.getGender().name() : null;
        this.birthday = interpreter.getBirthday();
        this.cpf = interpreter.getCpf();
        
        this.cnpj = interpreter.getCnpj();
        this.rating = interpreter.getRating();
        this.minValue = interpreter.getMinValue();
        this.maxValue = interpreter.getMaxValue();
        this.modality = interpreter.getModality() != null ? interpreter.getModality().name() : null;
        this.description = interpreter.getDescription();
        this.imageRights = interpreter.getImageRights();
        
        this.createdAt = LocalDate.now();
    }
}
