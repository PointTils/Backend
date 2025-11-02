package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.RatingPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.RatingRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.RatingResponseDTO;
import com.pointtils.pointtils.src.application.mapper.RatingResponseMapper;
import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.Enterprise;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.Rating;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import com.pointtils.pointtils.src.core.domain.exceptions.RatingException;
import com.pointtils.pointtils.src.infrastructure.repositories.AppointmentRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.RatingRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RatingResponseMapper ratingResponseMapper;

    @InjectMocks
    private RatingService ratingService;

    private UUID appointmentId;
    private UUID userId;
    private UUID interpreterId;
    private Appointment appointment;
    private Person user;
    private Rating rating;

    @BeforeEach
    @SuppressWarnings("unused")
    void setup() {
        appointmentId = UUID.randomUUID();
        userId = UUID.randomUUID();
        interpreterId = UUID.randomUUID();
        user = new Person();
        user.setId(userId);

        appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setUser(user);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setInterpreter(new Interpreter());

        rating = new Rating();
        rating.setId(UUID.randomUUID());
        rating.setStars(BigDecimal.valueOf(4));
        rating.setDescription("Muito bom!");
        rating.setAppointment(appointment);
    }

    @Test
    void createRating_shouldCreateAndReturnResponse() {
        RatingRequestDTO dto = new RatingRequestDTO();
        dto.setAppointmentId(appointmentId);
        dto.setStars(BigDecimal.valueOf(5));
        dto.setDescription("Ótimo!");

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        when(userRepository.findById(appointment.getUser().getId())).thenReturn(Optional.of(appointment.getUser()));
        when(userRepository.findById(appointment.getInterpreter().getId()))
                .thenReturn(Optional.of(appointment.getInterpreter()));

        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        when(ratingResponseMapper.toSingleResponseDTO(any())).thenReturn(mock(RatingResponseDTO.class));
        when(ratingRepository.findByInterpreterId(any())).thenReturn(List.of(rating));

        RatingResponseDTO response = ratingService.createRating(dto);

        assertNotNull(response);
        verify(ratingRepository).save(any(Rating.class));
        verify(ratingResponseMapper).toSingleResponseDTO(any());
    }

    @Test
    void createRating_shouldThrowIfAppointmentNotFound() {
        RatingRequestDTO dto = new RatingRequestDTO();
        dto.setAppointmentId(appointmentId);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> ratingService.createRating(dto));
        assertTrue(ex.getMessage().contains("Agendamento não encontrado"));
    }

    @Test
    void createRating_shouldThrowIfAppointmentPointsToNonExistingPerson() {
        RatingRequestDTO dto = new RatingRequestDTO();
        dto.setAppointmentId(appointmentId);

        Person person = Person.builder().id(userId).build();
        appointment.setUser(person);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> ratingService.createRating(dto));
        assertTrue(ex.getMessage().contains("Usuário do agendamento não encontrado"));
    }

    @Test
    void createRating_shouldThrowIfAppointmentPointsToNonExistingEnterprise() {
        RatingRequestDTO dto = new RatingRequestDTO();
        dto.setAppointmentId(appointmentId);

        Enterprise enterprise = Enterprise.builder().id(userId).build();
        appointment.setUser(enterprise);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> ratingService.createRating(dto));
        assertTrue(ex.getMessage().contains("Usuário do agendamento não encontrado"));
    }

    @Test
    void createRating_shouldThrowIfInterpreterNotFound() {
        RatingRequestDTO dto = new RatingRequestDTO();
        dto.setAppointmentId(appointmentId);

        Person person = Person.builder()
                .id(userId)
                .build();
        appointment.setUser(person);

        Interpreter interpreter = Interpreter.builder()
                .id(interpreterId)
                .build();
        appointment.setInterpreter(interpreter);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(userRepository.findById(userId)).thenReturn(Optional.of(person));
        when(userRepository.findById(interpreterId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> ratingService.createRating(dto));

        assertTrue(ex.getMessage().contains("Intérprete do agendamento não encontrado"));
    }

    @Test
    void createRating_shouldThrowIfAppointmentNotCompleted() {
        appointment.setStatus(AppointmentStatus.PENDING);
        RatingRequestDTO dto = new RatingRequestDTO();
        dto.setAppointmentId(appointmentId);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        RatingException ex = assertThrows(RatingException.class, () -> ratingService.createRating(dto));
        assertTrue(ex.getMessage().contains("Agendamento não concluído"));
    }

    @Test
    void getRatingsByInterpreterId_shouldReturnList() {
        UUID interpreterId = UUID.randomUUID();
        Interpreter interpreter = new Interpreter();
        interpreter.setId(interpreterId);

        when(userRepository.findById(interpreterId)).thenReturn(Optional.of(interpreter));
        when(ratingRepository.findByInterpreterId(interpreterId)).thenReturn(List.of(rating));
        when(ratingResponseMapper.toListResponseDTO(any())).thenReturn(mock(RatingResponseDTO.class));

        List<RatingResponseDTO> result = ratingService.getRatingsByInterpreterId(interpreterId);

        assertEquals(1, result.size());
        verify(ratingRepository).findByInterpreterId(interpreterId);
    }

    @Test
    void getRatingsByInterpreterId_shouldThrowIfInterpreterNotFound() {
        UUID interpreterId = UUID.randomUUID();
        when(userRepository.findById(interpreterId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> ratingService.getRatingsByInterpreterId(interpreterId));
        assertTrue(ex.getMessage().contains("Intérprete não encontrado"));
    }

    @Test
    void patchRating_shouldUpdateAndReturnResponse() {
        UUID ratingId = rating.getId();
        RatingPatchRequestDTO patchDTO = new RatingPatchRequestDTO();
        patchDTO.setStars(BigDecimal.valueOf(3));
        patchDTO.setDescription("Updated");

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));
        when(ratingRepository.save(any())).thenReturn(rating);
        when(ratingResponseMapper.toSingleResponseDTO(any())).thenReturn(mock(RatingResponseDTO.class));
        when(ratingRepository.findByInterpreterId(any())).thenReturn(List.of(rating));
        when(userRepository.save(any())).thenReturn(appointment.getInterpreter());

        RatingResponseDTO response = ratingService.patchRating(patchDTO, ratingId);

        assertNotNull(response);
        verify(ratingRepository).save(rating);
        assertEquals(patchDTO.getStars(), rating.getStars());
        assertEquals(patchDTO.getDescription(), rating.getDescription());
    }

    @Test
    void patchRating_shouldThrowIfRatingNotFound() {
        UUID ratingId = UUID.randomUUID();
        RatingPatchRequestDTO patchDTO = new RatingPatchRequestDTO();

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> ratingService.patchRating(patchDTO, ratingId));
        assertTrue(ex.getMessage().contains("Avaliação não encontrada"));
    }

    @Test
    void deleteRating_shouldDeleteAndUpdateInterpreterRating() {
        UUID ratingId = rating.getId();

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(ratingRepository.findByInterpreterId(any())).thenReturn(List.of());
        when(userRepository.save(any())).thenReturn(appointment.getInterpreter());

        ratingService.deleteRating(ratingId);

        verify(ratingRepository).delete(rating);
        verify(userRepository).save(appointment.getInterpreter());
    }

    @Test
    void deleteRating_shouldThrowIfRatingNotFound() {
        UUID ratingId = UUID.randomUUID();
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> ratingService.deleteRating(ratingId));
        assertTrue(ex.getMessage().contains("Avaliação não encontrada"));
    }

    @Test
    void deleteRating_shouldThrowIfAppointmentNotFound() {
        UUID ratingId = rating.getId();
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> ratingService.deleteRating(ratingId));
        assertTrue(ex.getMessage().contains("Agendamento não encontrado"));
    }

    @Test
    void patchRating_deveLancarEntityNotFoundException_quandoInterpreterForNull() {
        UUID ratingId = UUID.randomUUID();
        RatingPatchRequestDTO request = new RatingPatchRequestDTO();
        request.setStars(BigDecimal.valueOf(5));

        Rating rating = new Rating();
        Appointment appointment = new Appointment();
        appointment.setInterpreter(null);
        rating.setAppointment(appointment);

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ratingService.patchRating(request, ratingId);
        });

        assert (exception.getMessage().equals("Intérprete não encontrado"));
    }
}
