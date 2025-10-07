package com.pointtils.pointtils.src.application.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pointtils.pointtils.src.application.dto.requests.RatingRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.RatingResponseDTO;
import com.pointtils.pointtils.src.application.mapper.RatingResponseMapper;
import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.Rating;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import com.pointtils.pointtils.src.core.domain.exceptions.RatingException;
import com.pointtils.pointtils.src.infrastructure.repositories.AppointmentRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.RatingRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final RatingResponseMapper ratingResponseMapper;

    public RatingResponseDTO createRating(RatingRequestDTO ratingRequestDTO) {
        Appointment appointment = appointmentRepository.findById(ratingRequestDTO.getAppointmentId())
                .orElseThrow(() -> new RatingException("Agendamento ou usuário não encontrado"));

        User user = userRepository.findById(ratingRequestDTO.getUserId())
                .orElseThrow(() -> new RatingException("Agendamento ou usuário não encontrado"));

        
        if (!appointment.getUser().getId().equals(user.getId())) {
            throw new RatingException("Parâmetros de entrada inválidos");
        }

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new RatingException("Agendamento ainda não foi concluído (só posso avaliar depois de status ser encerrado)");
        }

        Rating rating = new Rating();
        rating.setStars(ratingRequestDTO.getStars());
        rating.setDescription(ratingRequestDTO.getDescription());
        rating.setAppointment(appointment);
        rating.setUserId(user.getId());

        ratingRepository.save(rating);
        updateInterpreterAverageRating(appointment);

        return ratingResponseMapper.toResponseDTO(rating, user);
    }

    private void updateInterpreterAverageRating(Appointment appointment) {
        List<Rating> ratings = ratingRepository.findByAppointment(appointment);
        BigDecimal totalStars = ratings.stream()
                .map(Rating::getStars)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageStars;
        if (ratings.isEmpty()) {
            averageStars = BigDecimal.ZERO;
        } else {
            averageStars = totalStars.divide(BigDecimal.valueOf(ratings.size()), BigDecimal.ROUND_HALF_UP);
        }
        var interpreter = appointment.getInterpreter();
        interpreter.setRating(averageStars);
        userRepository.save(interpreter);
    }
}
