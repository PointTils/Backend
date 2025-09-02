package com.pointtils.pointtils.src.core.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EmergencyContact {
    
    private String name;
    private String phone;
    private String relationship;

}
