package com.pointtils.pointtils.src.core.domain.entities;

import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;

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
public class AccessibilityPreferences {
    
    private String communication;
    private AppointmentModality modality;
    private Gender gender;
    private EmergencyContact emergency;
}
