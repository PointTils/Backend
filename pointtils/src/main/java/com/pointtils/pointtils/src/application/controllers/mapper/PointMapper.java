package com.pointtils.pointtils.src.application.controllers.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.pointtils.pointtils.src.application.controllers.dto.PointRequestDTO;
import com.pointtils.pointtils.src.application.controllers.dto.PointResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Point;

@Component
public class PointMapper {

    public Point toEntity(PointRequestDTO dto) {
        return Point.builder()
                .userId(dto.getUserId())
                .description(dto.getDescription())
                .type(dto.getType())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public PointResponseDTO toResponseDTO(Point entity) {
        return PointResponseDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .description(entity.getDescription())
                .type(entity.getType())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
