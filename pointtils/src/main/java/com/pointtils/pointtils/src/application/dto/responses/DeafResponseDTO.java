package com.pointtils.pointtils.src.application.dto.responses;

import java.time.LocalDate;

import com.pointtils.pointtils.src.application.dto.LocationDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeafResponseDTO {

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
    private LocalDate created_at = LocalDate.now();
    


    
}
