package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LocationMapperTest {

    @Test
    void shouldMapLocationToDto() {
        Location location = Location.builder()
                .id(UUID.randomUUID())
                .uf("SP")
                .city("São Paulo")
                .build();
        LocationDTO locationDTO = LocationMapper.toDto(location);

        assertNotNull(locationDTO);
        assertEquals(location.getUf(), locationDTO.getUf());
        assertEquals(location.getCity(), locationDTO.getCity());
    }

    @Test
    void shouldMapNullLocationToNullDto() {
        assertNull(LocationMapper.toDto(null));
    }

    @Test
    void shouldMapDtoToLocation() {
        LocationDTO dto = LocationDTO.builder()
                .id(UUID.randomUUID())
                .uf("SP")
                .city("São Paulo")
                .build();
        Location location = LocationMapper.toDomain(dto);

        assertNotNull(location);
        assertEquals(dto.getUf(), location.getUf());
        assertEquals(dto.getCity(), location.getCity());
    }

    @Test
    void shouldMapNullDtoToNullLocation() {
        assertNull(LocationMapper.toDomain(null));
    }
}
