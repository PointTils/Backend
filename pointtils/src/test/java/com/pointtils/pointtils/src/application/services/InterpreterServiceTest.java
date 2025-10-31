package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterListResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.mapper.InterpreterResponseMapper;
import com.pointtils.pointtils.src.application.mapper.LocationMapper;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.core.domain.entities.Specialty;
import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.pointtils.pointtils.src.util.TestDataUtil.createInterpreterCreationRequest;
import static com.pointtils.pointtils.src.util.TestDataUtil.createInterpreterPatchRequest;
import static com.pointtils.pointtils.src.util.TestDataUtil.createLocationPatchRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterpreterServiceTest {

        @Mock
        private InterpreterRepository repository;
        @Mock
        private PasswordEncoder passwordEncoder;
        @Mock
        private InterpreterResponseMapper responseMapper;
        @Mock
        private EmailService emailService;
        @Spy
        private LocationMapper locationMapper = new LocationMapper();
        @InjectMocks
        private InterpreterService service;

        @BeforeEach
        void setup() {
                ReflectionTestUtils.setField(service, "adminEmail", "admin@pointtils.com");
                ReflectionTestUtils.setField(service, "apiBaseUrl", "http://localhost:8080");
        }

        @Test
        void shouldRegisterNewInterpreter() {
                Interpreter interpreter = new Interpreter();
                InterpreterResponseDTO mappedResponse = new InterpreterResponseDTO();
                ArgumentCaptor<Interpreter> interpreterArgumentCaptor = ArgumentCaptor.forClass(Interpreter.class);
                when(repository.save(interpreterArgumentCaptor.capture())).thenReturn(interpreter);
                when(responseMapper.toResponseDTO(interpreter)).thenReturn(mappedResponse);
                when(passwordEncoder.encode("senha123")).thenReturn("hashedPassword");

                assertEquals(mappedResponse, service.registerBasic(createInterpreterCreationRequest()));
                assertEquals("João Intérprete", interpreterArgumentCaptor.getValue().getName());
                assertEquals("interpreter@exemplo.com", interpreterArgumentCaptor.getValue().getEmail());
                assertEquals("51999999999", interpreterArgumentCaptor.getValue().getPhone());
                assertEquals("hashedPassword", interpreterArgumentCaptor.getValue().getPassword());
                assertEquals("PENDING", interpreterArgumentCaptor.getValue().getStatus().name());
                assertEquals("INTERPRETER", interpreterArgumentCaptor.getValue().getType().name());
                assertEquals("picture_url", interpreterArgumentCaptor.getValue().getPicture());
                assertEquals("MALE", interpreterArgumentCaptor.getValue().getGender().name());
                assertEquals("1990-01-01", interpreterArgumentCaptor.getValue().getBirthday().toString());
                assertEquals(BigDecimal.ZERO, interpreterArgumentCaptor.getValue().getRating());
                assertEquals("", interpreterArgumentCaptor.getValue().getDescription());
                assertEquals("ALL", interpreterArgumentCaptor.getValue().getModality().name());
                assertFalse(interpreterArgumentCaptor.getValue().getImageRights());
        }

        @Test
        void shouldRegisterInterpreterWithoutProfessionalData() {
                var request = createInterpreterCreationRequest();
                request.setProfessionalData(null);

                InterpreterResponseDTO mapped = new InterpreterResponseDTO();

                when(repository.save(Mockito.argThat(interpreter -> interpreter.getEmail().equals(request.getEmail()))))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                when(responseMapper.toResponseDTO(
                                Mockito.argThat(interpreter -> interpreter.getEmail().equals(request.getEmail()))))
                                .thenReturn(mapped);

                when(passwordEncoder.encode("senha123")).thenReturn("encoded");

                InterpreterResponseDTO result = service.registerBasic(request);

                assertEquals(mapped, result);

                ArgumentCaptor<Interpreter> captor = ArgumentCaptor.forClass(Interpreter.class);
                verify(repository).save(captor.capture());
                assertEquals(null, captor.getValue().getCnpj());
        }

        @Test
        void shouldFindAll() {
                UUID id = UUID.randomUUID();
                Interpreter foundInterpreter = Interpreter.builder().id(id).build();
                InterpreterListResponseDTO mappedResponse = InterpreterListResponseDTO.builder().id(id).build();
                when(repository.findAll(any(Specification.class))).thenReturn(List.of(foundInterpreter));
                when(responseMapper.toListResponseDTO(foundInterpreter)).thenReturn(mappedResponse);

                assertThat(service.findAll(null, null, null, null, null, null, null, null))
                                .hasSize(1)
                                .contains(mappedResponse);
        }

        @Test
        void shouldFindAllWithFilters() {
                // Arrange
                UUID id = UUID.randomUUID();
                Location location = Location.builder()
                                .id(UUID.randomUUID())
                                .uf("SP")
                                .city("São Paulo")
                                .neighborhood("Higienópolis")
                                .build();

                List<Location> locations = new ArrayList<>();
                locations.add(location);

                Specialty specialty = new Specialty("Libras");
                specialty.setId(UUID.randomUUID());
                Set<Specialty> specialties = new HashSet<>();
                specialties.add(specialty);

                Schedule schedule = new Schedule();
                schedule.setDay(DayOfWeek.WED);
                schedule.setStartTime(LocalTime.of(9, 0));
                schedule.setEndTime(LocalTime.of(18, 0));
                Set<Schedule> schedules = new HashSet<>();
                schedules.add(schedule);

                Interpreter foundInterpreter = Interpreter.builder()
                                .id(id)
                                .name("interpreter")
                                .gender(Gender.FEMALE)
                                .modality(InterpreterModality.ONLINE)
                                .locations(locations)
                                .specialties(specialties)
                                .schedules(schedules)
                                .build();

                InterpreterListResponseDTO mappedResponse = InterpreterListResponseDTO.builder()
                                .id(id)
                                .build();

                when(repository.findAll(any(Specification.class))).thenReturn(List.of(foundInterpreter));
                when(responseMapper.toListResponseDTO(foundInterpreter)).thenReturn(mappedResponse);

                // Act
                List<InterpreterListResponseDTO> result = service.findAll(
                                "ONLINE",
                                "FEMALE",
                                "São Paulo",
                                "SP",
                                "Higienópolis",
                                specialty.getId().toString(),
                                "2025-12-31 10:00",
                                null);

                // Assert
                assertThat(result)
                                .hasSize(1)
                                .contains(mappedResponse);
        }

        @Test
        void shouldFindById() {
                UUID id = UUID.randomUUID();
                Interpreter foundInterpreter = Interpreter.builder().id(id).build();
                InterpreterResponseDTO mappedResponse = InterpreterResponseDTO.builder().id(id).build();
                when(repository.findById(id)).thenReturn(Optional.of(foundInterpreter));
                when(responseMapper.toResponseDTO(foundInterpreter)).thenReturn(mappedResponse);

                assertEquals(mappedResponse, service.findById(id));
        }

        @Test
        void shouldThrowExceptionIfFindByIdHasNoInterpreter() {
                UUID id = UUID.randomUUID();
                when(repository.findById(id)).thenReturn(Optional.empty());

                assertThrows(EntityNotFoundException.class, () -> service.findById(id));
        }

        @Test
        void shouldDeleteById() {
                UUID id = UUID.randomUUID();
                Interpreter foundInterpreter = Interpreter.builder().id(id).build();
                ArgumentCaptor<Interpreter> interpreterArgumentCaptor = ArgumentCaptor.forClass(Interpreter.class);
                when(repository.findById(id)).thenReturn(Optional.of(foundInterpreter));
                when(repository.save(interpreterArgumentCaptor.capture())).thenReturn(foundInterpreter);

                assertDoesNotThrow(() -> service.delete(id));
                assertEquals(UserStatus.INACTIVE, interpreterArgumentCaptor.getValue().getStatus());
        }

        @Test
        void shouldThrowExceptionIfDeleteByIdHasNoInterpreter() {
                UUID id = UUID.randomUUID();
                when(repository.findById(id)).thenReturn(Optional.empty());

                assertThrows(EntityNotFoundException.class, () -> service.delete(id));
        }

        @Test
        void shouldUpdateCompleteInterpreterDataWithAndWithoutProfessionalData() {
                UUID id = UUID.randomUUID();
                Interpreter found = Interpreter.builder()
                                .id(id)
                                .cnpj("12345678000195")
                                .build();
                InterpreterResponseDTO mapped = InterpreterResponseDTO.builder().id(id).build();

                when(repository.findById(id)).thenReturn(Optional.of(found));
                when(repository.save(Mockito.argThat(interpreter -> interpreter.getId().equals(id))))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(responseMapper.toResponseDTO(found)).thenReturn(mapped);
                when(passwordEncoder.encode("novaSenha")).thenReturn("encoded");

                var dto = createInterpreterCreationRequest();
                dto.setPassword("novaSenha");

                InterpreterResponseDTO resultWithProfessional = service.updateComplete(id, dto);
                assertEquals(mapped, resultWithProfessional);
                assertEquals("encoded", found.getPassword());

                var dtoWithoutProfessional = createInterpreterCreationRequest();
                dtoWithoutProfessional.setProfessionalData(null);
                dtoWithoutProfessional.setPassword(null);

                InterpreterResponseDTO resultWithoutProfessional = service.updateComplete(id, dtoWithoutProfessional);
                assertEquals(mapped, resultWithoutProfessional);

                assertEquals("12345678000195", found.getCnpj());
        }

        @Test
        void shouldUpdateAllInterpreterData() {
                UUID interpreterId = UUID.randomUUID();
                Interpreter foundInterpreter = Interpreter.builder().id(interpreterId).build();
                Location oldLocation = Location.builder()
                                .id(UUID.randomUUID())
                                .uf("RS")
                                .city("Porto Alegre")
                                .neighborhood("São João")
                                .interpreter(foundInterpreter)
                                .build();
                foundInterpreter.setLocations(new ArrayList<>(List.of(oldLocation)));
                InterpreterResponseDTO mappedResponse = InterpreterResponseDTO.builder().id(interpreterId).build();
                ArgumentCaptor<Interpreter> interpreterArgumentCaptor = ArgumentCaptor.forClass(Interpreter.class);

                when(repository.findById(interpreterId)).thenReturn(Optional.of(foundInterpreter));
                when(repository.save(interpreterArgumentCaptor.capture())).thenReturn(foundInterpreter);
                when(responseMapper.toResponseDTO(foundInterpreter)).thenReturn(mappedResponse);

                assertEquals(mappedResponse, service.updatePartial(interpreterId, createInterpreterPatchRequest()));
                assertEquals("Novo Nome", interpreterArgumentCaptor.getValue().getName());
                assertEquals("novo.nome@email.com", interpreterArgumentCaptor.getValue().getEmail());
                assertEquals("51988888888", interpreterArgumentCaptor.getValue().getPhone());
                assertEquals(Gender.FEMALE, interpreterArgumentCaptor.getValue().getGender());
                assertEquals("98765432000196", interpreterArgumentCaptor.getValue().getCnpj());
                assertEquals(InterpreterModality.ONLINE, interpreterArgumentCaptor.getValue().getModality());
                assertEquals("Teste", interpreterArgumentCaptor.getValue().getDescription());
                assertFalse(interpreterArgumentCaptor.getValue().getImageRights());
                assertThat(interpreterArgumentCaptor.getValue().getLocations())
                                .hasSize(1)
                                .anyMatch(loc -> loc.getUf().equals("SP") &&
                                                loc.getCity().equals("São Paulo") &&
                                                loc.getNeighborhood().equals("Higienópolis") &&
                                                loc.getInterpreter().getId().equals(interpreterId));
        }

        @Test
        void shouldUpdateInterpreterLocationById() {
                UUID interpreterId = UUID.randomUUID();
                Interpreter foundInterpreter = Interpreter.builder().id(interpreterId).build();
                Location oldLocation = Location.builder()
                                .id(UUID.randomUUID())
                                .uf("RS")
                                .city("Porto Alegre")
                                .neighborhood("São João")
                                .interpreter(foundInterpreter)
                                .build();
                foundInterpreter.setLocations(new ArrayList<>(List.of(oldLocation)));
                InterpreterResponseDTO mappedResponse = InterpreterResponseDTO.builder().id(interpreterId).build();
                ArgumentCaptor<Interpreter> interpreterArgumentCaptor = ArgumentCaptor.forClass(Interpreter.class);

                when(repository.findById(interpreterId)).thenReturn(Optional.of(foundInterpreter));
                when(repository.save(interpreterArgumentCaptor.capture())).thenReturn(foundInterpreter);
                when(responseMapper.toResponseDTO(foundInterpreter)).thenReturn(mappedResponse);

                assertEquals(mappedResponse, service.updatePartial(interpreterId, createLocationPatchRequest()));
                assertThat(interpreterArgumentCaptor.getValue().getLocations())
                                .hasSize(1)
                                .anyMatch(loc -> loc.getUf().equals("SP") &&
                                                loc.getCity().equals("São Paulo") &&
                                                loc.getNeighborhood().equals("Higienópolis") &&
                                                loc.getInterpreter().getId().equals(interpreterId));
        }

        @Test
        void shouldApproveInterpreterSuccessfullyWhenEmailSent() {
                UUID id = UUID.randomUUID();
                Interpreter interpreter = Interpreter.builder()
                                .id(id)
                                .name("João")
                                .email("joao@email.com")
                                .status(UserStatus.PENDING)
                                .build();

                when(repository.findById(id)).thenReturn(Optional.of(interpreter));
                when(repository.save(Mockito.argThat(i -> i.getId().equals(id))))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(emailService.sendInterpreterFeedbackEmail("joao@email.com", "João", true)).thenReturn(true);

                boolean result = service.approveInterpreter(id);

                assertTrue(result);
                assertEquals(UserStatus.ACTIVE, interpreter.getStatus());

                ArgumentCaptor<Interpreter> captor = ArgumentCaptor.forClass(Interpreter.class);
                verify(repository).save(captor.capture());
                assertEquals(id, captor.getValue().getId());
                assertEquals(UserStatus.ACTIVE, captor.getValue().getStatus());

                verify(emailService).sendInterpreterFeedbackEmail("joao@email.com", "João", true);
        }

        @Test
        void shouldApproveInterpreterButEmailFailsToSend() {
                UUID id = UUID.randomUUID();
                Interpreter interpreter = Interpreter.builder()
                                .id(id)
                                .name("Maria")
                                .email("maria@email.com")
                                .status(UserStatus.PENDING)
                                .build();

                when(repository.findById(id)).thenReturn(Optional.of(interpreter));
                when(repository.save(Mockito.argThat(i -> i.getId().equals(id))))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(emailService.sendInterpreterFeedbackEmail("maria@email.com", "Maria", true))
                                .thenReturn(false);

                boolean result = service.approveInterpreter(id);

                assertTrue(result);
                assertEquals(UserStatus.ACTIVE, interpreter.getStatus());

                ArgumentCaptor<Interpreter> captor = ArgumentCaptor.forClass(Interpreter.class);
                verify(repository).save(captor.capture());
                assertEquals(id, captor.getValue().getId());
                assertEquals(UserStatus.ACTIVE, captor.getValue().getStatus());

                verify(emailService).sendInterpreterFeedbackEmail("maria@email.com", "Maria", true);
        }

        @Test
        void shouldThrowExceptionWhenApprovingAlreadyApprovedInterpreter() {
                UUID id = UUID.randomUUID();
                Interpreter interpreter = Interpreter.builder()
                                .id(id)
                                .status(UserStatus.ACTIVE)
                                .build();

                when(repository.findById(id)).thenReturn(Optional.of(interpreter));

                assertThrows(IllegalArgumentException.class, () -> service.approveInterpreter(id));
        }

        @Test
        void shouldRejectInterpreterSuccessfullyWhenEmailSent() {
                UUID id = UUID.randomUUID();
                Interpreter interpreter = Interpreter.builder()
                                .id(id)
                                .name("José")
                                .email("jose@email.com")
                                .status(UserStatus.PENDING)
                                .build();

                when(repository.findById(id)).thenReturn(Optional.of(interpreter));
                when(repository.save(Mockito.argThat(i -> i.getId().equals(id))))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(emailService.sendInterpreterFeedbackEmail("jose@email.com", "José", false))
                                .thenReturn(true);

                boolean result = service.rejectInterpreter(id);

                assertTrue(result);
                assertEquals(UserStatus.INACTIVE, interpreter.getStatus());

                ArgumentCaptor<Interpreter> captor = ArgumentCaptor.forClass(Interpreter.class);
                verify(repository).save(captor.capture());
                assertEquals(id, captor.getValue().getId());
                assertEquals(UserStatus.INACTIVE, captor.getValue().getStatus());

                verify(emailService).sendInterpreterFeedbackEmail("jose@email.com", "José", false);
        }

        @Test
        void shouldRejectInterpreterButEmailFailsToSend() {
                UUID id = UUID.randomUUID();
                Interpreter interpreter = Interpreter.builder()
                                .id(id)
                                .name("Ana")
                                .email("ana@email.com")
                                .status(UserStatus.PENDING)
                                .build();

                when(repository.findById(id)).thenReturn(Optional.of(interpreter));
                when(repository.save(Mockito.argThat(i -> i.getId().equals(id))))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(emailService.sendInterpreterFeedbackEmail("ana@email.com", "Ana", false))
                                .thenReturn(false);

                boolean result = service.rejectInterpreter(id);

                assertTrue(result);
                assertEquals(UserStatus.INACTIVE, interpreter.getStatus());

                ArgumentCaptor<Interpreter> captor = ArgumentCaptor.forClass(Interpreter.class);
                verify(repository).save(captor.capture());
                assertEquals(id, captor.getValue().getId());
                assertEquals(UserStatus.INACTIVE, captor.getValue().getStatus());

                verify(emailService).sendInterpreterFeedbackEmail("ana@email.com", "Ana", false);
        }

        @Test
        void shouldThrowExceptionWhenRejectingAlreadyProcessedInterpreter() {
                UUID id = UUID.randomUUID();
                Interpreter interpreter = Interpreter.builder()
                                .id(id)
                                .status(UserStatus.ACTIVE)
                                .build();

                when(repository.findById(id)).thenReturn(Optional.of(interpreter));

                assertThrows(IllegalArgumentException.class, () -> service.rejectInterpreter(id));
        }

        @Test
        void approveInterpreter_ShouldReturnFalse_WhenFindThrows() {
                UUID id = UUID.randomUUID();
                when(repository.findById(id)).thenThrow(new RuntimeException("DB error"));

                boolean result = service.approveInterpreter(id);

                assertFalse(result);
                verify(repository, never()).save(any());
                verify(emailService, never()).sendInterpreterFeedbackEmail(any(), any(), anyBoolean());
        }

        @Test
        void approveInterpreter_ShouldReturnFalse_WhenEmailServiceThrows() {
                UUID id = UUID.randomUUID();
                Interpreter interpreter = Interpreter.builder()
                                .id(id)
                                .name("João")
                                .email("joao@example.com")
                                .status(UserStatus.PENDING)
                                .build();

                when(repository.findById(id)).thenReturn(Optional.of(interpreter));
                when(repository.save(any(Interpreter.class))).thenReturn(interpreter);
                when(emailService.sendInterpreterFeedbackEmail("joao@example.com", "João", true))
                                .thenThrow(new RuntimeException("SMTP down"));

                boolean result = service.approveInterpreter(id);

                assertFalse(result);
                assertEquals(UserStatus.ACTIVE, interpreter.getStatus());
                verify(repository).save(interpreter);
                verify(emailService).sendInterpreterFeedbackEmail("joao@example.com", "João", true);
        }

        @Test
        void rejectInterpreter_ShouldReturnFalse_WhenFindThrows() {
                UUID id = UUID.randomUUID();
                when(repository.findById(id)).thenThrow(new RuntimeException("DB error"));

                boolean result = service.rejectInterpreter(id);

                assertFalse(result);
                verify(repository, never()).save(any());
                verify(emailService, never()).sendInterpreterFeedbackEmail(any(), any(), anyBoolean());
        }

        @Test
        void rejectInterpreter_ShouldReturnFalse_WhenEmailServiceThrows() {
                UUID id = UUID.randomUUID();
                Interpreter interpreter = Interpreter.builder()
                                .id(id)
                                .name("Maria")
                                .email("maria@example.com")
                                .status(UserStatus.PENDING)
                                .build();

                when(repository.findById(id)).thenReturn(Optional.of(interpreter));
                when(repository.save(any(Interpreter.class))).thenReturn(interpreter);
                when(emailService.sendInterpreterFeedbackEmail("maria@example.com", "Maria", false))
                                .thenThrow(new RuntimeException("SMTP down"));

                boolean result = service.rejectInterpreter(id);

                assertFalse(result);
                assertEquals(UserStatus.INACTIVE, interpreter.getStatus());
                verify(repository).save(interpreter);
                verify(emailService).sendInterpreterFeedbackEmail("maria@example.com", "Maria", false);
        }

        @Test
        void shouldSendRegistrationEmailSuccessfully() {
                InterpreterBasicRequestDTO request = createInterpreterCreationRequest();

                ArgumentCaptor<Interpreter> interpreterCaptor = ArgumentCaptor.forClass(Interpreter.class);
                when(repository.save(interpreterCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
                when(responseMapper.toResponseDTO(any())).thenReturn(new InterpreterResponseDTO());
                when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded");

                when(emailService.sendInterpreterRegistrationRequestEmail(
                                eq("admin@pointtils.com"),
                                eq(request.getName()),
                                eq(request.getCpf()),
                                eq(request.getProfessionalData().getCnpj()),
                                eq(request.getEmail()),
                                eq(request.getPhone()),
                                anyString(),
                                anyString())).thenReturn(true);

                assertDoesNotThrow(() -> service.registerBasic(request));

                Interpreter saved = interpreterCaptor.getValue();
                verify(emailService).sendInterpreterRegistrationRequestEmail(
                                eq("admin@pointtils.com"),
                                eq(saved.getName()),
                                eq(saved.getCpf()),
                                eq(saved.getCnpj()),
                                eq(saved.getEmail()),
                                eq(saved.getPhone()),
                                anyString(),
                                anyString());
        }

        @Test
        void shouldHandleEmailFailureWhenSendingRegistrationEmail() {
                InterpreterBasicRequestDTO request = createInterpreterCreationRequest();

                ArgumentCaptor<Interpreter> interpreterCaptor = ArgumentCaptor.forClass(Interpreter.class);
                when(repository.save(interpreterCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
                when(responseMapper.toResponseDTO(any())).thenReturn(new InterpreterResponseDTO());
                when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded");

                when(emailService.sendInterpreterRegistrationRequestEmail(
                                eq("admin@pointtils.com"),
                                eq(request.getName()),
                                eq(request.getCpf()),
                                eq(request.getProfessionalData().getCnpj()),
                                eq(request.getEmail()),
                                eq(request.getPhone()),
                                anyString(),
                                anyString())).thenReturn(false);

                assertDoesNotThrow(() -> service.registerBasic(request));

                Interpreter saved = interpreterCaptor.getValue();
                verify(emailService).sendInterpreterRegistrationRequestEmail(
                                eq("admin@pointtils.com"),
                                eq(saved.getName()),
                                eq(saved.getCpf()),
                                eq(saved.getCnpj()),
                                eq(saved.getEmail()),
                                eq(saved.getPhone()),
                                anyString(),
                                anyString());
        }

        @Test
        void shouldHandleExceptionWhenSendingRegistrationEmail() {
                InterpreterBasicRequestDTO request = createInterpreterCreationRequest();

                ArgumentCaptor<Interpreter> interpreterCaptor = ArgumentCaptor.forClass(Interpreter.class);
                when(repository.save(interpreterCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
                when(responseMapper.toResponseDTO(any())).thenReturn(new InterpreterResponseDTO());
                when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded");

                when(emailService.sendInterpreterRegistrationRequestEmail(
                                eq("admin@pointtils.com"),
                                eq(request.getName()),
                                eq(request.getCpf()),
                                eq(request.getProfessionalData().getCnpj()),
                                eq(request.getEmail()),
                                eq(request.getPhone()),
                                anyString(),
                                anyString())).thenThrow(new RuntimeException("SMTP error"));

                assertDoesNotThrow(() -> service.registerBasic(request));

                Interpreter saved = interpreterCaptor.getValue();
                verify(emailService).sendInterpreterRegistrationRequestEmail(
                                eq("admin@pointtils.com"),
                                eq(saved.getName()),
                                eq(saved.getCpf()),
                                eq(saved.getCnpj()),
                                eq(saved.getEmail()),
                                eq(saved.getPhone()),
                                anyString(),
                                anyString());
        }
}
