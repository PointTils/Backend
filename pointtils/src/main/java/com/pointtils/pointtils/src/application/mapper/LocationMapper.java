package com.pointtils.pointtils.src.application.mapper;


import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.application.dto.requests.LocationRequestDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public LocationDTO toDto(Location location) {
        if (location == null) return null;
        return LocationDTO.builder()
                .id(location.getId())
                .uf(location.getUf())
                .city(location.getCity())
                .neighborhood(location.getNeighborhood())
                .build();
    }

    public Location toDomain(LocationRequestDTO locationRequestDTO, Interpreter interpreter) {
        if (locationRequestDTO == null) return null;
        return Location.builder()
                .uf(locationRequestDTO.getUf())
                .city(locationRequestDTO.getCity())
                .neighborhood(locationRequestDTO.getNeighborhood())
                .interpreter(interpreter)
                .build();
    }
}
