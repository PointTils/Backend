package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.mapper.InterpreterResponseMapper;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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
}
