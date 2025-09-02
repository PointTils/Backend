package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.core.domain.entities.Location;

public class LocationMapper {
    
    public static LocationDTO toDto(Location location){
        return new LocationDTO(
            location.getUf(),
            location.getCity()
        );
    }

    public static Location toDomain(LocationDTO locationDTO){
        return new Location(
            locationDTO.getUf(),
            locationDTO.getCity()
        );
    }
}
