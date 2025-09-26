package com.pointtils.pointtils.src.infrastructure.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("AppointmentRepository Simple Integration Tests")
class AppointmentRepositorySimpleTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @BeforeEach
    void setUp() {}

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
        
        assertEquals(AppointmentStatus.PENDING, AppointmentStatus.fromString("pending"));
        assertEquals(AppointmentStatus.ACCEPTED, AppointmentStatus.fromString("accepted"));
        assertEquals(AppointmentStatus.CANCELED, AppointmentStatus.fromString("canceled"));
        assertEquals(AppointmentStatus.COMPLETED, AppointmentStatus.fromString("completed"));
    }

    @Test
    @DisplayName("Deve validar enums de AppointmentModality")
    void shouldValidateAppointmentModalityEnums() {
        assertNotNull(AppointmentModality.ONLINE);
        assertNotNull(AppointmentModality.PERSONALLY);
        
        assertEquals(AppointmentModality.ONLINE, AppointmentModality.fromString("online"));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromString("personally"));
        assertEquals(AppointmentModality.PERSONALLY, AppointmentModality.fromString("presencial"));
    }
}