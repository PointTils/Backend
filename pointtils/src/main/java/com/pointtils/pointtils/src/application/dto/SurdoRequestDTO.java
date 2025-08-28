package com.pointtils.pointtils.src.application.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
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
