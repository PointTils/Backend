package com.pointtils.pointtils.src.application.mapper;


import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LocationMapper {

    public static LocationDTO toDto(Location location) {
        if (location == null) return null;
        return LocationDTO.builder()
                .uf(location.getUf())
                .city(location.getCity())
                .build();
    }

    public static Location toDomain(LocationDTO locationDTO) {
        if (locationDTO == null) return null;
        return Location.builder()
                .uf(locationDTO.getUf())
                .city(locationDTO.getCity())
                .build();
    }
}
