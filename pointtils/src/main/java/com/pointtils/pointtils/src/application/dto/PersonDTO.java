package com.pointtils.pointtils.src.application.dto;

import java.time.LocalDate;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PersonDTO extends UserDTO {
    
    @NotBlank(message = "O nome é obrigatório")
    private String name;
    
    @NotNull(message = "O gênero é obrigatório")
    private Gender gender;
    
    @NotNull(message = "A data de nascimento é obrigatória")
    private LocalDate birthday;
    
    @NotBlank(message = "O CPF é obrigatório")
    @Size(min = 11, max = 11, message = "O CPF deve ter exatamente 11 dígitos")
    private String cpf;    
}
