package com.pointtils.pointtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.pointtils.pointtils.src.application.controllers.dto.PointRequestDTO;
import com.pointtils.pointtils.src.application.controllers.dto.PointResponseDTO;
import com.pointtils.pointtils.src.application.controllers.mapper.PointMapper;
import com.pointtils.pointtils.src.application.services.PointService;
import com.pointtils.pointtils.src.core.domain.entities.Point;
import com.pointtils.pointtils.src.core.domain.entities.PointType;
import com.pointtils.pointtils.src.infrastructure.repositories.PointRepository;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointMapper pointMapper;

    @InjectMocks
    private PointService pointService;

    private Point point;
    private PointRequestDTO requestDTO;
    private PointResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Configurar objetos de teste
        point = new Point();
        point.setId(1L);
        point.setUserId("user123");
        point.setDescription("Morning entry");
        point.setType(PointType.ENTRY);
        point.setTimestamp(LocalDateTime.now());

        requestDTO = new PointRequestDTO();
        requestDTO.setUserId("user123");
        requestDTO.setDescription("Morning entry");
        requestDTO.setType(PointType.ENTRY);

        responseDTO = new PointResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setUserId("user123");
        responseDTO.setDescription("Morning entry");
        responseDTO.setType(PointType.ENTRY);
        responseDTO.setTimestamp(point.getTimestamp());
    }

    @Test
    void findAllShouldReturnListOfPoints() {
        // Given
        when(pointRepository.findAll()).thenReturn(Arrays.asList(point));
        when(pointMapper.toResponseDTO(point)).thenReturn(responseDTO);

        // When
        List<PointResponseDTO> result = pointService.findAll();

        // Then
        assertEquals(1, result.size());
        assertEquals(responseDTO, result.get(0));
    }

    @Test
    void findByIdShouldReturnPoint() {
        // Given
        when(pointRepository.findById(1L)).thenReturn(Optional.of(point));
        when(pointMapper.toResponseDTO(point)).thenReturn(responseDTO);

        // When
        PointResponseDTO result = pointService.findById(1L);

        // Then
        assertEquals(responseDTO, result);
    }

    @Test
    void findByIdShouldThrowExceptionWhenPointNotFound() {
        // Given
        when(pointRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> pointService.findById(999L));
    }

    @Test
    void createShouldReturnCreatedPoint() {
        // Given
        when(pointMapper.toEntity(requestDTO)).thenReturn(point);
        when(pointRepository.save(point)).thenReturn(point);
        when(pointMapper.toResponseDTO(point)).thenReturn(responseDTO);

        // When
        PointResponseDTO result = pointService.create(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(pointRepository, times(1)).save(point);
    }

    @Test
    void updateShouldReturnUpdatedPoint() {
        // Given
        when(pointRepository.existsById(1L)).thenReturn(true);
        when(pointMapper.toEntity(requestDTO)).thenReturn(point);
        when(pointRepository.save(any(Point.class))).thenReturn(point);
        when(pointMapper.toResponseDTO(point)).thenReturn(responseDTO);

        // When
        PointResponseDTO result = pointService.update(1L, requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(pointRepository, times(1)).save(point);
    }

    @Test
    void updateShouldThrowExceptionWhenPointNotFound() {
        // Given
        when(pointRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> pointService.update(999L, requestDTO));
    }

    @Test
    void deleteShouldCallRepositoryDeleteById() {
        // Given
        when(pointRepository.existsById(1L)).thenReturn(true);

        // When
        pointService.delete(1L);

        // Then
        verify(pointRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteShouldThrowExceptionWhenPointNotFound() {
        // Given
        when(pointRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> pointService.delete(999L));
    }
}
