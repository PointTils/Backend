package com.pointtils.pointtils.src.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.application.dto.requests.EnterprisePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.EnterpriseRequestDTO;
import com.pointtils.pointtils.src.core.domain.entities.Enterprise;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;
import com.pointtils.pointtils.src.infrastructure.repositories.EnterpriseRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
@ActiveProfiles("test")
class EnterpriseControllerTest {

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private LocationRepository locationRepository;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private EnterpriseRequestDTO validEnterpriseRequest;
    private EnterprisePatchRequestDTO validPatchRequest;
    private LocationDTO locationDTO;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        when(jwtService.isTokenExpired(anyString())).thenReturn(Boolean.FALSE);

        enterpriseRepository.deleteAll();
        locationRepository.deleteAll();

        locationDTO = LocationDTO.builder()
                .id(UUID.randomUUID())
                .uf("SP")
                .city("São Paulo")
                .build();

        validEnterpriseRequest = EnterpriseRequestDTO.builder()
                .corporateReason("Empresa Teste LTDA")
                .cnpj("12345678000190")
                .email("empresa@teste.com")
                .password("senhaSegura123")
                .phone("11999887766")
                .picture("https://example.com/picture.jpg")
                .location(locationDTO)
                .build();

        validPatchRequest = EnterprisePatchRequestDTO.builder()
                .corporateReason("Empresa Atualizada LTDA")
                .phone("11888777666")
                .email("novoemail@teste.com")
                .build();
    }

    @Test
    @DisplayName("Deve retornar 400 quando o ID não for um UUID válido no update (PATCH)")
    void shouldReturnBadRequestWhenIdIsNotValidUUIDOnPatch() throws Exception {
        mockMvc.perform(patch("/v1/enterprise-users/{id}", "not-a-uuid")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPatchRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Invalid UUID")));
    }

    @Test
    @DisplayName("Deve retornar 201 quando criar empresa com sucesso")
    void shouldCreateEnterpriseSuccessfully() throws Exception {
        mockMvc.perform(post("/v1/enterprise-users/register")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEnterpriseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Empresa cadastrada com sucesso"))
                .andExpect(jsonPath("$.data.corporate_reason").value("Empresa Teste LTDA"))
                .andExpect(jsonPath("$.data.cnpj").value("12345678000190"))
                .andExpect(jsonPath("$.data.email").value("empresa@teste.com"));

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        assertThat(enterprises).hasSize(1);
        assertThat(enterprises.get(0).getCorporateReason()).isEqualTo("Empresa Teste LTDA");
        assertThat(enterprises.get(0).getCnpj()).isEqualTo("12345678000190");
        assertThat(enterprises.get(0).getEmail()).isEqualTo("empresa@teste.com");
    }

    @Test
    @DisplayName("Deve retornar 422 quando criar empresa com CNPJ inválido")
    void shouldGetUnprocessableEntityWhenCreatingEnterpriseWithInvalidCNPJ() throws Exception {
        EnterpriseRequestDTO invalidCNPJRequest = EnterpriseRequestDTO.builder()
                .corporateReason("Empresa Teste LTDA")
                .cnpj("123")
                .email("empresa@teste.com")
                .password("senhaSegura123")
                .phone("11999887766")
                .location(locationDTO)
                .build();

        mockMvc.perform(post("/v1/enterprise-users/register")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCNPJRequest)))
                .andExpect(status().isUnprocessableEntity());

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        assertThat(enterprises).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar 422 quando criar empresa com email inválido")
    void shouldGetUnprocessableEntityWhenCreatingEnterpriseWithInvalidEmail() throws Exception {
        EnterpriseRequestDTO invalidEmailRequest = EnterpriseRequestDTO.builder()
                .corporateReason("Empresa Teste LTDA")
                .cnpj("12345678000190")
                .email("email-invalido.com")
                .password("senhaSegura123")
                .phone("11999887766")
                .location(locationDTO)
                .build();

        mockMvc.perform(post("/v1/enterprise-users/register")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmailRequest)))
                .andExpect(status().isUnprocessableEntity());

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        assertThat(enterprises).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar 422 quando criar empresa com telefone inválido")
    void shouldGetUnprocessableEntityWhenCreatingEnterpriseWithInvalidPhone() throws Exception {
        EnterpriseRequestDTO invalidPhoneRequest = EnterpriseRequestDTO.builder()
                .corporateReason("Empresa Teste LTDA")
                .cnpj("12345678000190")
                .email("empresa@teste.com")
                .password("senhaSegura123")
                .phone("abc123def")
                .location(locationDTO)
                .build();

        mockMvc.perform(post("/v1/enterprise-users/register")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPhoneRequest)))
                .andExpect(status().isUnprocessableEntity());

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        assertThat(enterprises).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar 400 quando criar empresa com campos obrigatórios vazios")
    void shouldGetBadRequestWhenCreatingEnterpriseWithEmptyRequiredFields() throws Exception {
        EnterpriseRequestDTO emptyFieldsRequest = EnterpriseRequestDTO.builder()
                .corporateReason("")
                .cnpj("")
                .email("")
                .password("")
                .phone("")
                .build();

        mockMvc.perform(post("/v1/enterprise-users/register")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyFieldsRequest)))
                .andExpect(status().isBadRequest());

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        assertThat(enterprises).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar 409 quando criar empresa com CNPJ já existente")
    void shouldGetConflictWhenCreatingEnterpriseWithExistingCNPJ() throws Exception {
        mockMvc.perform(post("/v1/enterprise-users/register")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEnterpriseRequest)))
                .andExpect(status().isCreated());

        EnterpriseRequestDTO duplicateCNPJRequest = EnterpriseRequestDTO.builder()
                .corporateReason("Nova Empresa LTDA")
                .cnpj("12345678000190")  // Same CNPJ
                .email("novaempresa@teste.com")
                .password("senhaSegura123")
                .phone("11999887766")
                .location(locationDTO)
                .build();

        mockMvc.perform(post("/v1/enterprise-users/register")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateCNPJRequest)))
                .andExpect(status().isConflict());

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        assertThat(enterprises).hasSize(1);
    }

    @Test
    @DisplayName("Deve retornar 409 quando criar empresa com email já existente")
    void shouldGetConflictWhenCreatingEnterpriseWithExistingEmail() throws Exception {
        mockMvc.perform(post("/v1/enterprise-users/register")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEnterpriseRequest)))
                .andExpect(status().isCreated());

        EnterpriseRequestDTO duplicateEmailRequest = EnterpriseRequestDTO.builder()
                .corporateReason("Nova Empresa LTDA")
                .cnpj("98765432000100")
                .email("empresa@teste.com")
                .password("senhaSegura123")
                .phone("11999887766")
                .location(locationDTO)
                .build();

        mockMvc.perform(post("/v1/enterprise-users/register")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEmailRequest)))
                .andExpect(status().isConflict());

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        assertThat(enterprises).hasSize(1);
    }

    @Test
    @DisplayName("Deve retornar 200 retornar todas as empresas com sucesso")
    void shouldReturnAllEnterprisesSuccessfully() throws Exception {
        mockMvc.perform(post("/v1/enterprise-users/register")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEnterpriseRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/enterprise-users")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].corporate_reason").value("Empresa Teste LTDA"))
                .andExpect(jsonPath("$.data[0].cnpj").value("12345678000190"))
                .andExpect(jsonPath("$.data[0].email").value("empresa@teste.com"));
    }

    @Test
    @DisplayName("Deve retornar 204 quando não existem empresas")
    void shouldGetNoContentWhenNoEnterprisesExist() throws Exception {
        mockMvc.perform(get("/v1/enterprise-users")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar 200 quando encontrar empresa por ID com sucesso")
    void shouldFindEnterpriseByIdSuccessfully() throws Exception {
        mockMvc.perform(post("/v1/enterprise-users/register")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEnterpriseRequest)))
                .andExpect(status().isCreated());

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        UUID createdId = enterprises.get(0).getId();

        mockMvc.perform(get("/v1/enterprise-users/{id}", createdId)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(createdId.toString()))
                .andExpect(jsonPath("$.data.corporate_reason").value("Empresa Teste LTDA"))
                .andExpect(jsonPath("$.data.cnpj").value("12345678000190"))
                .andExpect(jsonPath("$.data.email").value("empresa@teste.com"))
                .andExpect(jsonPath("$.data.type").value("ENTERPRISE"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando empresa não for encontrada por ID")
    void shouldGetNotFoundWhenEnterpriseNotFoundById() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(get("/v1/enterprise-users/{id}", nonExistentId)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 201 quando atualizar empresa parcialmente com dados válidos")
    void shouldUpdateEnterprisePartiallyWithValidData() throws Exception {
        mockMvc.perform(post("/v1/enterprise-users/register")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEnterpriseRequest)))
                .andExpect(status().isCreated());

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        UUID createdId = enterprises.get(0).getId();

        mockMvc.perform(patch("/v1/enterprise-users/{id}", createdId)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPatchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId.toString()))
                .andExpect(jsonPath("$.corporate_reason").value("Empresa Atualizada LTDA"))
                .andExpect(jsonPath("$.email").value("novoemail@teste.com"))
                .andExpect(jsonPath("$.phone").value("11888777666"));

        Optional<Enterprise> updatedEnterprise = enterpriseRepository.findById(createdId);
        assertThat(updatedEnterprise).isPresent();
        assertThat(updatedEnterprise.get().getCorporateReason()).isEqualTo("Empresa Atualizada LTDA");
        assertThat(updatedEnterprise.get().getEmail()).isEqualTo("novoemail@teste.com");
        assertThat(updatedEnterprise.get().getPhone()).isEqualTo("11888777666");
    }

    @Test
    @DisplayName("Deve retornar 404 quando atualizar empresa inexistente")
    void shouldGetNotFoundWhenUpdatingNonExistentEnterprise() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(patch("/v1/enterprise-users/{id}", nonExistentId)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPatchRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 422 quando atualizar empresa com dados inválidos")
    void shouldGetUnprocessableEntityWhenUpdatingEnterpriseWithInvalidData() throws Exception {
        mockMvc.perform(post("/v1/enterprise-users/register")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEnterpriseRequest)))
                .andExpect(status().isCreated());

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        UUID createdId = enterprises.get(0).getId();

        EnterprisePatchRequestDTO invalidPatchRequest = EnterprisePatchRequestDTO.builder()
                .cnpj("123")
                .email("invalid-email")
                .build();

        mockMvc.perform(patch("/v1/enterprise-users/{id}", createdId)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPatchRequest)))
                .andExpect(status().isUnprocessableEntity());

        Optional<Enterprise> unchangedEnterprise = enterpriseRepository.findById(createdId);
        assertThat(unchangedEnterprise).isPresent();
        assertThat(unchangedEnterprise.get().getCnpj()).isEqualTo("12345678000190");
        assertThat(unchangedEnterprise.get().getEmail()).isEqualTo("empresa@teste.com");
    }

    @Test
    @DisplayName("Deve retornar 204 quando deletar empresa com sucesso")
    void shouldGetWhenDeleteEnterpriseSuccessfully() throws Exception {
        mockMvc.perform(post("/v1/enterprise-users/register")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEnterpriseRequest)))
                .andExpect(status().isCreated());

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        UUID createdId = enterprises.get(0).getId();

        mockMvc.perform(delete("/v1/enterprise-users/{id}", createdId)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<Enterprise> deletedEnterprise = enterpriseRepository.findById(createdId);
        assertThat(deletedEnterprise).isPresent();
        assertThat(deletedEnterprise.get().getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar deletar uma empresa inexistente")
    void shouldGetNotFoundAfterTryingToRemoveUnexistingEnterprise() throws Exception {
        UUID randomUUID = UUID.randomUUID();
        mockMvc.perform(delete("/v1/enterprise-users/{id}", randomUUID)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        assertThat(enterprises).isEmpty();
    }
}