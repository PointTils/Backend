package com.pointtils.pointtils.src.application.dto.requests;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterpreterSpecificationFilterDTO {
    private InterpreterModality modality;
    private String uf;
    private String city;
    private String neighborhood;
    private List<UUID> specialties;
    private Gender gender;
    private LocalDateTime availableDate;
    private String name;
}
