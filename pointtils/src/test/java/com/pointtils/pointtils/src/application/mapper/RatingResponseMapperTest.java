package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.responses.RatingResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.Rating;
import com.pointtils.pointtils.src.core.domain.entities.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RatingResponseMapperTest {

    private final RatingResponseMapper ratingResponseMapper = new RatingResponseMapper();

    @Test
    void shouldMapRatingToResponseDto() {
        User user = new User() {
            @Override
            public UUID getId() {
                return UUID.fromString("6f37c5d6-ea31-4969-bd3e-15d5a1e39d1e");
            }

            @Override
            public String getDisplayName() {
                return "Nome Mock";
            }

            @Override
            public String getDocument() {
                return "11122233344";
            }

            @Override
            public String getPicture() {
                return "picture_url";
            }
        };

        Appointment appointment = new Appointment();
        appointment.setUser(user);

        LocalDateTime ratingDate = LocalDateTime.of(2025, 10, 31, 9, 30, 50);
        UUID ratingId = UUID.fromString("24ccf183-22af-45e5-b06c-c6c78121839c");

        Rating rating = new Rating(ratingId, BigDecimal.valueOf(4.5), "Intérprete competente", appointment, ratingDate, ratingDate);

        RatingResponseDTO actualResponse = ratingResponseMapper.toResponseDTO(rating);
        assertEquals(ratingId, actualResponse.getId());
        assertEquals(BigDecimal.valueOf(4.5), actualResponse.getStars());
        assertEquals("Intérprete competente", actualResponse.getDescription());
        assertEquals("2025-10-31T09:30:50", actualResponse.getDate());
        assertNotNull(actualResponse.getUser());
        assertEquals(user.getId(), actualResponse.getUser().getId());
        assertEquals("Nome Mock", actualResponse.getUser().getName());
        assertEquals("picture_url", actualResponse.getUser().getPicture());
    }
}
