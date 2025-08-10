package com.pointtils.pointtils.src.application.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointtils.pointtils.src.application.controllers.dto.PointRequestDTO;
import com.pointtils.pointtils.src.application.controllers.dto.PointResponseDTO;
import com.pointtils.pointtils.src.application.controllers.mapper.PointMapper;
import com.pointtils.pointtils.src.core.domain.entities.Point;
import com.pointtils.pointtils.src.infrastructure.repositories.PointRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointMapper pointMapper;

    @Transactional(readOnly = true)
    public List<PointResponseDTO> findAll() {
        return pointRepository.findAll().stream()
                .map(pointMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PointResponseDTO findById(Long id) {
        return pointRepository.findById(id)
                .map(pointMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Point not found with id: " + id));
    }

    @Transactional
    public PointResponseDTO create(PointRequestDTO requestDTO) {
        Point point = pointMapper.toEntity(requestDTO);
        Point savedPoint = pointRepository.save(point);
        return pointMapper.toResponseDTO(savedPoint);
    }

    @Transactional
    public PointResponseDTO update(Long id, PointRequestDTO requestDTO) {
        if (!pointRepository.existsById(id)) {
            throw new EntityNotFoundException("Point not found with id: " + id);
        }
        
        Point point = pointMapper.toEntity(requestDTO);
        point.setId(id);
        Point updatedPoint = pointRepository.save(point);
        
        return pointMapper.toResponseDTO(updatedPoint);
    }

    @Transactional
    public void delete(Long id) {
        if (!pointRepository.existsById(id)) {
            throw new EntityNotFoundException("Point not found with id: " + id);
        }
        pointRepository.deleteById(id);
    }
}
