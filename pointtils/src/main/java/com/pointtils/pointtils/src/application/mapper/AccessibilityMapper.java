package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.AccessibilityPreferencesDTO;
import com.pointtils.pointtils.src.core.domain.entities.AccessibilityPreferences;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;

public class AccessibilityMapper {
    
    public static AccessibilityPreferencesDTO toDto(AccessibilityPreferences accesssibility){
        if (accesssibility == null) return null;
        return new AccessibilityPreferencesDTO(accesssibility.getCommunication(), 
        accesssibility.getModality() != null ? accesssibility.getModality().name() : null, 
        accesssibility.getGender() != null ? accesssibility.getGender().name() : null, 
        EmergencyContactMapper.toDto(accesssibility.getEmergency())
        );
    }

    public static AccessibilityPreferences toDomain(AccessibilityPreferencesDTO accesssibility){
        if (accesssibility == null) return null;
        return new AccessibilityPreferences(
            accesssibility.getCommunication(),
            AppointmentModality.fromString(accesssibility.getModality()),
            Gender.fromString(accesssibility.getAccessGender()),
            EmergencyContactMapper.toDomain(accesssibility.getEmergency())
        );
    }
}
