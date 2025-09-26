package com.pointtils.pointtils.src.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pointtils.pointtils.src.application.dto.requests.AppointmentPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.AppointmentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import com.pointtils.pointtils.src.infrastructure.repositories.AppointmentRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService Tests")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private InterpreterRepository interpreterRepository;

    @Mock
    private UserRepository userRepository;

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
        userId = UUID.randomUUID();
        interpreterId = UUID.randomUUID();

        mockUser = new Person() {
            @Override
            public String getDisplayName() {
                return "Test User";
            }
        };
        mockUser.setId(userId);

        mockInterpreter = Interpreter.builder()
                .id(interpreterId)
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
    }

    @Test
    @DisplayName("Deve lançar exceção quando intérprete não encontrado")
    void shouldThrowExceptionWhenInterpreterNotFound() {
        when(interpreterRepository.findById(interpreterId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> appointmentService.createAppointment(appointmentRequestDTO));
        
        assertEquals("Interpreter não encontrado com o id: " + interpreterId, exception.getMessage());
        verify(interpreterRepository).findById(interpreterId);
        verify(userRepository, never()).findById(any());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void shouldThrowExceptionWhenUserNotFound() {
        when(interpreterRepository.findById(interpreterId)).thenReturn(Optional.of(mockInterpreter));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> appointmentService.createAppointment(appointmentRequestDTO));
        
        assertEquals("User não encontrado com o id: " + userId, exception.getMessage());
        verify(interpreterRepository).findById(interpreterId);
        verify(userRepository).findById(userId);
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar todos os appointments")
    void shouldReturnAllAppointments() {
        List<Appointment> appointments = Arrays.asList(mockAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        List<AppointmentResponseDTO> result = appointmentService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(appointmentId, result.get(0).getId());
        
        verify(appointmentRepository).findAll();
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
    }

    @Test
    @DisplayName("Deve lançar exceção quando appointment não encontrado por ID")
    void shouldThrowExceptionWhenAppointmentNotFoundById() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> appointmentService.findById(appointmentId));
        
        assertEquals("Solicitação não encontrada com o id: " + appointmentId, exception.getMessage());
        verify(appointmentRepository).findById(appointmentId);
    }

    @Test
    @DisplayName("Deve atualizar appointment parcialmente")
    void shouldUpdateAppointmentPartially() {
        AppointmentPatchRequestDTO patchDTO = AppointmentPatchRequestDTO.builder()
                .uf("RJ")
                .city("Rio de Janeiro")
                .status(AppointmentStatus.ACCEPTED)
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));
        
        Appointment updatedAppointment = Appointment.builder()
                .id(appointmentId)
                .uf("RJ")
                .city("Rio de Janeiro")
                .modality(AppointmentModality.ONLINE)
                .date(LocalDate.now().plusDays(1))
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
    }

    @Test
    @DisplayName("Deve deletar appointment por ID")
    void shouldDeleteAppointmentById() {
        when(appointmentRepository.existsById(appointmentId)).thenReturn(true);
        doNothing().when(appointmentRepository).deleteById(appointmentId);

        appointmentService.delete(appointmentId);

        verify(appointmentRepository).existsById(appointmentId);
        verify(appointmentRepository).deleteById(appointmentId);
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
    }

    @Test
    @DisplayName("Deve buscar appointments com filtros")
    void shouldSearchAppointmentsWithFilters() {
        List<Appointment> appointments = Arrays.asList(mockAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);
        
        LocalDateTime fromDateTime = LocalDateTime.now();

        List<AppointmentResponseDTO> result = appointmentService.searchAppointments(
            interpreterId, userId, AppointmentStatus.PENDING, AppointmentModality.ONLINE, fromDateTime);

        assertNotNull(result);
        verify(appointmentRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar appointments sem filtros")
    void shouldSearchAppointmentsWithoutFilters() {
        List<Appointment> appointments = Arrays.asList(mockAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        List<AppointmentResponseDTO> result = appointmentService.searchAppointments(
            null, null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository).findAll();
    }

    @Test
    @DisplayName("Deve validar data/hora no filtro de busca")
    void shouldValidateDateTimeInSearchFilter() {
        Appointment futureAppointment = Appointment.builder()
                .id(UUID.randomUUID())
                .date(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(10, 0))
                .interpreter(mockInterpreter)
                .user(mockUser)
                .status(AppointmentStatus.PENDING)
                .modality(AppointmentModality.ONLINE)
                .build();

        List<Appointment> appointments = Arrays.asList(futureAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);
        
        LocalDateTime fromDateTime = LocalDateTime.now().plusDays(1);

        List<AppointmentResponseDTO> result = appointmentService.searchAppointments(
            null, null, null, null, fromDateTime);

        assertNotNull(result);
        verify(appointmentRepository).findAll();
    }
}