package com.pointtils.pointtils.src.application.mapper;

import java.util.List;

import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.User;

public class LocationMapper {
    

    public static LocationDTO toDto(List<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            return null;
        }
        Location location = locations.get(0); 
        return new LocationDTO(
            location.getUf(),
            location.getCity()
        );
    }
    

    public static Location toDomain(LocationDTO locationDTO, User user) {
        if (locationDTO == null) return null;
        
        Location location = new Location(locationDTO.getUf(), locationDTO.getCity());
        location.setUser(user);
        return location;
    }
    
    //deprected
    public static LocationDTO toDto(Location location) {
        if (location == null) return null;
        return new LocationDTO(
            location.getUf(),
            location.getCity()
        );
    }

    //deprected
    public static Location toDomain(LocationDTO locationDTO) {
        if (locationDTO == null) return null;
        return new Location(locationDTO.getUf(), locationDTO.getCity());
    }
}
