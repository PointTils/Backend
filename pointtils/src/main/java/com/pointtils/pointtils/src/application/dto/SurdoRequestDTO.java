package com.pointtils.pointtils.src.application.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SurdoRequestDTO {
    
    private String name;
    private String email;
    private String password;
    private String phone;
    private String gender;
    private LocalDate birthday;
    private String cpf;
    private String picture;
    private String status;
    private String type;

    private LocationDTO location;
}
