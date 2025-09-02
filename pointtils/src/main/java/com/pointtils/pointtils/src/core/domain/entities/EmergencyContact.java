package com.pointtils.pointtils.src.core.domain.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmergencyContact {
    
    private String name;
    private String phone;
    private String relationship;

}
