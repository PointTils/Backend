package com.pointtils.pointtils.src.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EmergencyContactDTO {
    
    private String name;
    private String phone;
    private String relationship;

}
