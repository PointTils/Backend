package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.application.dto.requests.LocationRequestDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LocationMapperTest {

    @Test
    void shouldMapLocationToDto() {
        Interpreter interpreter = new Interpreter();
        Location location = Location.builder()
                .id(UUID.randomUUID())
                .uf("SP")
                .city("São Paulo")
                .neighborhood("Higienópolis")
                .interpreter(interpreter)
                .build();

        LocationDTO actualDTO = LocationMapper.toDto(location);
        assertNotNull(actualDTO);
        assertEquals("SP", actualDTO.getUf());
        assertEquals("São Paulo", actualDTO.getCity());
        assertEquals("Higienópolis", actualDTO.getNeighborhood());
    }

    @Test
    void shouldMapNullLocationToNullDto() {
        assertNull(LocationMapper.toDto(null));
    }

    @Test
    void shouldMapRequestDtoToLocation() {
        Interpreter interpreter = new Interpreter();
        LocationRequestDTO dto = new LocationRequestDTO();
        dto.setUf("SP");
        dto.setCity("São Paulo");
        dto.setNeighborhood("Higienópolis");

        Location location = LocationMapper.toDomain(dto, interpreter);
        assertNotNull(location);
        assertEquals("SP", location.getUf());
        assertEquals("São Paulo", location.getCity());
        assertEquals("Higienópolis", location.getNeighborhood());
        assertEquals(interpreter, location.getInterpreter());
    }

    @Test
    void shouldMapNullDtoToNullLocation() {
        Interpreter interpreter = new Interpreter();
        assertNull(LocationMapper.toDomain(null, interpreter));
    }
}
