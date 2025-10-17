package com.pointtils.pointtils.src.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointtils.pointtils.src.application.dto.requests.ParametersBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ParametersPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ParametersResponseDTO;
import com.pointtils.pointtils.src.infrastructure.configs.GlobalExceptionHandler;
import com.pointtils.pointtils.src.application.services.ParametersService;
import com.pointtils.pointtils.src.core.domain.entities.Parameters;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.pointtils.pointtils.src.infrastructure.configs.GlobalExceptionHandler;


import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ParametersControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ParametersService parametersService;

    @InjectMocks
    private ParametersController parametersController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Parameters parameters;
    private UUID parametersId;

    @BeforeEach
    void setUp() {
        parametersId = UUID.randomUUID();
        parameters = new Parameters(parametersId, "Test Key", "Test Value");
        parameters.setId(parametersId);

        mockMvc = MockMvcBuilders.standaloneSetup(parametersController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
    @Test
    @DisplayName("Deve cadastrar um Parameters com sucesso")
    void createSucess() throws Exception{

        ParametersBasicRequestDTO dto = new ParametersBasicRequestDTO("Test Key", "Test Value");
        ParametersResponseDTO responseDTO = new ParametersResponseDTO(parametersId, "Test Key", "Test Value");

        when(parametersService.create(any(ParametersBasicRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/v1/parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Parâmetro cadastrado com sucesso"))
                .andExpect(jsonPath("$.data.id").value(parametersId.toString()))
                .andExpect(jsonPath("$.data.key").value("Test Key"))
                .andExpect(jsonPath("$.data.value").value("Test Value"));


    }

    @Test
    @DisplayName("Deve listar todos os Parameters com sucesso")
    void findAllSucess() throws Exception{
    
        ParametersResponseDTO responseDTO = new ParametersResponseDTO();
        when(parametersService.findAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/v1/parameters")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Parâmetros listados com sucesso"))
                .andExpect(jsonPath("$.data[0].id").value(parametersId.toString()))
                .andExpect(jsonPath("$.data[0].key").value("Test Key"))
                .andExpect(jsonPath("$.data[0].value").value("Test Value"));
    }

    @Test
    @DisplayName("Deve buscar um Parameters por key com sucesso")
    void findByKeySucess() throws Exception{
        String key = "Test Key";
        ParametersResponseDTO responseDTO = new ParametersResponseDTO(parametersId, key, "Test Value");

        when(parametersService.findByKey(key)).thenReturn(responseDTO);

        mockMvc.perform(get("/v1/parameters/{key}", key)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Parâmetro encontrado com sucesso"))
                .andExpect(jsonPath("$.data.id").value(parametersId.toString()))
                .andExpect(jsonPath("$.data.key").value("Test Key"))
                .andExpect(jsonPath("$.data.value").value("Test Value"));
    }

    @Test
    @DisplayName("Deve atualizar um Parameters com sucesso")
    void putSucess() throws Exception{
        ParametersPatchRequestDTO dto = new ParametersPatchRequestDTO("Updated Value");
        ParametersResponseDTO responseDTO = new ParametersResponseDTO(parametersId, "Test Key", "Updated Value");

        when(parametersService.put(any(UUID.class), any(ParametersPatchRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/v1/parameters/{id}", parametersId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Parâmetro atualizado com sucesso"))
                .andExpect(jsonPath("$.data.id").value(parametersId.toString()))
                .andExpect(jsonPath("$.data.key").value("Test Key"))
                .andExpect(jsonPath("$.data.value").value("Updated Value"));
    }
    @Test
    @DisplayName("Deve deletar um Parameters com sucesso")
    void deleteSucess() throws Exception{
        doNothing().when(parametersService).delete(parametersId);
        mockMvc.perform(delete("/v1/parameters/{id}", parametersId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}