package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.responses.RatingResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.RatingUserResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Rating;
import com.pointtils.pointtils.src.core.domain.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingResponseMapper {

    public RatingResponseDTO toSingleResponseDTO(Rating rating) {
        return RatingResponseDTO.builder()
                .id(rating.getId())
                .stars(rating.getStars())
                .description(rating.getDescription())
                .date(rating.getCreatedAt().toString())
                .user(toUserResponseDTO(rating.getAppointment().getUser()))
                .build();
    }

    public RatingResponseDTO toListResponseDTO(Rating rating) {
        return RatingResponseDTO.builder()
                .id(rating.getId())
                .stars(rating.getStars())
                .description(rating.getDescription())
                .date(rating.getCreatedAt().toString())
                .user(toUserResponseDTO(rating.getAppointment().getUser()))
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
