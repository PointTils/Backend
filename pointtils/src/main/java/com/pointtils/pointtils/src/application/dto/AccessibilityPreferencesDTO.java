package com.pointtils.pointtils.src.application.dto;

import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AccessibilityPreferencesDTO {

    public AccessibilityPreferencesDTO(String communication2, AppointmentModality modality2, Gender gender2,
            EmergencyContactDTO dto) {
        //TODO Auto-generated constructor stub
    }
    private String communication;
    private String modality;
    private String gender;
    private EmergencyContactDTO emergency;

}
