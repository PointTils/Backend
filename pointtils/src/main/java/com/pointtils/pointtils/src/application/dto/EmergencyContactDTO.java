package com.pointtils.pointtils.src.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EmergencyContactDTO {
    
    private String name;
    private String phone;
    private String relationship;

}
