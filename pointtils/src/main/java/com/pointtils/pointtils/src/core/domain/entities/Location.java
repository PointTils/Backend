package com.pointtils.pointtils.src.core.domain.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@Getter
@Setter
public class Location {
    
    private String uf;
    private String city;
    
}
