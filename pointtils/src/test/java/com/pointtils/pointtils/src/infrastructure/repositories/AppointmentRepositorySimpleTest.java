package com.pointtils.pointtils.src.infrastructure.repositories;

import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes de Integração AppointmentRepository")
class AppointmentRepositorySimpleTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User mockUser;
    private Interpreter mockInterpreter;

    @BeforeEach
    void setUp() {
        Person testPerson = Person.builder()
                .email("test@test.com")
                .password("password")
                .phone("11999999999")
                .status(com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus.ACTIVE)
                .type(com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE.PERSON)
                .name("Test User")
                .build();
        mockUser = entityManager.persistAndFlush(testPerson);

        mockInterpreter = Interpreter.builder()
                .email("interpreter@test.com")
                .password("password")
                .phone("11888888888")
                .status(com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus.ACTIVE)
                .type(com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE.INTERPRETER)
                .name("Test Interpreter")
                .build();
        mockInterpreter = entityManager.persistAndFlush(mockInterpreter);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há appointments")
    void shouldReturnEmptyListWhenNoAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();

        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista vazia para status inexistente")
    void shouldReturnEmptyListForNonExistentStatus() {
        List<Appointment> appointments = appointmentRepository.findByStatus(AppointmentStatus.CANCELED);

        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista vazia para data inexistente")
    void shouldReturnEmptyListForNonExistentDate() {
        LocalDate futureDate = LocalDate.now().plusDays(100);

        List<Appointment> appointments = appointmentRepository.findByDate(futureDate);

        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());
    }

    @Test
    @DisplayName("Deve verificar que appointment não existe por ID inexistente")
    void shouldReturnEmptyOptionalForNonExistentId() {
        java.util.UUID nonExistentId = java.util.UUID.randomUUID();

        Optional<Appointment> appointment = appointmentRepository.findById(nonExistentId);

        assertTrue(appointment.isEmpty());
    }

    @Test
    @DisplayName("Deve verificar que não existe appointment por ID inexistente")
    void shouldReturnFalseForNonExistentId() {
        java.util.UUID nonExistentId = java.util.UUID.randomUUID();

        boolean exists = appointmentRepository.existsById(nonExistentId);

        assertFalse(exists);
    }

    @Test
    @DisplayName("Deve contar zero appointments quando não há dados")
    void shouldCountZeroWhenNoAppointments() {
        long count = appointmentRepository.count();

        assertEquals(0, count);
    }

    @Test
    @DisplayName("Deve validar estrutura de entidade Appointment")
    void shouldValidateAppointmentEntityStructure() {
        Appointment appointment = Appointment.builder()
                .uf("RJ")
                .city("Rio de Janeiro")
                .neighborhood("Copacabana")
                .street("Rua do Teste")
                .streetNumber(123)
                .addressDetails("Apto 101")
                .modality(AppointmentModality.PERSONALLY)
                .date(LocalDate.now().plusDays(2))
                .description("Appointment para teste de estrutura")
                .status(AppointmentStatus.ACCEPTED)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .interpreter(mockInterpreter)
                .user(mockUser)
                .build();

        assertNotNull(appointment);
        assertEquals("RJ", appointment.getUf());
        assertEquals("Rio de Janeiro", appointment.getCity());
        assertEquals("Copacabana", appointment.getNeighborhood());
        assertEquals("Rua do Teste", appointment.getStreet());
        assertEquals(123, appointment.getStreetNumber());
        assertEquals("Apto 101", appointment.getAddressDetails());
        assertEquals(AppointmentModality.PERSONALLY, appointment.getModality());
        assertEquals(AppointmentStatus.ACCEPTED, appointment.getStatus());
        assertEquals(LocalTime.of(9, 0), appointment.getStartTime());
        assertEquals(LocalTime.of(10, 0), appointment.getEndTime());
    }

    @Test
    @DisplayName("Deve validar que repository é injetado corretamente")
    void shouldValidateRepositoryIsInjectedCorrectly() {
        assertNotNull(appointmentRepository);
    }

    @Test
    @DisplayName("Deve validar métodos de consulta customizada do repository")
    void shouldValidateCustomRepositoryMethods() {
        assertDoesNotThrow(() -> appointmentRepository.findByStatus(AppointmentStatus.PENDING));
        assertDoesNotThrow(() -> appointmentRepository.findByDate(LocalDate.now()));
    }

    @Test
    @DisplayName("Deve validar enums de AppointmentStatus")
    void shouldValidateAppointmentStatusEnums() {
        assertNotNull(AppointmentStatus.PENDING);
        assertNotNull(AppointmentStatus.ACCEPTED);
        assertNotNull(AppointmentStatus.CANCELED);
        assertNotNull(AppointmentStatus.COMPLETED);

        assertEquals(AppointmentStatus.PENDING, AppointmentStatus.fromJson("pending"));
        assertEquals(AppointmentStatus.ACCEPTED, AppointmentStatus.fromJson("accepted"));
        assertEquals(AppointmentStatus.CANCELED, AppointmentStatus.fromJson("canceled"));
        assertEquals(AppointmentStatus.COMPLETED, AppointmentStatus.fromJson("completed"));
    }

    @Test
    @DisplayName("Deve validar enums de AppointmentModality")
    void shouldValidateAppointmentModalityEnums() {
        assertNotNull(AppointmentModality.ONLINE);
        assertNotNull(AppointmentModality.PERSONALLY);

        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromJson("online"));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromJson("personally"));
    }

    @Test
    @DisplayName("Deve validar que appointment pode ser salvo e recuperado")
    void shouldSaveAndRetrieveAppointment() {
        Appointment appointment = Appointment.builder()
                .uf("SP")
                .city("São Paulo")
                .modality(AppointmentModality.ONLINE)
                .date(LocalDate.now().plusDays(1))
                .description("Teste de persistência")
                .status(AppointmentStatus.PENDING)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .interpreter(mockInterpreter)
                .user(mockUser)
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("SP", saved.getUf());
        assertEquals("São Paulo", saved.getCity());
        assertEquals(AppointmentModality.ONLINE, saved.getModality());
        assertEquals(AppointmentStatus.PENDING, saved.getStatus());
    }

    @Test
    @DisplayName("Deve contar appointments salvos corretamente")
    void shouldCountSavedAppointments() {
        assertEquals(0, appointmentRepository.count());

        Appointment appointment1 = Appointment.builder()
                .uf("SP")
                .city("São Paulo")
                .modality(AppointmentModality.ONLINE)
                .date(LocalDate.now().plusDays(1))
                .status(AppointmentStatus.PENDING)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .description("Appointment 1")
                .interpreter(mockInterpreter)
                .user(mockUser)
                .build();

        Appointment appointment2 = Appointment.builder()
                .uf("RJ")
                .city("Rio de Janeiro")
                .modality(AppointmentModality.PERSONALLY)
                .date(LocalDate.now().plusDays(2))
                .status(AppointmentStatus.ACCEPTED)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(15, 0))
                .description("Appointment 2")
                .interpreter(mockInterpreter)
                .user(mockUser)
                .build();

        appointmentRepository.save(appointment1);
        appointmentRepository.save(appointment2);

        assertEquals(2, appointmentRepository.count());
    }

    @Test
    @DisplayName("Deve validar todos os campos obrigatórios")
    void shouldValidateRequiredFields() {
        Appointment appointment = new Appointment();
        appointment.setUf("MG");
        appointment.setCity("Belo Horizonte");
        appointment.setModality(AppointmentModality.PERSONALLY);
        appointment.setDate(LocalDate.now().plusDays(3));
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setStartTime(LocalTime.of(16, 0));
        appointment.setEndTime(LocalTime.of(17, 30));
        appointment.setDescription("Appointment completo");
        appointment.setInterpreter(mockInterpreter);
        appointment.setUser(mockUser);

        Appointment saved = appointmentRepository.save(appointment);

        assertNotNull(saved);
        assertEquals("MG", saved.getUf());
        assertEquals("Belo Horizonte", saved.getCity());
        assertEquals(AppointmentModality.PERSONALLY, saved.getModality());
        assertEquals(AppointmentStatus.COMPLETED, saved.getStatus());
        assertEquals("Appointment completo", saved.getDescription());
    }

    @Test
    @DisplayName("Deve permitir busca por múltiplos status")
    void shouldAllowSearchByMultipleStatuses() {
        Appointment pending = Appointment.builder()
                .uf("SP").city("São Paulo")
                .modality(AppointmentModality.ONLINE)
                .date(LocalDate.now().plusDays(1))
                .status(AppointmentStatus.PENDING)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .description("Pending appointment")
                .interpreter(mockInterpreter)
                .user(mockUser)
                .build();

        Appointment accepted = Appointment.builder()
                .uf("RJ").city("Rio de Janeiro")
                .modality(AppointmentModality.PERSONALLY)
                .date(LocalDate.now().plusDays(2))
                .status(AppointmentStatus.ACCEPTED)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(15, 0))
                .description("Accepted appointment")
                .interpreter(mockInterpreter)
                .user(mockUser)
                .build();

        appointmentRepository.save(pending);
        appointmentRepository.save(accepted);

        List<Appointment> pendingAppointments = appointmentRepository.findByStatus(AppointmentStatus.PENDING);
        List<Appointment> acceptedAppointments = appointmentRepository.findByStatus(AppointmentStatus.ACCEPTED);

        assertEquals(1, pendingAppointments.size());
        assertEquals(1, acceptedAppointments.size());
        assertEquals(AppointmentStatus.PENDING, pendingAppointments.get(0).getStatus());
        assertEquals(AppointmentStatus.ACCEPTED, acceptedAppointments.get(0).getStatus());
    }
}