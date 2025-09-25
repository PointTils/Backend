package com.pointtils.pointtils.src.application.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.LocationRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ProfessionalDataBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ProfessionalDataPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.mapper.InterpreterResponseMapper;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class InterpreterServiceTest {

    @Mock
    private InterpreterRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private InterpreterResponseMapper responseMapper;
    @InjectMocks
    private InterpreterService service;

    @Test
    void shouldRegisterNewInterpreter() {
        Interpreter interpreter = new Interpreter();
        InterpreterResponseDTO mappedResponse = new InterpreterResponseDTO();
        ArgumentCaptor<Interpreter> interpreterArgumentCaptor = ArgumentCaptor.forClass(Interpreter.class);
        when(repository.save(interpreterArgumentCaptor.capture())).thenReturn(interpreter);
        when(responseMapper.toResponseDTO(interpreter)).thenReturn(mappedResponse);
        when(passwordEncoder.encode("senha123")).thenReturn("hashedPassword");

        assertEquals(mappedResponse, service.registerBasic(createValidBasicRequest()));
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
        assertEquals(BigDecimal.ZERO, interpreterArgumentCaptor.getValue().getMinValue());
        assertEquals(BigDecimal.ZERO, interpreterArgumentCaptor.getValue().getMaxValue());
        assertEquals("", interpreterArgumentCaptor.getValue().getDescription());
        assertEquals("ALL", interpreterArgumentCaptor.getValue().getModality().name());
        assertFalse(interpreterArgumentCaptor.getValue().getImageRights());
    }

    @Test
    void shouldFindAll() {
        UUID id = UUID.randomUUID();
        Interpreter foundInterpreter = Interpreter.builder().id(id).build();
        InterpreterResponseDTO mappedResponse = InterpreterResponseDTO.builder().id(id).build();
        when(repository.findAll(any(Specification.class))).thenReturn(List.of(foundInterpreter));
        when(responseMapper.toResponseDTO(foundInterpreter)).thenReturn(mappedResponse);

        assertThat(service.findAll(
                null,
                null,
                null,
                null,
                null,
                null,
                null))
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

        assertEquals(mappedResponse, service.updatePartial(interpreterId, createValidPatchRequest()));
        assertEquals("Novo Nome", interpreterArgumentCaptor.getValue().getName());
        assertEquals("novo.nome@email.com", interpreterArgumentCaptor.getValue().getEmail());
        assertEquals("51988888888", interpreterArgumentCaptor.getValue().getPhone());
        assertEquals(Gender.FEMALE, interpreterArgumentCaptor.getValue().getGender());
        assertEquals("98765432000196", interpreterArgumentCaptor.getValue().getCnpj());
        assertEquals(InterpreterModality.ONLINE, interpreterArgumentCaptor.getValue().getModality());
        assertEquals(250, interpreterArgumentCaptor.getValue().getMinValue().doubleValue());
        assertEquals(500, interpreterArgumentCaptor.getValue().getMaxValue().doubleValue());
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

    private InterpreterBasicRequestDTO createValidBasicRequest() {
        InterpreterBasicRequestDTO request = new InterpreterBasicRequestDTO();
        request.setName("João Intérprete");
        request.setEmail("interpreter@exemplo.com");
        request.setPassword("senha123");
        request.setPhone("51999999999");
        request.setGender(Gender.MALE);
        request.setBirthday(LocalDate.of(1990, 1, 1));
        request.setCpf("12345678901");
        request.setPicture("picture_url");
        request.setProfessionalData(new ProfessionalDataBasicRequestDTO("12345678000195",
                new BigDecimal("100.00"),
                new BigDecimal("500.00"),
                true,
                InterpreterModality.PERSONALLY,
                "Intérprete experiente em LIBRAS"));
        return request;
    }

    private InterpreterPatchRequestDTO createValidPatchRequest() {
        InterpreterPatchRequestDTO requestDTO = createLocationPatchRequest();
        requestDTO.setName("Novo Nome");
        requestDTO.setEmail("novo.nome@email.com");
        requestDTO.setGender(Gender.FEMALE);
        requestDTO.setPicture("nova foto");
        requestDTO.setPhone("51988888888");
        requestDTO.setBirthday(LocalDate.of(2000, 5, 23));
        requestDTO.setProfessionalData(createValidProfessionalDataPatchRequest());
        return requestDTO;
    }

    private InterpreterPatchRequestDTO createLocationPatchRequest() {
        InterpreterPatchRequestDTO requestDTO = new InterpreterPatchRequestDTO();
        requestDTO.setLocations(List.of(new LocationRequestDTO("SP", "São Paulo", "Higienópolis")));
        return requestDTO;
    }

    private ProfessionalDataPatchRequestDTO createValidProfessionalDataPatchRequest() {
        ProfessionalDataPatchRequestDTO professionalData = new ProfessionalDataPatchRequestDTO();
        professionalData.setCnpj("98765432000196");
        professionalData.setDescription("Teste");
        professionalData.setImageRights(Boolean.FALSE);
        professionalData.setMinValue(BigDecimal.valueOf(250));
        professionalData.setMaxValue(BigDecimal.valueOf(500));
        professionalData.setModality(InterpreterModality.ONLINE);
        return professionalData;
    }
}
