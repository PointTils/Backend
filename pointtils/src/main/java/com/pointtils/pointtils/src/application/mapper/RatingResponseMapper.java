package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.responses.RatingResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Rating;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingResponseMapper {
    private final AppointmentMapper appointmentMapper;

    public RatingResponseDTO toSingleResponseDTO(Rating rating) {
        return RatingResponseDTO.builder()
                .id(rating.getId())
                .stars(rating.getStars())
                .description(rating.getDescription())
                .appointment(appointmentMapper.toResponseDTO(rating.getAppointment()))
                .build();
    }

    public RatingResponseDTO toListResponseDTO(Rating rating) {
        return RatingResponseDTO.builder()
                .id(rating.getId())
                .stars(rating.getStars())
                .description(rating.getDescription())
                .date(rating.getAppointment().getDate().toString())
                .appointment(appointmentMapper.toResponseDTO(rating.getAppointment()))
                .build();
    }
}
