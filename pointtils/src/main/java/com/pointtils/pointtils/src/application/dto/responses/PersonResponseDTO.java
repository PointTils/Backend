package com.pointtils.pointtils.src.application.dto.responses;

import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PersonResponseDTO extends UserResponseDTO {

    private String name;
    private Gender gender;
    private LocalDate birthday;
    private String cpf;
}
