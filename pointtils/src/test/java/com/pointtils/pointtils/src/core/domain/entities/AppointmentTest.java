package com.pointtils.pointtils.src.core.domain.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;

@DisplayName("Testes da Entidade Appointment")
class AppointmentTest {

    private Appointment appointment;
    private User mockUser;
    private Interpreter mockInterpreter;

    @BeforeEach
    void setUp() {
        mockUser = new Person() {
            @Override
            public String getDisplayName() {
                return "Test User";
            }
        };
        mockUser.setId(UUID.randomUUID());
        
        mockInterpreter = Interpreter.builder()
                .id(UUID.randomUUID())
                .build();
    }

    @Test
    @DisplayName("Deve criar appointment com construtor completo")
    void shouldCreateAppointmentWithFullConstructor() {
        String uf = "SP";
        String city = "São Paulo";
        AppointmentModality modality = AppointmentModality.ONLINE;
        LocalDate date = LocalDate.now().plusDays(1);
        String description = "Consulta médica";
        AppointmentStatus status = AppointmentStatus.PENDING;
        LocalTime startTime = LocalTime.of(14, 0);
        LocalTime endTime = LocalTime.of(15, 0);

        appointment = new Appointment(uf, city, modality, date, description, status, 
                                    mockInterpreter, mockUser, startTime, endTime);

        assertNotNull(appointment);
        assertEquals(uf, appointment.getUf());
        assertEquals(city, appointment.getCity());
        assertEquals(modality, appointment.getModality());
        assertEquals(date, appointment.getDate());
        assertEquals(description, appointment.getDescription());
        assertEquals(status, appointment.getStatus());
        assertEquals(mockInterpreter, appointment.getInterpreter());
        assertEquals(mockUser, appointment.getUser());
        assertEquals(startTime, appointment.getStartTime());
        assertEquals(endTime, appointment.getEndTime());
    }

    @Test
    @DisplayName("Deve criar appointment com builder pattern")
    void shouldCreateAppointmentWithBuilder() {
        appointment = Appointment.builder()
                .uf("RJ")
                .city("Rio de Janeiro")
                .neighborhood("Copacabana")
                .street("Av. Atlântica")
                .streetNumber(1000)
                .addressDetails("Apto 101")
                .modality(AppointmentModality.PERSONALLY)
                .date(LocalDate.now().plusDays(2))
                .description("Atendimento presencial")
                .status(AppointmentStatus.ACCEPTED)
                .interpreter(mockInterpreter)
                .user(mockUser)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 30))
                .build();

        assertNotNull(appointment);
        assertEquals("RJ", appointment.getUf());
        assertEquals("Rio de Janeiro", appointment.getCity());
        assertEquals("Copacabana", appointment.getNeighborhood());
        assertEquals("Av. Atlântica", appointment.getStreet());
        assertEquals(1000, appointment.getStreetNumber());
        assertEquals("Apto 101", appointment.getAddressDetails());
        assertEquals(AppointmentModality.PERSONALLY, appointment.getModality());
        assertEquals(AppointmentStatus.ACCEPTED, appointment.getStatus());
        assertEquals(mockInterpreter, appointment.getInterpreter());
        assertEquals(mockUser, appointment.getUser());
    }

    @Test
    @DisplayName("Deve permitir modificação de campos via setters")
    void shouldAllowFieldModificationViaSetters() {
        appointment = Appointment.builder()
                .modality(AppointmentModality.ONLINE)
                .status(AppointmentStatus.PENDING)
                .build();

        appointment.setModality(AppointmentModality.PERSONALLY);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setDescription("Descrição atualizada");
        appointment.setUf("MG");
        appointment.setCity("Belo Horizonte");

        assertEquals(AppointmentModality.PERSONALLY, appointment.getModality());
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
        assertEquals("Descrição atualizada", appointment.getDescription());
        assertEquals("MG", appointment.getUf());
        assertEquals("Belo Horizonte", appointment.getCity());
    }

    @Test
    @DisplayName("Deve permitir appointment sem endereço completo para modalidade online")
    void shouldAllowAppointmentWithoutFullAddressForOnlineModality() {
        appointment = Appointment.builder()
                .modality(AppointmentModality.ONLINE)
                .date(LocalDate.now().plusDays(1))
                .description("Atendimento online")
                .status(AppointmentStatus.PENDING)
                .interpreter(mockInterpreter)
                .user(mockUser)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .build();

        assertNotNull(appointment);
        assertEquals(AppointmentModality.ONLINE, appointment.getModality());
        assertNull(appointment.getStreet());
        assertNull(appointment.getStreetNumber());
        assertNull(appointment.getNeighborhood());
    }

    @Test
    @DisplayName("Deve validar que appointment tem interpretador e usuário")
    void shouldValidateAppointmentHasInterpreterAndUser() {
        appointment = Appointment.builder()
                .modality(AppointmentModality.ONLINE)
                .date(LocalDate.now().plusDays(1))
                .description("Teste")
                .status(AppointmentStatus.PENDING)
                .interpreter(mockInterpreter)
                .user(mockUser)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(15, 0))
                .build();

        assertNotNull(appointment.getInterpreter());
        assertNotNull(appointment.getUser());
        assertEquals(mockInterpreter.getId(), appointment.getInterpreter().getId());
        assertEquals(mockUser.getId(), appointment.getUser().getId());
    }

    @Test
    @DisplayName("Deve permitir horários de início e fim diferentes")
    void shouldAllowDifferentStartAndEndTimes() {
        LocalTime startTime = LocalTime.of(9, 30);
        LocalTime endTime = LocalTime.of(11, 45);

        appointment = Appointment.builder()
                .startTime(startTime)
                .endTime(endTime)
                .modality(AppointmentModality.PERSONALLY)
                .date(LocalDate.now().plusDays(1))
                .description("Teste de horários")
                .status(AppointmentStatus.PENDING)
                .interpreter(mockInterpreter)
                .user(mockUser)
                .build();

        assertEquals(startTime, appointment.getStartTime());
        assertEquals(endTime, appointment.getEndTime());
        assertTrue(appointment.getStartTime().isBefore(appointment.getEndTime()));
    }

    @Test
    @DisplayName("Deve permitir criar appointment no construtor padrão")
    void shouldCreateAppointmentWithDefaultConstructor() {
        appointment = new Appointment();

        assertNotNull(appointment);
        assertNull(appointment.getId());
        assertNull(appointment.getModality());
        assertNull(appointment.getStatus());
    }
}