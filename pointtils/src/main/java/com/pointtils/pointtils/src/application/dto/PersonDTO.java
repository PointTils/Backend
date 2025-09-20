package com.pointtils.pointtils.src.application.dto;
import java.time.LocalDate;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PersonDTO extends UserDTO {
    private String name;
    private Gender gender;
    private LocalDate birthday;
    private String cpf;    
}
