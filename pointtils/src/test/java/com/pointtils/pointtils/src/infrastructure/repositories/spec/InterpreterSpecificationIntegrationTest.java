package com.pointtils.pointtils.src.infrastructure.repositories.spec;

import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.core.domain.entities.Specialty;
import com.pointtils.pointtils.src.core.domain.entities.UserSpecialty;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.infrastructure.repositories.AppointmentRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.ScheduleRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.SpecialtyRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserSpecialtyRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class InterpreterSpecificationIntegrationTest {

    @Autowired
    private InterpreterRepository interpreterRepository;
    @Autowired
    private SpecialtyRepository specialtyRepository;
    @Autowired
    private UserSpecialtyRepository userSpecialtyRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    @AfterEach
    void clearDatabase() {
        interpreterRepository.deleteAll();
    }

    @Test
    void shouldFilterInterpretersByName() {
        Interpreter interpreter = buildInterpreter();
        interpreterRepository.save(interpreter);

        Specification<Interpreter> spec = InterpreterSpecification.filter(
                null, null, null, null, null, null, null, "Roberto"
        );

        List<Interpreter> result = interpreterRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Carlos Roberto Júnior");
    }

    @ParameterizedTest
    @CsvSource(value = {
            "ONLINE,ONLINE",
            "ALL,ONLINE",
            "PERSONALLY,PERSONALLY",
            "ALL,PERSONALLY",
            "ONLINE,ALL",
            "PERSONALLY,ALL",
            "ALL,ALL"
    })
    void shouldFilterInterpretersByModality(String interpreterModality, String modalityToFilterBy) {
        Interpreter interpreter = buildInterpreter();
        InterpreterModality convertedInterpreterModality = InterpreterModality.fromString(interpreterModality);
        interpreter.setModality(convertedInterpreterModality);
        interpreterRepository.save(interpreter);

        Specification<Interpreter> spec = InterpreterSpecification.filter(
                InterpreterModality.fromString(modalityToFilterBy), null, null, null, null, null, null, null
        );

        List<Interpreter> result = interpreterRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Carlos Roberto Júnior");
        assertThat(result.get(0).getModality()).isEqualTo(convertedInterpreterModality);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "ONLINE,PERSONALLY",
            "PERSONALLY,ONLINE"
    })
    void shouldFilterZeroInterpretersByModality(String interpreterModality, String modalityToFilterBy) {
        Interpreter interpreter = buildInterpreter();
        InterpreterModality convertedInterpreterModality = InterpreterModality.fromString(interpreterModality);
        interpreter.setModality(convertedInterpreterModality);
        interpreterRepository.save(interpreter);

        Specification<Interpreter> spec = InterpreterSpecification.filter(
                InterpreterModality.fromString(modalityToFilterBy), null, null, null, null, null, null, null
        );

        List<Interpreter> result = interpreterRepository.findAll(spec);
        assertEquals(0, result.size());
    }

    @Test
    void shouldFilterInterpretersByLocation() {
        Interpreter interpreter = buildInterpreter();
        Location location = buildLocation(interpreter);
        interpreter.setLocations(List.of(location));
        interpreterRepository.save(interpreter);

        Specification<Interpreter> spec = InterpreterSpecification.filter(
                null, "RS", "Porto Alegre", "Auxiliadora", null, null, null, null
        );

        List<Interpreter> result = interpreterRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Carlos Roberto Júnior");
        assertThat(result.get(0).getLocations().get(0).getUf()).isEqualTo("RS");
        assertThat(result.get(0).getLocations().get(0).getCity()).isEqualTo("Porto Alegre");
        assertThat(result.get(0).getLocations().get(0).getNeighborhood()).isEqualTo("Auxiliadora");
    }

    @Test
    void shouldFilterInterpretersByGender() {
        Interpreter interpreter = buildInterpreter();
        interpreterRepository.save(interpreter);

        Specification<Interpreter> spec = InterpreterSpecification.filter(
                null, null, null, null, null, Gender.MALE, null, null
        );

        List<Interpreter> result = interpreterRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Carlos Roberto Júnior");
    }

    @Test
    void shouldFilterInterpretersByMultipleSpecialties() {
        Interpreter interpreter = buildInterpreter();
        interpreter = interpreterRepository.save(interpreter);

        Specialty firstSpecialty = new Specialty("Intérprete Tátil");
        firstSpecialty = specialtyRepository.save(firstSpecialty);
        Specialty secondSpecialty = new Specialty("Intérprete de Libras");
        secondSpecialty = specialtyRepository.save(secondSpecialty);

        userSpecialtyRepository.save(new UserSpecialty(firstSpecialty, interpreter));
        userSpecialtyRepository.save(new UserSpecialty(secondSpecialty, interpreter));

        Specification<Interpreter> spec = InterpreterSpecification.filter(
                null, null, null, null, List.of(firstSpecialty.getId(), secondSpecialty.getId()), null, null, null
        );

        List<Interpreter> result = interpreterRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Carlos Roberto Júnior");
    }

    @ParameterizedTest
    @CsvSource(value = {
            "10:00,11:00,10:00", //requested time starts when appointment starts
            "10:00,11:00,10:30", //requested time starts in the middle of the appointment
            "10:00,11:00,09:30", //requested time ends in the middle of the appointment
            "10:40,10:50,10:00", //requested time envelops the appointment
    })
    @Disabled
    void shouldFilterZeroInterpretersIfDateTimeConflictsWithAppointment(String appointmentStartTime,
                                                                        String appointmentEndTime,
                                                                        String requestedTime) {
        Interpreter interpreter = buildInterpreter();
        interpreter = interpreterRepository.save(interpreter);

        Schedule schedule = buildSchedule(interpreter);
        scheduleRepository.save(schedule);

        LocalDate date = LocalDate.of(2025, 10, 6);
        Appointment appointment = buildAppointment(interpreter, date, LocalTime.parse(appointmentStartTime), LocalTime.parse(appointmentEndTime));
        appointmentRepository.save(appointment);

        LocalDateTime requestedDateTime = LocalDateTime.of(date, LocalTime.parse(requestedTime));
        Specification<Interpreter> spec = InterpreterSpecification.filter(
                null, null, null, null, null, null, requestedDateTime, null
        );

        List<Interpreter> result = interpreterRepository.findAll(spec);
        assertEquals(0, result.size());
    }

    private Interpreter buildInterpreter() {
        return Interpreter.builder()
                .name("Carlos Roberto Júnior")
                .cpf("11122233344")
                .gender(Gender.MALE)
                .birthday(LocalDate.of(1990, 1, 1))
                .email("carlos.roberto@email.com")
                .password("password")
                .phone("54987548754")
                .status(UserStatus.ACTIVE)
                .type(UserTypeE.INTERPRETER)
                .build();
    }

    private Location buildLocation(Interpreter interpreter) {
        Location location = new Location();
        location.setUf("RS");
        location.setCity("Porto Alegre");
        location.setNeighborhood("Auxiliadora");
        location.setInterpreter(interpreter);
        return location;
    }

    private Schedule buildSchedule(Interpreter interpreter) {
        return Schedule.builder()
                .day(DayOfWeek.MON)
                .startTime(LocalTime.of(8, 0))
                .startTime(LocalTime.of(20, 0))
                .interpreter(interpreter)
                .build();
    }

    private Appointment buildAppointment(Interpreter interpreter, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return Appointment.builder()
                .modality(AppointmentModality.ONLINE)
                .status(AppointmentStatus.ACCEPTED)
                .description("Appointment")
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .user(interpreter)
                .interpreter(interpreter)
                .build();
    }
}