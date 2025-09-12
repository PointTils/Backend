package com.pointtils.pointtils.src.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.application.dto.requests.DeafRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.PersonalRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.DeafResponseDTO;
import com.pointtils.pointtils.src.application.services.DeafRegisterService;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.infrastructure.configs.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DeafControllerTest {

    @Mock
    private DeafRegisterService deafRegisterService;
    @Spy
    private ObjectMapper objectMapper;
    @InjectMocks
    private DeafController deafController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(deafController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve retornar 201 quando criar usuário surdo com sucesso")
    void shouldCreateDeafUserSuccessfully() throws Exception {
        DeafResponseDTO expectedResponse = buildDtoResponse();
        when(deafRegisterService.registerPerson(any())).thenReturn(expectedResponse);

        mockMvc.perform(post("/v1/deaf-users/register")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDtoRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuário surdo cadastrado com sucesso"))
                .andExpect(jsonPath("$.data.email").value("senhor.mock@gmail.com"))
                .andExpect(jsonPath("$.data.cpf").value("11122233344"))
                .andExpect(jsonPath("$.data.picture").value("image"))
                .andExpect(jsonPath("$.data.name").value("Senhor Mock"))
                .andExpect(jsonPath("$.data.gender").value("M"))
                .andExpect(jsonPath("$.data.phone").value("5142424242"))
                .andExpect(jsonPath("$.data.location.uf").value("RS"))
                .andExpect(jsonPath("$.data.location.city").value("Porto Alegre"));

    }

    private DeafRequestDTO buildDtoRequest() {
        DeafRequestDTO request = new DeafRequestDTO();
        PersonalRequestDTO personalRequest = new PersonalRequestDTO();
        personalRequest.setEmail("senhor.mock@gmail.com");
        personalRequest.setPassword("minhasenha123");
        personalRequest.setPicture("image");
        personalRequest.setCpf("11122233344");
        personalRequest.setName("Senhor Mock");
        personalRequest.setGender("M");
        personalRequest.setPhone("5142424242");
        personalRequest.setBirthday(LocalDate.of(2000, 1, 22));
        request.setPersonalRequestDTO(personalRequest);
        request.setLocation(LocationDTO.builder()
                .uf("RS")
                .city("Porto Alegre")
                .build());
        return request;
    }

    private DeafResponseDTO buildDtoResponse() {
        DeafResponseDTO response = new DeafResponseDTO();
        response.setId(UUID.randomUUID());
        response.setEmail("senhor.mock@gmail.com");
        response.setPicture("image");
        response.setCpf("11122233344");
        response.setName("Senhor Mock");
        response.setGender("M");
        response.setPhone("5142424242");
        response.setBirthday(LocalDate.of(2000, 1, 22));
        response.setStatus(UserStatus.ACTIVE.name());
        response.setType(UserTypeE.CLIENT.name());
        response.setLocation(LocationDTO.builder()
                .uf("RS")
                .city("Porto Alegre")
                .build());
        return response;
    }
}