package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.EmergencyContactDTO;
import com.pointtils.pointtils.src.core.domain.entities.EmergencyContact;

public class EmergencyContactMapper {
    
    public static EmergencyContactDTO toDto(EmergencyContact emergency){
        return new EmergencyContactDTO(
            emergency.getName(),
            emergency.getPhone(),
            emergency.getRelationship()
        );
    }

    public static EmergencyContact toDomain(EmergencyContactDTO emergency){
        return new EmergencyContact(
            emergency.getName(),
            emergency.getPhone(),
            emergency.getRelationship()
        );
    }


    
}
