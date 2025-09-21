package com.pointtils.pointtils.src.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.application.dto.PersonDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.PersonalRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ProfessionalPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.ProfessionalInfoResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.application.services.InterpreterRegisterService;
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
    private InterpreterRegisterService interpreterRegisterService;

    @Test
    @DisplayName("Deve cadastrar intérprete com sucesso usando dados básicos")
    void deveCadastrarInterpreterComSucessoUsandoDadosBasicos() throws Exception {
        // Arrange
        InterpreterBasicRequestDTO request = createValidBasicRequest();
        InterpreterResponseDTO mockResponse = createMockResponse();

        when(interpreterRegisterService.registerBasic(any(InterpreterBasicRequestDTO.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/interpreters/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Intérprete cadastrado com sucesso"))
                .andExpect(jsonPath("$.data.id_interpreter").exists())
                .andExpect(jsonPath("$.data.user.email").value("interpreter@exemplo.com"))
                .andExpect(jsonPath("$.data.person.name").value("João Intérprete"))
                .andExpect(jsonPath("$.data.user.type").value("interpreter"))
                .andExpect(jsonPath("$.data.user.status").value("pending"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando dados pessoais não forem fornecidos")
    void deveRetornar400QuandoDadosPessoaisNaoFornecidos() throws Exception {
        // Arrange
        InterpreterBasicRequestDTO request = new InterpreterBasicRequestDTO();
        request.setLocation(new LocationDTO("RS", "Porto Alegre"));

        // Act & Assert
        mockMvc.perform(post("/v1/interpreters/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados inválidos: [Dados pessoais devem ser preenchidos]"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando localização não for fornecida")
    void deveRetornar400QuandoLocalizacaoNaoFornecida() throws Exception {
        // Arrange
        InterpreterBasicRequestDTO request = new InterpreterBasicRequestDTO();
        request.setPersonalData(createValidPersonalData());

        // Act & Assert
        mockMvc.perform(post("/v1/interpreters/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados inválidos: [Localização deve ser preenchida]"));
    }

    @Test
    @DisplayName("Deve retornar 422 quando email for inválido")
    void deveRetornar422QuandoEmailForInvalido() throws Exception {
        // Arrange
        PersonalRequestDTO personalData = createValidPersonalData();
        personalData.setEmail("email-invalido");

        InterpreterBasicRequestDTO request = new InterpreterBasicRequestDTO();
        request.setPersonalData(personalData);
        request.setLocation(new LocationDTO("RS", "Porto Alegre"));

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
        PersonalRequestDTO personalData = createValidPersonalData();
        personalData.setCpf("123"); // CPF inválido

        InterpreterBasicRequestDTO request = new InterpreterBasicRequestDTO();
        request.setPersonalData(personalData);
        request.setLocation(new LocationDTO("RS", "Porto Alegre"));

        // Act & Assert
        mockMvc.perform(post("/v1/interpreters/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Dados inválidos: [CPF inválido]"));
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

        when(interpreterRegisterService.updatePartial(any(UUID.class), any(InterpreterPatchRequestDTO.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(patch("/v1/interpreters/{id}", interpreterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Intérprete atualizado com sucesso"))
                .andExpect(jsonPath("$.data.professional_info.cnpj").value("12345678000195"))
                .andExpect(jsonPath("$.data.professional_info.min_value").value(100.00))
                .andExpect(jsonPath("$.data.professional_info.max_value").value(500.00))
                .andExpect(jsonPath("$.data.professional_info.image_rights").value(true))
                .andExpect(jsonPath("$.data.professional_info.modality").value("presencial"))
                .andExpect(jsonPath("$.data.professional_info.description").value("Intérprete experiente em LIBRAS"));
    }

    private InterpreterBasicRequestDTO createValidBasicRequest() {
        InterpreterBasicRequestDTO request = new InterpreterBasicRequestDTO();
        request.setPersonalData(createValidPersonalData());
        request.setLocation(new LocationDTO("RS", "Porto Alegre"));
        return request;
    }

    private PersonalRequestDTO createValidPersonalData() {
        return PersonalRequestDTO.builder()
                .name("João Intérprete")
                .email("interpreter@exemplo.com")
                .password("senha123")
                .phone("51999999999")
                .gender("M")
                .birthday(LocalDate.of(1990, 1, 1))
                .cpf("12345678901")
                .picture("picture_url")
                .build();
    }

    private InterpreterResponseDTO createMockResponse() {
        UserResponseDTO user = UserResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("interpreter@exemplo.com")
                .type("interpreter")
                .status("pending")
                .phone("51999999999")
                .picture("picture_url")
                .build();

        PersonDTO person = PersonDTO.builder()
                .name("João Intérprete")
                .gender(Gender.MALE)
                .birthday(LocalDate.of(1990, 1, 1))
                .cpf("12345678901")
                .build();

        ProfessionalInfoResponseDTO professionalInfo = ProfessionalInfoResponseDTO.builder()
                .cnpj(null)
                .rating(new BigDecimal("0.0"))
                .minValue(new BigDecimal("0.0"))
                .maxValue(new BigDecimal("0.0"))
                .imageRights(false)
                .modality(null)
                .description(null)
                .build();

        InterpreterResponseDTO response = new InterpreterResponseDTO();
        response.setIdInterpreter(UUID.randomUUID());
        response.setUser(user);
        response.setPerson(person);
        response.setProfessionalInfo(professionalInfo);
        response.setLocation(new LocationDTO("RS", "Porto Alegre"));
        response.setSpecialties(null);

        return response;
    }

    private InterpreterResponseDTO createMockResponseWithProfessionalData() {
        UserResponseDTO user = UserResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("interpreter@exemplo.com")
                .type("interpreter")
                .status("pending")
                .phone("51999999999")
                .picture("picture_url")
                .build();

        PersonDTO person = PersonDTO.builder()
                .name("João Intérprete")
                .gender(Gender.MALE)
                .birthday(LocalDate.of(1990, 1, 1))
                .cpf("12345678901")
                .build();

        ProfessionalInfoResponseDTO professionalInfo = ProfessionalInfoResponseDTO.builder()
                .cnpj("12345678000195")
                .rating(new BigDecimal("0.0"))
                .minValue(new BigDecimal("100.00"))
                .maxValue(new BigDecimal("500.00"))
                .imageRights(true)
                .modality("presencial")
                .description("Intérprete experiente em LIBRAS")
                .build();

        InterpreterResponseDTO response = new InterpreterResponseDTO();
        response.setIdInterpreter(UUID.randomUUID());
        response.setUser(user);
        response.setPerson(person);
        response.setProfessionalInfo(professionalInfo);
        response.setLocation(new LocationDTO("RS", "Porto Alegre"));
        response.setSpecialties(null);

        return response;
    }
}
