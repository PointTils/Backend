package com.pointtils.pointtils.src.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ProfessionalPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.ProfessionalDataResponseDTO;
import com.pointtils.pointtils.src.application.services.InterpreterService;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class InterpreterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InterpreterService interpreterService;

    @Test
    @DisplayName("Deve cadastrar intérprete com sucesso usando dados básicos")
    void deveCadastrarInterpreterComSucessoUsandoDadosBasicos() throws Exception {
        // Arrange
        InterpreterBasicRequestDTO request = createValidBasicRequest();
        InterpreterResponseDTO mockResponse = createMockResponse();

        when(interpreterService.registerBasic(any(InterpreterBasicRequestDTO.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/interpreters/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Intérprete cadastrado com sucesso"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value("interpreter@exemplo.com"))
                .andExpect(jsonPath("$.data.name").value("João Intérprete"))
                .andExpect(jsonPath("$.data.type").value("interpreter"))
                .andExpect(jsonPath("$.data.status").value("pending"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando dados pessoais não forem fornecidos")
    void deveRetornar400QuandoDadosPessoaisNaoFornecidos() throws Exception {
        // Arrange
        InterpreterBasicRequestDTO request = createValidBasicRequest();
        request.setName(null);

        // Act & Assert
        mockMvc.perform(post("/v1/interpreters/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados inválidos: [Nome deve ser preenchido]"));
    }

    @Test
    @DisplayName("Deve retornar 422 quando email for inválido")
    void deveRetornar422QuandoEmailForInvalido() throws Exception {
        // Arrange
        InterpreterBasicRequestDTO request = createValidBasicRequest();
        request.setEmail("email-invalido");

        // Act & Assert
        mockMvc.perform(post("/v1/interpreters/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Dados inválidos: [Email inválido]"));
    }

    @Test
    @DisplayName("Deve retornar 422 quando CPF for inválido")
    void deveRetornar422QuandoCpfForInvalido() throws Exception {
        // Arrange
        InterpreterBasicRequestDTO request = createValidBasicRequest();
        request.setCpf("123"); // CPF inválido

        // Act & Assert
        mockMvc.perform(post("/v1/interpreters/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Deve completar dados profissionais de intérprete após cadastro inicial")
    void deveCompletarDadosProfissionaisAposCadastroInicial() throws Exception {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        InterpreterPatchRequestDTO patchRequest = new InterpreterPatchRequestDTO();

        ProfessionalPatchRequestDTO professionalData = ProfessionalPatchRequestDTO.builder()
                .cnpj("12345678000195")
                .minValue(new BigDecimal("100.00"))
                .maxValue(new BigDecimal("500.00"))
                .imageRights(true)
                .modality("presencial")
                .description("Intérprete experiente em LIBRAS")
                .build();

        patchRequest.setProfessionalData(professionalData);

        InterpreterResponseDTO mockResponse = createMockResponseWithProfessionalData();

        when(interpreterService.updatePartial(any(UUID.class), any(InterpreterPatchRequestDTO.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(patch("/v1/interpreters/{id}", interpreterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Intérprete atualizado com sucesso"))
                .andExpect(jsonPath("$.data.professional_data.cnpj").value("12345678000195"))
                .andExpect(jsonPath("$.data.professional_data.min_value").value(100.00))
                .andExpect(jsonPath("$.data.professional_data.max_value").value(500.00))
                .andExpect(jsonPath("$.data.professional_data.image_rights").value(true))
                .andExpect(jsonPath("$.data.professional_data.modality").value("presencial"))
                .andExpect(jsonPath("$.data.professional_data.description").value("Intérprete experiente em LIBRAS"));
    }

    private InterpreterBasicRequestDTO createValidBasicRequest() {
        InterpreterBasicRequestDTO request = new InterpreterBasicRequestDTO();
        request.setName("João Intérprete");
        request.setEmail("interpreter@exemplo.com");
        request.setPassword("senha123");
        request.setPhone("51999999999");
        request.setGender("M");
        request.setBirthday(LocalDate.of(1990, 1, 1));
        request.setCpf("12345678901");
        request.setPicture("picture_url");
        request.setCnpj("12345678000195");
        return request;
    }

    private InterpreterResponseDTO createMockResponse() {
        ProfessionalDataResponseDTO professionalInfo = ProfessionalDataResponseDTO.builder()
                .cnpj(null)
                .rating(new BigDecimal("0.0"))
                .minValue(new BigDecimal("0.0"))
                .maxValue(new BigDecimal("0.0"))
                .imageRights(false)
                .modality(null)
                .description(null)
                .build();

        return InterpreterResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("interpreter@exemplo.com")
                .type("interpreter")
                .status("pending")
                .phone("51999999999")
                .picture("picture_url")
                .name("João Intérprete")
                .gender(Gender.MALE)
                .birthday(LocalDate.of(1990, 1, 1))
                .cpf("12345678901")
                .locations(List.of(new LocationDTO(UUID.randomUUID(), "RS", "Porto Alegre", "São João")))
                .specialties(Collections.emptyList())
                .professionalData(professionalInfo)
                .build();
    }

    private InterpreterResponseDTO createMockResponseWithProfessionalData() {
        ProfessionalDataResponseDTO professionalInfo = ProfessionalDataResponseDTO.builder()
                .cnpj("12345678000195")
                .rating(new BigDecimal("0.0"))
                .minValue(new BigDecimal("100.00"))
                .maxValue(new BigDecimal("500.00"))
                .imageRights(true)
                .modality("presencial")
                .description("Intérprete experiente em LIBRAS")
                .build();

        return InterpreterResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("interpreter@exemplo.com")
                .type("interpreter")
                .status("pending")
                .phone("51999999999")
                .picture("picture_url")
                .name("João Intérprete")
                .gender(Gender.MALE)
                .birthday(LocalDate.of(1990, 1, 1))
                .cpf("12345678901")
                .locations(List.of(new LocationDTO(UUID.randomUUID(), "RS", "Porto Alegre", "São João")))
                .specialties(Collections.emptyList())
                .professionalData(professionalInfo)
                .build();
    }
}
