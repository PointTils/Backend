package com.pointtils.pointtils.src.core.domain.entities;

import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class AccessibilityPreferences {
    
    private String communication;
    private AppointmentModality modality;
    private Gender gender;
    
    @Embedded
    private EmergencyContact emergency;

    public AccessibilityPreferences(String communication, AppointmentModality modality, Gender gender, EmergencyContact ec){
        this.communication = communication;
        this.modality = modality;
        this.gender = gender;
        this.emergency = ec;
    }

}
