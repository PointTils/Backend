package com.pointtils.pointtils.src.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ProfessionalDataPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterListResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.services.InterpreterService;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.s3.S3Client;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.pointtils.pointtils.src.util.TestDataUtil.createInterpreterCreationRequest;
import static com.pointtils.pointtils.src.util.TestDataUtil.createInterpreterListResponse;
import static com.pointtils.pointtils.src.util.TestDataUtil.createInterpreterResponse;
import static com.pointtils.pointtils.src.util.TestDataUtil.createInterpreterResponseWithProfessionalData;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@EnableAutoConfiguration(exclude = S3AutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class InterpreterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InterpreterService interpreterService;

    @MockitoBean
    private S3Client s3Client;

    @Test
    @DisplayName("Deve cadastrar intérprete com sucesso usando dados básicos")
    void deveCadastrarInterpreterComSucessoUsandoDadosBasicos() throws Exception {
        // Arrange
        InterpreterBasicRequestDTO request = createInterpreterCreationRequest();
        InterpreterResponseDTO mockResponse = createInterpreterResponse();

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
        InterpreterBasicRequestDTO request = createInterpreterCreationRequest();
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
        InterpreterBasicRequestDTO request = createInterpreterCreationRequest();
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
        InterpreterBasicRequestDTO request = createInterpreterCreationRequest();
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

        ProfessionalDataPatchRequestDTO professionalData = ProfessionalDataPatchRequestDTO.builder()
                .cnpj("12345678000195")
                .minValue(new BigDecimal("100.00"))
                .maxValue(new BigDecimal("500.00"))
                .imageRights(true)
                .modality(InterpreterModality.PERSONALLY)
                .description("Intérprete experiente em LIBRAS")
                .build();

        patchRequest.setProfessionalData(professionalData);

        InterpreterResponseDTO mockResponse = createInterpreterResponseWithProfessionalData();

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
                .andExpect(jsonPath("$.data.professional_data.description")
                        .value("Intérprete experiente em LIBRAS"));
    }

    @Test
    @DisplayName("Deve encontrar intérprete por ID com sucesso")
    void deveBuscarInterpreterPorIdComSucesso() throws Exception {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        InterpreterResponseDTO mockResponse = createInterpreterResponse();
        when(interpreterService.findById(interpreterId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/v1/interpreters/{interpreterId}", interpreterId)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Intérprete encontrado com sucesso"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value("interpreter@exemplo.com"));
    }

    @Test
    @DisplayName("Deve deletar intérprete por ID com sucesso")
    void deveDeletarInterpreterPorIdComSucesso() throws Exception {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        doNothing().when(interpreterService).delete(interpreterId);

        // Act & Assert
        mockMvc.perform(delete("/v1/interpreters/{interpreterId}", interpreterId)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve encontrar todos os intérpretes com sucesso")
    void deveBuscarInterpretesComSucesso() throws Exception {
        // Arrange
        InterpreterListResponseDTO mockResponse = createInterpreterListResponse();
        when(interpreterService.findAll(
                null, null, null, null, null, null, null)).thenReturn(List.of(mockResponse));

        // Act & Assert
        mockMvc.perform(get("/v1/interpreters")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Intérpretes encontrados com sucesso"))
                .andExpect(jsonPath("$.data[0].id").exists());
    }

    @Test
    @DisplayName("Deve atualizar todos os dados do intérprete intérprete com sucesso")
    void deveRealizarAtualizacaoCompletaDoInterpreterComSucesso() throws Exception {
        // Arrange
        UUID interpreterId = UUID.randomUUID();
        InterpreterBasicRequestDTO request = createInterpreterCreationRequest();
        InterpreterResponseDTO mockResponse = createInterpreterResponse();

        when(interpreterService.updateComplete(eq(interpreterId), any(InterpreterBasicRequestDTO.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(put("/v1/interpreters/{id}", interpreterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Intérprete atualizado com sucesso"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value("interpreter@exemplo.com"))
                .andExpect(jsonPath("$.data.name").value("João Intérprete"))
                .andExpect(jsonPath("$.data.type").value("interpreter"))
                .andExpect(jsonPath("$.data.status").value("pending"));
    }
}
