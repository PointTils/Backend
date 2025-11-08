package com.pointtils.pointtils.src.application.dto.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindAllInterpreterDTO {
    String modality;
    String gender;
    String city;
    String uf;
    String neighborhood;
    String specialty;
    String availableDate;
    String name;
}
