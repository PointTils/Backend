package com.pointtils.pointtils.src.application.mapper;

import org.springframework.stereotype.Component;

import com.pointtils.pointtils.src.application.dto.responses.RatingResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.RatingUserResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Rating;
import com.pointtils.pointtils.src.core.domain.entities.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RatingResponseMapper {
    
    public RatingResponseDTO toSingleResponseDTO(Rating rating, User user) {
        return RatingResponseDTO.builder()
            .id(rating.getId())
            .stars(rating.getStars())
            .description(rating.getDescription())
            .appointmentId(rating.getAppointment().getId())
            .user(toUserResponseDTO(user))
            .build();
    }

    public RatingResponseDTO toListResponseDTO(Rating rating, User user) {
        return RatingResponseDTO.builder()
            .id(rating.getId())
            .stars(rating.getStars())
            .description(rating.getDescription())
            .date(rating.getAppointment().getDate().toString())
            .user(toUserResponseDTO(user))
            .build();
    }

    private RatingUserResponseDTO toUserResponseDTO(User user) {
        return RatingUserResponseDTO.builder()
            .id(user.getId())
            .name(user.getDisplayName())
            .picture(user.getPicture())
            .build();
    }
}
