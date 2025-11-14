package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.AppointmentPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.AppointmentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentFilterResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentResponseDTO;
import com.pointtils.pointtils.src.application.mapper.AppointmentMapper;
import com.pointtils.pointtils.src.application.mapper.UserSpecialtyMapper;
import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.NotificationType;
import com.pointtils.pointtils.src.infrastructure.repositories.AppointmentRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.RatingRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AppointmentService")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private InterpreterRepository interpreterRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private EmailService emailService;
    @Mock
    private NotificationService notificationService;

    @Spy
    private AppointmentMapper appointmentMapper = new AppointmentMapper(new UserSpecialtyMapper());

    @InjectMocks
    private AppointmentService appointmentService;

    private User mockUser;
    private Interpreter mockInterpreter;
    private Appointment mockAppointment;
    private AppointmentRequestDTO appointmentRequestDTO;
    private UUID appointmentId;
    private UUID userId;
    private UUID interpreterId;

    @BeforeEach
    void setUp() {
        appointmentId = UUID.randomUUID();
        userId = UUID.fromString("8ac9c3ef-e7fa-42df-bb37-febc138c3090");
        interpreterId = UUID.fromString("1eb35247-bbb6-4564-bc50-bb16ad0901bd");

        mockUser = new Person() {
            @Override
            public String getDisplayName() {
                return "Test User";
            }
        };
        mockUser.setId(userId);
        mockUser.setEmail("user@email.com");
        ((Person) mockUser).setName("Test User");

        mockInterpreter = Interpreter.builder()
                .id(interpreterId)
                .email("interpreter@email.com")
                .name("Interpreter Test")
                .build();

        mockAppointment = Appointment.builder()
                .id(appointmentId)
                .uf("SP")
                .city("São Paulo")
                .modality(AppointmentModality.ONLINE)
                .date(LocalDate.now().plusDays(1))
                .description("Consulta teste")
                .status(AppointmentStatus.PENDING)
                .interpreter(mockInterpreter)
                .user(mockUser)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(15, 0))
                .build();

        appointmentRequestDTO = AppointmentRequestDTO.builder()
                .uf("SP")
                .city("São Paulo")
                .modality(AppointmentModality.ONLINE)
                .date(LocalDate.now().plusDays(1))
                .description("Consulta teste")
                .interpreterId(interpreterId)
                .userId(userId)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(15, 0))
                .build();
    }

    @Test
    @DisplayName("Deve criar appointment com sucesso")
    void shouldCreateAppointmentSuccessfully() {
        when(interpreterRepository.findById(interpreterId)).thenReturn(Optional.of(mockInterpreter));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);

        AppointmentResponseDTO result = appointmentService.createAppointment(appointmentRequestDTO);

        assertNotNull(result);
        assertEquals(appointmentId, result.getId());
        assertEquals("SP", result.getUf());
        assertEquals("São Paulo", result.getCity());

        verify(interpreterRepository).findById(interpreterId);
        verify(userRepository).findById(userId);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(notificationService).sendNotificationToUser(interpreterId, NotificationType.APPOINTMENT_REQUESTED);
    }

    @Test
    @DisplayName("Deve criar appointment com status PENDING por padrão")
    void shouldCreateAppointmentWithDefaultPendingStatus() {
        when(interpreterRepository.findById(interpreterId)).thenReturn(Optional.of(mockInterpreter));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);

        AppointmentResponseDTO result = appointmentService.createAppointment(appointmentRequestDTO);

        assertNotNull(result);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(notificationService).sendNotificationToUser(interpreterId, NotificationType.APPOINTMENT_REQUESTED);
    }

    @Test
    @DisplayName("Deve lançar exceção quando intérprete não encontrado")
    void shouldThrowExceptionWhenInterpreterNotFound() {
        when(interpreterRepository.findById(interpreterId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> appointmentService.createAppointment(appointmentRequestDTO));

        assertEquals("Intérprete não encontrado com o id: " + interpreterId, exception.getMessage());
        verify(interpreterRepository).findById(interpreterId);
        verify(userRepository, never()).findById(any());
        verify(appointmentRepository, never()).save(any());
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void shouldThrowExceptionWhenUserNotFound() {
        when(interpreterRepository.findById(interpreterId)).thenReturn(Optional.of(mockInterpreter));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> appointmentService.createAppointment(appointmentRequestDTO));

        assertEquals("Usuário não encontrado com o id: " + userId, exception.getMessage());
        verify(interpreterRepository).findById(interpreterId);
        verify(userRepository).findById(userId);
        verify(appointmentRepository, never()).save(any());
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve retornar todos os appointments")
    void shouldReturnAllAppointments() {
        List<Appointment> appointments = Collections.singletonList(mockAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        List<AppointmentResponseDTO> result = appointmentService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(appointmentId, result.get(0).getId());

        verify(appointmentRepository).findAll();
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve encontrar appointment por ID")
    void shouldFindAppointmentById() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        AppointmentResponseDTO result = appointmentService.findById(appointmentId);

        assertNotNull(result);
        assertEquals(appointmentId, result.getId());
        assertEquals("SP", result.getUf());

        verify(appointmentRepository).findById(appointmentId);
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve lançar exceção quando appointment não encontrado por ID")
    void shouldThrowExceptionWhenAppointmentNotFoundById() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> appointmentService.findById(appointmentId));

        assertEquals("Solicitação não encontrada com o id: " + appointmentId, exception.getMessage());
        verify(appointmentRepository).findById(appointmentId);
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve atualizar appointment parcialmente para status ACCEPTED")
    void shouldUpdateAppointmentPartiallyToAcceptedStatus() {
        AppointmentPatchRequestDTO patchDTO = AppointmentPatchRequestDTO.builder()
                .uf("RJ")
                .city("Rio de Janeiro")
                .status(AppointmentStatus.ACCEPTED)
                .loggedUserEmail("interpreter@email.com")
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        Appointment updatedAppointment = Appointment.builder()
                .id(appointmentId)
                .uf("RJ")
                .city("Rio de Janeiro")
                .modality(AppointmentModality.ONLINE)
                .date(LocalDate.of(2025, 11, 8))
                .description("Consulta teste atualizada")
                .status(AppointmentStatus.ACCEPTED)
                .interpreter(mockInterpreter)
                .user(mockUser)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(15, 0))
                .build();

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(updatedAppointment);

        AppointmentResponseDTO result = appointmentService.updatePartial(appointmentId, patchDTO);

        assertNotNull(result);
        assertEquals(appointmentId, result.getId());

        verify(appointmentRepository).findById(appointmentId);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(notificationService).sendNotificationToUser(userId, NotificationType.APPOINTMENT_ACCEPTED);
        verify(notificationService).scheduleNotificationForUser(userId, NotificationType.APPOINTMENT_REMINDER,
                LocalDateTime.of(2025, 11, 7, 14, 0));
        verify(notificationService).scheduleNotificationForUser(interpreterId, NotificationType.APPOINTMENT_REMINDER,
                LocalDateTime.of(2025, 11, 7, 14, 0));
        verifyNoMoreInteractions(notificationService);
    }

    @ParameterizedTest
    @DisplayName("Deve atualizar appointment parcialmente para status CANCELED")
    @CsvSource(value = {
            "interpreter@email.com,8ac9c3ef-e7fa-42df-bb37-febc138c3090",
            "user@email.com,1eb35247-bbb6-4564-bc50-bb16ad0901bd"
    })
    void shouldUpdateAppointmentPartiallyToCanceledStatus(String loggedUserEmail, String userToNotifyId) {
        AppointmentPatchRequestDTO patchDTO = AppointmentPatchRequestDTO.builder()
                .uf("RJ")
                .city("Rio de Janeiro")
                .status(AppointmentStatus.CANCELED)
                .loggedUserEmail(loggedUserEmail)
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        Appointment updatedAppointment = Appointment.builder()
                .id(appointmentId)
                .uf("RJ")
                .city("Rio de Janeiro")
                .modality(AppointmentModality.ONLINE)
                .date(LocalDate.of(2025, 11, 8))
                .description("Consulta teste atualizada")
                .status(AppointmentStatus.CANCELED)
                .interpreter(mockInterpreter)
                .user(mockUser)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(15, 0))
                .build();

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(updatedAppointment);

        AppointmentResponseDTO result = appointmentService.updatePartial(appointmentId, patchDTO);

        assertNotNull(result);
        assertEquals(appointmentId, result.getId());

        verify(appointmentRepository).findById(appointmentId);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(notificationService).sendNotificationToUser(UUID.fromString(userToNotifyId), NotificationType.APPOINTMENT_CANCELED);
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve deletar appointment por ID")
    void shouldDeleteAppointmentById() {
        when(appointmentRepository.existsById(appointmentId)).thenReturn(true);
        doNothing().when(appointmentRepository).deleteById(appointmentId);

        appointmentService.delete(appointmentId);

        verify(appointmentRepository).existsById(appointmentId);
        verify(appointmentRepository).deleteById(appointmentId);
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar appointment inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentAppointment() {
        when(appointmentRepository.existsById(appointmentId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> appointmentService.delete(appointmentId));

        assertEquals("Solicitação não encontrada com o id: " + appointmentId, exception.getMessage());
        verify(appointmentRepository).existsById(appointmentId);
        verify(appointmentRepository, never()).deleteById(any());
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve buscar appointments com filtros")
    void shouldSearchAppointmentsWithFilters() {
        List<Appointment> appointments = Collections.singletonList(mockAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        LocalDateTime fromDateTime = LocalDateTime.now();

        List<AppointmentFilterResponseDTO> result = appointmentService.searchAppointments(
                interpreterId, userId, AppointmentStatus.PENDING, AppointmentModality.ONLINE, fromDateTime, true, -1);

        assertNotNull(result);
        verify(appointmentRepository).findAll();
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve buscar appointments sem filtros")
    void shouldSearchAppointmentsWithoutFilters() {
        List<Appointment> appointments = Collections.singletonList(mockAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        List<AppointmentFilterResponseDTO> result = appointmentService.searchAppointments(
                null, null, null, null, null, null, -1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository).findAll();
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve atualizar appointment parcialmente com todos os campos")
    void shouldUpdateAppointmentPartiallyWithAllFields() {
        UUID newInterpreterId = UUID.randomUUID();
        UUID newUserId = UUID.randomUUID();

        Interpreter newInterpreter = Interpreter.builder()
                .id(newInterpreterId)
                .email("interpreter@email.com")
                .build();

        User newUser = new Person() {
            @Override
            public String getDisplayName() {
                return "New User";
            }
        };
        newUser.setId(newUserId);

        AppointmentPatchRequestDTO fullPatchDTO = AppointmentPatchRequestDTO.builder()
                .uf("MG")
                .city("Belo Horizonte")
                .neighborhood("Savassi")
                .street("Rua Nova")
                .streetNumber(456)
                .addressDetails("Casa 2")
                .modality(AppointmentModality.PERSONALLY)
                .date(LocalDate.now().plusDays(3))
                .description("Nova descrição")
                .status(AppointmentStatus.COMPLETED)
                .interpreterId(newInterpreterId)
                .userId(newUserId)
                .startTime(LocalTime.of(16, 0))
                .endTime(LocalTime.of(17, 30))
                .loggedUserEmail("interpreter@email.com")
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));
        when(interpreterRepository.findById(newInterpreterId)).thenReturn(Optional.of(newInterpreter));
        when(userRepository.findById(newUserId)).thenReturn(Optional.of(newUser));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);

        AppointmentResponseDTO result = appointmentService.updatePartial(appointmentId, fullPatchDTO);

        assertNotNull(result);
        verify(appointmentRepository).findById(appointmentId);
        verify(interpreterRepository).findById(newInterpreterId);
        verify(userRepository).findById(newUserId);
        verify(appointmentRepository).save(any(Appointment.class));
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve lançar exceção quando intérprete não encontrado no update")
    void shouldThrowExceptionWhenInterpreterNotFoundInUpdate() {
        UUID newInterpreterId = UUID.randomUUID();
        AppointmentPatchRequestDTO patchDTO = AppointmentPatchRequestDTO.builder()
                .interpreterId(newInterpreterId)
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));
        when(interpreterRepository.findById(newInterpreterId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> appointmentService.updatePartial(appointmentId, patchDTO));

        assertEquals("Intérprete não encontrado com o id: " + newInterpreterId, exception.getMessage());
        verify(appointmentRepository).findById(appointmentId);
        verify(interpreterRepository).findById(newInterpreterId);
        verify(appointmentRepository, never()).save(any());
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado no update")
    void shouldThrowExceptionWhenUserNotFoundInUpdate() {
        UUID newUserId = UUID.randomUUID();
        AppointmentPatchRequestDTO patchDTO = AppointmentPatchRequestDTO.builder()
                .userId(newUserId)
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));
        when(userRepository.findById(newUserId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> appointmentService.updatePartial(appointmentId, patchDTO));

        assertEquals("Usuário não encontrado com o id: " + newUserId, exception.getMessage());
        verify(appointmentRepository).findById(appointmentId);
        verify(userRepository).findById(newUserId);
        verify(appointmentRepository, never()).save(any());
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve lançar exceção quando appointment não encontrado no update")
    void shouldThrowExceptionWhenAppointmentNotFoundInUpdate() {
        AppointmentPatchRequestDTO patchDTO = AppointmentPatchRequestDTO.builder()
                .uf("RJ")
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> appointmentService.updatePartial(appointmentId, patchDTO));

        assertEquals("Solicitação não encontrada com o id: " + appointmentId, exception.getMessage());
        verify(appointmentRepository).findById(appointmentId);
        verify(appointmentRepository, never()).save(any());
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve filtrar appointments por interpreterId")
    void shouldFilterAppointmentsByInterpreterId() {
        UUID otherInterpreterId = UUID.randomUUID();
        Interpreter otherInterpreter = Interpreter.builder().id(otherInterpreterId).build();

        Appointment otherAppointment = Appointment.builder()
                .id(UUID.randomUUID())
                .interpreter(otherInterpreter)
                .user(mockUser)
                .status(AppointmentStatus.PENDING)
                .modality(AppointmentModality.ONLINE)
                .date(LocalDate.now())
                .startTime(LocalTime.of(10, 0))
                .build();

        List<Appointment> appointments = Arrays.asList(mockAppointment, otherAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        List<AppointmentFilterResponseDTO> result = appointmentService.searchAppointments(
                interpreterId, null, null, null, null, null, -1);

        assertNotNull(result);
        assertEquals(1, result.size()); // Só deve retornar o mockAppointment
        verify(appointmentRepository).findAll();
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve filtrar appointments por userId")
    void shouldFilterAppointmentsByUserId() {
        User otherUser = new Person() {
            @Override
            public String getDisplayName() {
                return "Other User";
            }
        };
        UUID otherUserId = UUID.randomUUID();
        otherUser.setId(otherUserId);

        Appointment otherAppointment = Appointment.builder()
                .id(UUID.randomUUID())
                .interpreter(mockInterpreter)
                .user(otherUser)
                .status(AppointmentStatus.PENDING)
                .modality(AppointmentModality.ONLINE)
                .date(LocalDate.now())
                .startTime(LocalTime.of(10, 0))
                .build();

        List<Appointment> appointments = Arrays.asList(mockAppointment, otherAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        List<AppointmentFilterResponseDTO> result = appointmentService.searchAppointments(
                null, userId, null, null, null, null, -1);

        assertNotNull(result);
        assertEquals(1, result.size()); // Só deve retornar o mockAppointment
        verify(appointmentRepository).findAll();
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve filtrar appointments por status")
    void shouldFilterAppointmentsByStatus() {
        Appointment acceptedAppointment = Appointment.builder()
                .id(UUID.randomUUID())
                .interpreter(mockInterpreter)
                .user(mockUser)
                .status(AppointmentStatus.ACCEPTED)
                .modality(AppointmentModality.ONLINE)
                .date(LocalDate.now())
                .startTime(LocalTime.of(10, 0))
                .build();

        List<Appointment> appointments = Arrays.asList(mockAppointment, acceptedAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        List<AppointmentFilterResponseDTO> result = appointmentService.searchAppointments(
                null, null, AppointmentStatus.PENDING, null, null, null, -1);

        assertNotNull(result);
        assertEquals(1, result.size()); // Só deve retornar o mockAppointment (PENDING)
        verify(appointmentRepository).findAll();
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve filtrar appointments por modality")
    void shouldFilterAppointmentsByModality() {
        Appointment personallyAppointment = Appointment.builder()
                .id(UUID.randomUUID())
                .interpreter(mockInterpreter)
                .user(mockUser)
                .status(AppointmentStatus.PENDING)
                .modality(AppointmentModality.PERSONALLY)
                .date(LocalDate.now())
                .startTime(LocalTime.of(10, 0))
                .build();

        List<Appointment> appointments = Arrays.asList(mockAppointment, personallyAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        List<AppointmentFilterResponseDTO> result = appointmentService.searchAppointments(
                null, null, null, AppointmentModality.ONLINE, null, null, -1);

        assertNotNull(result);
        assertEquals(1, result.size()); // Só deve retornar o mockAppointment (ONLINE)
        verify(appointmentRepository).findAll();
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve filtrar appointments por data posterior")
    void shouldFilterAppointmentsByFromDateTime() {
        // Appointment no passado
        Appointment pastAppointment = Appointment.builder()
                .id(UUID.randomUUID())
                .interpreter(mockInterpreter)
                .user(mockUser)
                .status(AppointmentStatus.PENDING)
                .modality(AppointmentModality.ONLINE)
                .date(LocalDate.now().minusDays(1))
                .startTime(LocalTime.of(10, 0))
                .build();

        List<Appointment> appointments = Arrays.asList(mockAppointment, pastAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        LocalDateTime fromDateTime = LocalDateTime.now(); // Filtro para appointments após agora

        List<AppointmentFilterResponseDTO> result = appointmentService.searchAppointments(
                null, null, null, null, fromDateTime, null, -1);

        assertNotNull(result);
        assertEquals(1, result.size()); // Só deve retornar o mockAppointment (futuro)
        verify(appointmentRepository).findAll();
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando filtros não encontram nada")
    void shouldReturnEmptyListWhenFiltersMatchNothing() {
        List<Appointment> appointments = Collections.singletonList(mockAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        UUID nonExistentId = UUID.randomUUID();
        List<AppointmentFilterResponseDTO> result = appointmentService.searchAppointments(
                nonExistentId, null, null, null, null, null, -1);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(appointmentRepository).findAll();
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Should filter appointments by hasRating=true")
    void shouldFilterAppointmentsByHasRatingTrue() {
        List<Appointment> appointments = Collections.singletonList(mockAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);
        when(ratingRepository.existsByAppointment(mockAppointment)).thenReturn(true);

        List<AppointmentFilterResponseDTO> result = appointmentService.searchAppointments(
                null, null, null, null, null, true, -1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ratingRepository).existsByAppointment(mockAppointment);
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Should filter appointments by hasRating=false")
    void shouldFilterAppointmentsByHasRatingFalse() {
        List<Appointment> appointments = Collections.singletonList(mockAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);
        when(ratingRepository.existsByAppointment(mockAppointment)).thenReturn(false);

        List<AppointmentFilterResponseDTO> result = appointmentService.searchAppointments(
                null, null, null, null, null, false, -1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ratingRepository).existsByAppointment(mockAppointment);
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Deve enviar email quando status mudar para ACCEPTED")
    void shouldSendEmailWhenStatusChangedToAccepted() {
        AppointmentPatchRequestDTO patchDTO = AppointmentPatchRequestDTO.builder()
                .status(AppointmentStatus.ACCEPTED)
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        Appointment savedAppointment = Appointment.builder()
                .id(appointmentId)
                .uf(mockAppointment.getUf())
                .city(mockAppointment.getCity())
                .modality(AppointmentModality.ONLINE)
                .date(mockAppointment.getDate())
                .description(mockAppointment.getDescription())
                .status(AppointmentStatus.ACCEPTED)
                .interpreter(mockInterpreter)
                .user(mockUser)
                .startTime(mockAppointment.getStartTime())
                .endTime(mockAppointment.getEndTime())
                .build();

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        appointmentService.updatePartial(appointmentId, patchDTO);

        String expectedDate = savedAppointment.getDate().toString() + " às "
                + savedAppointment.getStartTime().toString();
        verify(emailService).sendAppointmentAcceptedEmail(
                mockUser.getEmail(), mockUser.getDisplayName(), expectedDate,
                mockInterpreter.getDisplayName(), "Online", "Online");
    }

    @Test
    @DisplayName("Deve enviar emails quando status mudar para CANCELED")
    void shouldSendEmailsWhenStatusChangedToCanceled() {
        AppointmentPatchRequestDTO patchDTO = AppointmentPatchRequestDTO.builder()
                .status(AppointmentStatus.CANCELED)
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        Appointment savedAppointment = Appointment.builder()
                .id(appointmentId)
                .uf(mockAppointment.getUf())
                .city(mockAppointment.getCity())
                .modality(AppointmentModality.PERSONALLY)
                .date(mockAppointment.getDate())
                .description("Motivo de cancelamento")
                .status(AppointmentStatus.CANCELED)
                .interpreter(mockInterpreter)
                .user(mockUser)
                .startTime(mockAppointment.getStartTime())
                .endTime(mockAppointment.getEndTime())
                .street("Rua Teste")
                .streetNumber(123)
                .neighborhood("Bairro")
                .build();

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        appointmentService.updatePartial(appointmentId, patchDTO);

        String expectedDate = savedAppointment.getDate().toString() + " às "
                + savedAppointment.getStartTime().toString();

        // Notificar usuário
        verify(emailService).sendAppointmentCanceledEmail(
                mockUser.getEmail(), mockUser.getDisplayName(), expectedDate,
                mockInterpreter.getDisplayName(), savedAppointment.getDescription());

        // Notificar intérprete (parameters order swapped in service)
        verify(emailService).sendAppointmentCanceledEmail(
                mockInterpreter.getEmail(), mockInterpreter.getDisplayName(), expectedDate,
                mockUser.getDisplayName(), savedAppointment.getDescription());
    }

    @Test
    @DisplayName("Deve enviar email de negação quando status voltar para PENDING vindo de ACCEPTED")
    void shouldSendDeniedEmailWhenBackToPendingFromAccepted() {
        // Appointment atualmente ACCEPTED
        Appointment acceptedAppointment = Appointment.builder()
                .id(appointmentId)
                .uf("SP")
                .city("São Paulo")
                .modality(AppointmentModality.ONLINE)
                .date(LocalDate.now().plusDays(1))
                .description("Descricao")
                .status(AppointmentStatus.ACCEPTED)
                .interpreter(mockInterpreter)
                .user(mockUser)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(15, 0))
                .build();

        AppointmentPatchRequestDTO patchDTO = AppointmentPatchRequestDTO.builder()
                .status(AppointmentStatus.PENDING)
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(acceptedAppointment));

        Appointment savedAppointment = Appointment.builder()
                .id(appointmentId)
                .uf(acceptedAppointment.getUf())
                .city(acceptedAppointment.getCity())
                .modality(acceptedAppointment.getModality())
                .date(acceptedAppointment.getDate())
                .description(acceptedAppointment.getDescription())
                .status(AppointmentStatus.PENDING)
                .interpreter(mockInterpreter)
                .user(mockUser)
                .startTime(acceptedAppointment.getStartTime())
                .endTime(acceptedAppointment.getEndTime())
                .build();

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        appointmentService.updatePartial(appointmentId, patchDTO);

        String expectedDate = savedAppointment.getDate().toString() + " às "
                + savedAppointment.getStartTime().toString();
        verify(emailService).sendAppointmentDeniedEmail(
                mockUser.getEmail(), mockUser.getDisplayName(), expectedDate, mockInterpreter.getDisplayName());
    }

}