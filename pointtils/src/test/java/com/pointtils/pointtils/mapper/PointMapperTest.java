package com.pointtils.pointtils.mapper;

import com.pointtils.pointtils.src.application.dto.PointRequestDTO;
import com.pointtils.pointtils.src.application.dto.PointResponseDTO;
import com.pointtils.pointtils.src.application.mapper.PointMapper;
import com.pointtils.pointtils.src.core.domain.entities.Point;
import com.pointtils.pointtils.src.core.domain.entities.PointType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PointMapperTest {

    private PointMapper pointMapper;

    @BeforeEach
    void setUp() {
        pointMapper = new PointMapper();
    }

    @Test
    void shouldMapPointRequestDTOToPoint() {
        // Given
        PointRequestDTO requestDTO = new PointRequestDTO("user123", "Test description", "ENTRY");

        // When
        Point point = pointMapper.toEntity(requestDTO);

        // Then
        assertNotNull(point);
        assertEquals("user123", point.getUserId());
        assertEquals("Test description", point.getDescription());
        assertEquals(PointType.ENTRY, point.getType());
        assertNotNull(point.getTimestamp());
    }

    @Test
    void shouldMapPointRequestDTOToPointWithExitType() {
        // Given
        PointRequestDTO requestDTO = new PointRequestDTO("user123", "Test description", "EXIT");

        // When
        Point point = pointMapper.toEntity(requestDTO);

        // Then
        assertNotNull(point);
        assertEquals("user123", point.getUserId());
        assertEquals("Test description", point.getDescription());
        assertEquals(PointType.EXIT, point.getType());
        assertNotNull(point.getTimestamp());
    }

    @Test
    void shouldMapPointRequestDTOToPointWithNullValues() {
        // Given
        PointRequestDTO requestDTO = new PointRequestDTO(null, null, null);

        // When
        Point point = pointMapper.toEntity(requestDTO);

        // Then
        assertNotNull(point);
        assertNull(point.getUserId());
        assertNull(point.getDescription());
        assertNull(point.getType());
        assertNotNull(point.getTimestamp());
    }

    @Test
    void shouldMapPointRequestDTOToPointWithEmptyStrings() {
        // Given
        PointRequestDTO requestDTO = new PointRequestDTO("", "", "ENTRY");

        // When
        Point point = pointMapper.toEntity(requestDTO);

        // Then
        assertNotNull(point);
        assertEquals("", point.getUserId());
        assertEquals("", point.getDescription());
        assertEquals(PointType.ENTRY, point.getType());
        assertNotNull(point.getTimestamp());
    }

    @Test
    void shouldMapPointToPointResponseDTO() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        Point point = new Point();
        point.setId(1L);
        point.setUserId("user123");
        point.setDescription("Test description");
        point.setTimestamp(timestamp);
        point.setType(PointType.ENTRY);

        // When
        PointResponseDTO responseDTO = pointMapper.toResponseDTO(point);

        // Then
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("user123", responseDTO.getUserId());
        assertEquals("Test description", responseDTO.getDescription());
        assertEquals(timestamp, responseDTO.getTimestamp());
        assertEquals("ENTRY", responseDTO.getType());
    }

    @Test
    void shouldMapPointToPointResponseDTOWithExitType() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        Point point = new Point();
        point.setId(1L);
        point.setUserId("user123");
        point.setDescription("Test description");
        point.setTimestamp(timestamp);
        point.setType(PointType.EXIT);

        // When
        PointResponseDTO responseDTO = pointMapper.toResponseDTO(point);

        // Then
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("user123", responseDTO.getUserId());
        assertEquals("Test description", responseDTO.getDescription());
        assertEquals(timestamp, responseDTO.getTimestamp());
        assertEquals("EXIT", responseDTO.getType());
    }

    @Test
    void shouldMapPointToPointResponseDTOWithNullValues() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        Point point = new Point();
        point.setId(null);
        point.setUserId(null);
        point.setDescription(null);
        point.setTimestamp(timestamp);
        point.setType(null);

        // When
        PointResponseDTO responseDTO = pointMapper.toResponseDTO(point);

        // Then
        assertNotNull(responseDTO);
        assertNull(responseDTO.getId());
        assertNull(responseDTO.getUserId());
        assertNull(responseDTO.getDescription());
        assertEquals(timestamp, responseDTO.getTimestamp());
        assertNull(responseDTO.getType());
    }

    @Test
    void shouldHandleNullValuesInPointRequestDTO() {
        // Given
        PointRequestDTO requestDTO = new PointRequestDTO(null, null, null);

        // When
        Point point = pointMapper.toEntity(requestDTO);

        // Then
        assertNotNull(point);
        assertNull(point.getUserId());
        assertNull(point.getDescription());
        assertNull(point.getType());
        assertNotNull(point.getTimestamp());
    }

    @Test
    void shouldHandleEmptyStringsInPointRequestDTO() {
        // Given
        PointRequestDTO requestDTO = new PointRequestDTO("", "", "ENTRY");

        // When
        Point point = pointMapper.toEntity(requestDTO);

        // Then
        assertNotNull(point);
        assertEquals("", point.getUserId());
        assertEquals("", point.getDescription());
        assertEquals(PointType.ENTRY, point.getType());
        assertNotNull(point.getTimestamp());
    }

    @Test
    void shouldHandleNullValuesInPoint() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        Point point = new Point();
        point.setId(null);
        point.setUserId(null);
        point.setDescription(null);
        point.setTimestamp(timestamp);
        point.setType(null);

        // When
        PointResponseDTO responseDTO = pointMapper.toResponseDTO(point);

        // Then
        assertNotNull(responseDTO);
        assertNull(responseDTO.getId());
        assertNull(responseDTO.getUserId());
        assertNull(responseDTO.getDescription());
        assertEquals(timestamp, responseDTO.getTimestamp());
        assertNull(responseDTO.getType());
    }
}
