package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.RatingPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.RatingRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.RatingResponseDTO;
import com.pointtils.pointtils.src.application.mapper.RatingResponseMapper;
import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Rating;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import com.pointtils.pointtils.src.core.domain.exceptions.RatingException;
import com.pointtils.pointtils.src.infrastructure.repositories.AppointmentRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.RatingRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final RatingResponseMapper ratingResponseMapper;

    public RatingResponseDTO createRating(RatingRequestDTO ratingRequestDTO) {
        Appointment appointment = appointmentRepository.findById(ratingRequestDTO.getAppointmentId())
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new RatingException("Agendamento não concluído");
        }

        userRepository.findById(appointment.getUser().getId())
            .orElseThrow(() -> new EntityNotFoundException("Usuário do agendamento não encontrado"));

        userRepository.findById(appointment.getInterpreter().getId())
            .orElseThrow(() -> new EntityNotFoundException("Intérprete do agendamento não encontrado"));

        Rating rating = new Rating();
        rating.setStars(ratingRequestDTO.getStars());
        rating.setDescription(ratingRequestDTO.getDescription());
        rating.setAppointment(appointment);

        ratingRepository.save(rating);
        updateInterpreterAverageRating(appointment.getInterpreter());

        return ratingResponseMapper.toResponseDTO(rating);
    }

    public List<RatingResponseDTO> getRatingsByInterpreterId(UUID interpreterId) {
        User interpreter = userRepository.findById(interpreterId)
                .orElseThrow(() -> new EntityNotFoundException("Intérprete não encontrado"));

        List<Rating> ratings = ratingRepository.findByInterpreterId(interpreter.getId());

        return ratings.stream()
                .map(ratingResponseMapper::toResponseDTO)
                .toList();
    }

    public RatingResponseDTO patchRating(RatingPatchRequestDTO request, UUID ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada"));

        rating.setStars(request.getStars());
        if (request.getDescription() != null) {
            rating.setDescription(request.getDescription());
        }

        Interpreter interpreter = rating.getAppointment().getInterpreter();

        if (interpreter == null) {
            throw new EntityNotFoundException("Intérprete não encontrado");
        }
        updateInterpreterAverageRating(rating.getAppointment().getInterpreter());

        ratingRepository.save(rating);
        return ratingResponseMapper.toResponseDTO(rating);
    }

    public void deleteRating(UUID ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada"));

        Appointment appointment = appointmentRepository.findById(rating.getAppointment().getId())
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));

        ratingRepository.delete(rating);
        updateInterpreterAverageRating(appointment.getInterpreter());
    }

    private void updateInterpreterAverageRating(Interpreter interpreter) {
        List<Rating> ratings = ratingRepository.findByInterpreterId(interpreter.getId());
        BigDecimal totalStars = ratings.stream()
                .map(Rating::getStars)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageStars;
        if (ratings.isEmpty()) {
            averageStars = BigDecimal.ZERO;
        } else {
            averageStars = totalStars.divide(BigDecimal.valueOf(ratings.size()), RoundingMode.HALF_UP);
        }
        interpreter.setRating(averageStars);
        userRepository.save(interpreter);
    }
}
