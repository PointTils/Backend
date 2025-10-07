package com.pointtils.pointtils.src.application.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pointtils.pointtils.src.application.dto.requests.RatingPatchRequestDTO;
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

    public RatingResponseDTO createRating(RatingRequestDTO ratingRequestDTO, UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
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

        return ratingResponseMapper.toSingleResponseDTO(rating, user);
    }

    public List<RatingResponseDTO> getRatingsByInterpreterId(UUID interpreterId) {
        User interpreter = userRepository.findById(interpreterId)
                .orElseThrow(() -> new RatingException("Intérprete não encontrado"));

        List<Rating> ratings = ratingRepository.findByInterpreterId(interpreter.getId());

        return ratings.stream()
                .map(rating -> ratingResponseMapper.toListResponseDTO(rating, userRepository.findById(rating.getUserId()).orElse(null)))
                .toList();
    }

    public RatingResponseDTO patchRating(RatingPatchRequestDTO request, UUID ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingException("Avaliação não encontrada"));
        
        rating.setStars(request.getStars());
        if(request.getDescription() != null) {
            rating.setDescription(request.getDescription());
        }
        
        updateInterpreterAverageRating(appointmentRepository.findById(rating.getAppointment().getId())
                .orElseThrow(() -> new RatingException("Agendamento não encontrado")));

        ratingRepository.save(rating);
        return ratingResponseMapper.toSingleResponseDTO(rating, userRepository.findById(rating.getUserId()).orElse(null));
    }

    public void deleteRating(UUID ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingException("Avaliação não encontrada"));
        
        Appointment appointment = appointmentRepository.findById(rating.getAppointment().getId())
                .orElseThrow(() -> new RatingException("Agendamento não encontrado"));

        ratingRepository.delete(rating);
        updateInterpreterAverageRating(appointment);
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
