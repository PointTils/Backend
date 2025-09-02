package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.AccessibilityPreferencesDTO;
import com.pointtils.pointtils.src.core.domain.entities.AccessibilityPreferences;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;

public class AccessibilityMapper {
    
    public static AccessibilityPreferencesDTO toDto(AccessibilityPreferences accesssibility){
        return new AccessibilityPreferencesDTO(accesssibility.getCommunication(), 
        accesssibility.getModality(), 
        accesssibility.getGender(), 
        EmergencyContactMapper.toDto(accesssibility.getEmergency())
        );
    }

    public static AccessibilityPreferences toDomain(AccessibilityPreferencesDTO accesssibility){
        return new AccessibilityPreferences(
            accesssibility.getCommunication(),
            AppointmentModality.valueOf(accesssibility.getModality()),
            Gender.valueOf(accesssibility.getGender()),
            EmergencyContactMapper.toDomain(accesssibility.getEmergency())
        );
    }
}
