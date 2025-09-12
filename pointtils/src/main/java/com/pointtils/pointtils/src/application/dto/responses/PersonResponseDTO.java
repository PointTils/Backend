package com.pointtils.pointtils.src.application.dto.responses;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponseDTO {
    private String name;
    private String gender;
    private LocalDate birthday;
    private String cpf;
}