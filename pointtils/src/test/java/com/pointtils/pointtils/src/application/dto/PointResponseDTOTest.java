package com.pointtils.pointtils.src.application.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PointResponseDTOTest {

    @Test
    void shouldCreatePointResponseDTOWithAllArgsConstructor() {
        // Given
        Long id = 1L;
        String userId = "user123";
        String description = "Test description";
        LocalDateTime timestamp = LocalDateTime.now();
        String type = "ENTRY";

        // When
        PointResponseDTO dto = new PointResponseDTO(id, userId, description, timestamp, type);

        // Then
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(description, dto.getDescription());
        assertEquals(timestamp, dto.getTimestamp());
        assertEquals(type, dto.getType());
    }

    @Test
    void shouldCreatePointResponseDTOWithNoArgsConstructor() {
        // When
        PointResponseDTO dto = new PointResponseDTO();

        // Then
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getUserId());
        assertNull(dto.getDescription());
        assertNull(dto.getTimestamp());
        assertNull(dto.getType());
    }

    @Test
    void shouldCreatePointResponseDTOWithBuilder() {
        // Given
        Long id = 1L;
        String userId = "user123";
        String description = "Test description";
        LocalDateTime timestamp = LocalDateTime.now();
        String type = "ENTRY";

        // When
        PointResponseDTO dto = PointResponseDTO.builder()
                .id(id)
                .userId(userId)
                .description(description)
                .timestamp(timestamp)
                .type(type)
                .build();

        // Then
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(description, dto.getDescription());
        assertEquals(timestamp, dto.getTimestamp());
        assertEquals(type, dto.getType());
    }

    @Test
    void shouldSetAndGetProperties() {
        // Given
        PointResponseDTO dto = new PointResponseDTO();
        Long id = 1L;
        String userId = "user123";
        String description = "Test description";
        LocalDateTime timestamp = LocalDateTime.now();
        String type = "ENTRY";

        // When
        dto.setId(id);
        dto.setUserId(userId);
        dto.setDescription(description);
        dto.setTimestamp(timestamp);
        dto.setType(type);

        // Then
        assertEquals(id, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(description, dto.getDescription());
        assertEquals(timestamp, dto.getTimestamp());
        assertEquals(type, dto.getType());
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        PointResponseDTO dto = new PointResponseDTO(null, null, null, null, null);

        // Then
        assertNull(dto.getId());
        assertNull(dto.getUserId());
        assertNull(dto.getDescription());
        assertNull(dto.getTimestamp());
        assertNull(dto.getType());
    }

    @Test
    void shouldHandleEmptyStrings() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        PointResponseDTO dto = new PointResponseDTO(1L, "", "", timestamp, "");

        // Then
        assertEquals(1L, dto.getId());
        assertEquals("", dto.getUserId());
        assertEquals("", dto.getDescription());
        assertEquals(timestamp, dto.getTimestamp());
        assertEquals("", dto.getType());
    }

    @Test
    void shouldGenerateToString() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        PointResponseDTO dto = new PointResponseDTO(1L, "user123", "Test description", timestamp, "ENTRY");

        // When
        String toString = dto.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("user123"));
        assertTrue(toString.contains("Test description"));
        assertTrue(toString.contains("ENTRY"));
    }

    @Test
    void shouldGenerateHashCode() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        PointResponseDTO dto1 = new PointResponseDTO(1L, "user123", "Test description", timestamp, "ENTRY");
        PointResponseDTO dto2 = new PointResponseDTO(1L, "user123", "Test description", timestamp, "ENTRY");

        // When
        int hashCode1 = dto1.hashCode();
        int hashCode2 = dto2.hashCode();

        // Then
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void shouldBeEqualWithSameValues() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        PointResponseDTO dto1 = new PointResponseDTO(1L, "user123", "Test description", timestamp, "ENTRY");
        PointResponseDTO dto2 = new PointResponseDTO(1L, "user123", "Test description", timestamp, "ENTRY");

        // Then
        assertEquals(dto1, dto2);
    }

    @Test
    void shouldNotBeEqualWithDifferentValues() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        PointResponseDTO dto1 = new PointResponseDTO(1L, "user123", "Test description", timestamp, "ENTRY");
        PointResponseDTO dto2 = new PointResponseDTO(2L, "user456", "Different description", timestamp, "EXIT");

        // Then
        assertNotEquals(dto1, dto2);
    }

    @Test
    void shouldNotBeEqualWithDifferentTimestamps() {
        // Given
        LocalDateTime timestamp1 = LocalDateTime.now();
        LocalDateTime timestamp2 = timestamp1.plusMinutes(1);
        PointResponseDTO dto1 = new PointResponseDTO(1L, "user123", "Test description", timestamp1, "ENTRY");
        PointResponseDTO dto2 = new PointResponseDTO(1L, "user123", "Test description", timestamp2, "ENTRY");

        // Then
        assertNotEquals(dto1, dto2);
    }

    @Test
    void shouldNotBeEqualWithNull() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        PointResponseDTO dto = new PointResponseDTO(1L, "user123", "Test description", timestamp, "ENTRY");

        // Then
        assertNotEquals(null, dto);
    }

    @Test
    void shouldNotBeEqualWithDifferentClass() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        PointResponseDTO dto = new PointResponseDTO(1L, "user123", "Test description", timestamp, "ENTRY");
        String differentObject = "not a dto";

        // Then
        assertNotEquals(dto, differentObject);
    }

    @Test
    void shouldHandleNullIdInEquality() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        PointResponseDTO dto1 = new PointResponseDTO(null, "user123", "Test description", timestamp, "ENTRY");
        PointResponseDTO dto2 = new PointResponseDTO(null, "user123", "Test description", timestamp, "ENTRY");

        // Then
        assertEquals(dto1, dto2);
    }

    @Test
    void shouldHandleMixedNullValuesInEquality() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        PointResponseDTO dto1 = new PointResponseDTO(1L, null, "Test description", timestamp, "ENTRY");
        PointResponseDTO dto2 = new PointResponseDTO(1L, null, "Test description", timestamp, "ENTRY");

        // Then
        assertEquals(dto1, dto2);
    }
}
