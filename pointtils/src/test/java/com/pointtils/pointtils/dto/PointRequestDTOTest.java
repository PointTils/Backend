package com.pointtils.pointtils.dto;

import com.pointtils.pointtils.src.application.dto.PointRequestDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointRequestDTOTest {

    @Test
    void shouldCreatePointRequestDTOWithAllArgsConstructor() {
        // Given
        String userId = "user123";
        String description = "Test description";
        String type = "ENTRY";

        // When
        PointRequestDTO dto = new PointRequestDTO(userId, description, type);

        // Then
        assertNotNull(dto);
        assertEquals(userId, dto.getUserId());
        assertEquals(description, dto.getDescription());
        assertEquals(type, dto.getType());
    }

    @Test
    void shouldCreatePointRequestDTOWithNoArgsConstructor() {
        // When
        PointRequestDTO dto = new PointRequestDTO();

        // Then
        assertNotNull(dto);
        assertNull(dto.getUserId());
        assertNull(dto.getDescription());
        assertNull(dto.getType());
    }

    @Test
    void shouldCreatePointRequestDTOWithBuilder() {
        // Given
        String userId = "user123";
        String description = "Test description";
        String type = "ENTRY";

        // When
        PointRequestDTO dto = PointRequestDTO.builder()
                .userId(userId)
                .description(description)
                .type(type)
                .build();

        // Then
        assertNotNull(dto);
        assertEquals(userId, dto.getUserId());
        assertEquals(description, dto.getDescription());
        assertEquals(type, dto.getType());
    }

    @Test
    void shouldSetAndGetProperties() {
        // Given
        PointRequestDTO dto = new PointRequestDTO();
        String userId = "user123";
        String description = "Test description";
        String type = "ENTRY";

        // When
        dto.setUserId(userId);
        dto.setDescription(description);
        dto.setType(type);

        // Then
        assertEquals(userId, dto.getUserId());
        assertEquals(description, dto.getDescription());
        assertEquals(type, dto.getType());
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        PointRequestDTO dto = new PointRequestDTO(null, null, null);

        // Then
        assertNull(dto.getUserId());
        assertNull(dto.getDescription());
        assertNull(dto.getType());
    }

    @Test
    void shouldHandleEmptyStrings() {
        // Given
        PointRequestDTO dto = new PointRequestDTO("", "", "");

        // Then
        assertEquals("", dto.getUserId());
        assertEquals("", dto.getDescription());
        assertEquals("", dto.getType());
    }

    @Test
    void shouldGenerateToString() {
        // Given
        PointRequestDTO dto = new PointRequestDTO("user123", "Test description", "ENTRY");

        // When
        String toString = dto.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("user123"));
        assertTrue(toString.contains("Test description"));
        assertTrue(toString.contains("ENTRY"));
    }

    @Test
    void shouldGenerateHashCode() {
        // Given
        PointRequestDTO dto1 = new PointRequestDTO("user123", "Test description", "ENTRY");
        PointRequestDTO dto2 = new PointRequestDTO("user123", "Test description", "ENTRY");

        // When
        int hashCode1 = dto1.hashCode();
        int hashCode2 = dto2.hashCode();

        // Then
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void shouldBeEqualWithSameValues() {
        // Given
        PointRequestDTO dto1 = new PointRequestDTO("user123", "Test description", "ENTRY");
        PointRequestDTO dto2 = new PointRequestDTO("user123", "Test description", "ENTRY");

        // Then
        assertEquals(dto1, dto2);
    }

    @Test
    void shouldNotBeEqualWithDifferentValues() {
        // Given
        PointRequestDTO dto1 = new PointRequestDTO("user123", "Test description", "ENTRY");
        PointRequestDTO dto2 = new PointRequestDTO("user456", "Different description", "EXIT");

        // Then
        assertNotEquals(dto1, dto2);
    }

    @Test
    void shouldNotBeEqualWithNull() {
        // Given
        PointRequestDTO dto = new PointRequestDTO("user123", "Test description", "ENTRY");

        // Then
        assertNotEquals(null, dto);
    }

    @Test
    void shouldNotBeEqualWithDifferentClass() {
        // Given
        PointRequestDTO dto = new PointRequestDTO("user123", "Test description", "ENTRY");
        String differentObject = "not a dto";

        // Then
        assertNotEquals(dto, differentObject);
    }
}
