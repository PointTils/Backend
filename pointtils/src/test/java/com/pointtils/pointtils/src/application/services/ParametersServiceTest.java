package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.ParametersBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ParametersPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ParametersResponseDTO;
import com.pointtils.pointtils.src.application.mapper.ParametersMapper;
import com.pointtils.pointtils.src.core.domain.entities.Parameters;
import com.pointtils.pointtils.src.infrastructure.repositories.ParametersRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParametersServiceTest {

    @Mock
    private ParametersRepository repository;

    @Mock
    private ParametersMapper mapper;

    @InjectMocks
    private ParametersService parametersService;

    private Parameters parameters;
    private ParametersBasicRequestDTO basicRequestDTO;
    private ParametersPatchRequestDTO patchRequestDTO;
    private ParametersResponseDTO responseDTO;
    private UUID parametersId;

    @BeforeEach
    void setUp() {
        parametersId = UUID.randomUUID();
        
        parameters = Parameters.builder()
                .id(parametersId)
                .key("test.key")
                .value("test.value")
                .build();

        basicRequestDTO = new ParametersBasicRequestDTO();
        basicRequestDTO.setKey("test.key");
        basicRequestDTO.setValue("test.value");

        patchRequestDTO = new ParametersPatchRequestDTO();
        patchRequestDTO.setKey("updated.key");
        patchRequestDTO.setValue("updated.value");

        responseDTO = ParametersResponseDTO.builder()
                .id(parametersId)
                .key("test.key")
                .value("test.value")
                .build();
    }

    @Test
    @DisplayName("Deve criar parâmetro com sucesso")
    void createSuccess() {
        when(repository.existsByKey("test.key")).thenReturn(false);
        when(mapper.toEntity(basicRequestDTO)).thenReturn(parameters);
        when(repository.save(any(Parameters.class))).thenReturn(parameters);
        when(mapper.toResponseDTO(parameters)).thenReturn(responseDTO);

        ParametersResponseDTO result = parametersService.create(basicRequestDTO);

        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(responseDTO.getKey(), result.getKey());
        assertEquals(responseDTO.getValue(), result.getValue());
        
        verify(repository).existsByKey("test.key");
        verify(mapper).toEntity(basicRequestDTO);
        verify(repository).save(any(Parameters.class));
        verify(mapper).toResponseDTO(parameters);
    }

    @Test
    @DisplayName("Deve falhar ao criar parâmetro com chave duplicada")
    void createFailure() {
        when(repository.existsByKey("test.key")).thenReturn(true);

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> parametersService.create(basicRequestDTO)
        );

        assertEquals("Já existe um parâmetro cadastrado com esta chave", exception.getMessage());
        
        verify(repository).existsByKey("test.key");
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Deve buscar todos os parâmetros")
    void findAllSuccess() {
        List<Parameters> parametersList = List.of(parameters);
        
        when(repository.findAll()).thenReturn(parametersList);
        when(mapper.toResponseDTO(parameters)).thenReturn(responseDTO);

        List<ParametersResponseDTO> result = parametersService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseDTO.getId(), result.get(0).getId());
        assertEquals(responseDTO.getKey(), result.get(0).getKey());
        assertEquals(responseDTO.getValue(), result.get(0).getValue());
        
        verify(repository).findAll();
        verify(mapper).toResponseDTO(parameters);
    }

    @Test
    @DisplayName("Deve buscar parâmetro por chave com sucesso")
    void findByKeySuccess() {
        when(repository.findByKey("test.key")).thenReturn(Optional.of(parameters));
        when(mapper.toResponseDTO(parameters)).thenReturn(responseDTO);

        ParametersResponseDTO result = parametersService.findByKey("test.key");

        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(responseDTO.getKey(), result.getKey());
        assertEquals(responseDTO.getValue(), result.getValue());
        
        verify(repository).findByKey("test.key");
        verify(mapper).toResponseDTO(parameters);
    }

    @Test
    @DisplayName("Deve falhar ao buscar parâmetro por chave inexistente")
    void findByKeyFailure() {
        when(repository.findByKey("inexistente.key")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> parametersService.findByKey("inexistente.key")
        );

        assertEquals("Parâmetro não encontrado", exception.getMessage());
        
        verify(repository).findByKey("inexistente.key");
        verify(mapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Deve atualizar parâmetro com sucesso")
    void putSuccess() {
        Parameters updatedParameters = Parameters.builder()
                .id(parametersId)
                .key("updated.key")
                .value("updated.value")
                .build();

        ParametersResponseDTO updatedResponseDTO = ParametersResponseDTO.builder()
                .id(parametersId)
                .key("updated.key")
                .value("updated.value")
                .build();

        when(repository.findById(parametersId)).thenReturn(Optional.of(parameters));
        when(repository.save(any(Parameters.class))).thenReturn(updatedParameters);
        when(mapper.toResponseDTO(updatedParameters)).thenReturn(updatedResponseDTO);

        ParametersResponseDTO result = parametersService.put(parametersId, patchRequestDTO);

        assertNotNull(result);
        assertEquals(updatedResponseDTO.getId(), result.getId());
        assertEquals(updatedResponseDTO.getKey(), result.getKey());
        assertEquals(updatedResponseDTO.getValue(), result.getValue());
        
        verify(repository).findById(parametersId);
        verify(repository).save(any(Parameters.class));
        verify(mapper).toResponseDTO(updatedParameters);
    }

    @Test
    @DisplayName("Deve atualizar parâmetro parcialmente com campos nulos")
    void putPartiallySuccess() {
        ParametersPatchRequestDTO emptyPatchDTO = new ParametersPatchRequestDTO();

        when(repository.findById(parametersId)).thenReturn(Optional.of(parameters));
        when(repository.save(any(Parameters.class))).thenReturn(parameters);
        when(mapper.toResponseDTO(parameters)).thenReturn(responseDTO);

        ParametersResponseDTO result = parametersService.put(parametersId, emptyPatchDTO);

        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(responseDTO.getKey(), result.getKey());
        assertEquals(responseDTO.getValue(), result.getValue());
        
        verify(repository).findById(parametersId);
        verify(repository).save(any(Parameters.class));
        verify(mapper).toResponseDTO(parameters);
    }

    @Test
    @DisplayName("Deve falhar ao atualizar parâmetro inexistente")
    void putFailure() {
        when(repository.findById(parametersId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> parametersService.put(parametersId, patchRequestDTO)
        );

        assertEquals("Parâmetro não encontrado", exception.getMessage());
        
        verify(repository).findById(parametersId);
        verify(repository, never()).save(any());
        verify(mapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Deve deletar parâmetro com sucesso")
    void deleteSuccess() {
        doNothing().when(repository).deleteById(parametersId);

        assertDoesNotThrow(() -> parametersService.delete(parametersId));
        
        verify(repository).deleteById(parametersId);
    }

    @Test
    @DisplayName("Deve atualizar apenas o campo key quando value for nulo")
    void putKeyOnlySuccess() {
        ParametersPatchRequestDTO keyOnlyPatchDTO = new ParametersPatchRequestDTO();
        keyOnlyPatchDTO.setKey("updated.key");
        keyOnlyPatchDTO.setValue(null);

        Parameters updatedParameters = Parameters.builder()
                .id(parametersId)
                .key("updated.key")
                .value("test.value") 
                .build();

        ParametersResponseDTO updatedResponseDTO = ParametersResponseDTO.builder()
                .id(parametersId)
                .key("updated.key")
                .value("test.value")
                .build();

        when(repository.findById(parametersId)).thenReturn(Optional.of(parameters));
        when(repository.save(any(Parameters.class))).thenReturn(updatedParameters);
        when(mapper.toResponseDTO(updatedParameters)).thenReturn(updatedResponseDTO);

        ParametersResponseDTO result = parametersService.put(parametersId, keyOnlyPatchDTO);

        assertNotNull(result);
        assertEquals("updated.key", result.getKey());
        assertEquals("test.value", result.getValue()); 
        
        verify(repository).findById(parametersId);
        verify(repository).save(any(Parameters.class));
        verify(mapper).toResponseDTO(updatedParameters);
    }

    @Test
    @DisplayName("Deve atualizar apenas o campo value quando key for nulo")
    void putValueOnlySuccess() {
        ParametersPatchRequestDTO valueOnlyPatchDTO = new ParametersPatchRequestDTO();
        valueOnlyPatchDTO.setKey(null);
        valueOnlyPatchDTO.setValue("updated.value");

        Parameters updatedParameters = Parameters.builder()
                .id(parametersId)
                .key("test.key") 
                .value("updated.value")
                .build();

        ParametersResponseDTO updatedResponseDTO = ParametersResponseDTO.builder()
                .id(parametersId)
                .key("test.key")
                .value("updated.value")
                .build();

        when(repository.findById(parametersId)).thenReturn(Optional.of(parameters));
        when(repository.save(any(Parameters.class))).thenReturn(updatedParameters);
        when(mapper.toResponseDTO(updatedParameters)).thenReturn(updatedResponseDTO);

        ParametersResponseDTO result = parametersService.put(parametersId, valueOnlyPatchDTO);

        assertNotNull(result);
        assertEquals("test.key", result.getKey()); 
        assertEquals("updated.value", result.getValue());
        
        verify(repository).findById(parametersId);
        verify(repository).save(any(Parameters.class));
        verify(mapper).toResponseDTO(updatedParameters);
    }
}
