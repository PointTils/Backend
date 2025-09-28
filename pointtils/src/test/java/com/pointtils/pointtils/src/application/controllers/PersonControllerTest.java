package com.pointtils.pointtils.src.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointtils.pointtils.src.application.dto.PersonDTO;
import com.pointtils.pointtils.src.application.dto.requests.PersonCreationRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.PersonPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.PersonResponseDTO;
import com.pointtils.pointtils.src.application.services.PersonService;
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

import java.util.List;
import java.util.UUID;

import static com.pointtils.pointtils.src.util.TestDataUtil.createPersonCreationRequest;
import static com.pointtils.pointtils.src.util.TestDataUtil.createPersonPatchRequest;
import static com.pointtils.pointtils.src.util.TestDataUtil.createPersonResponse;
import static com.pointtils.pointtils.src.util.TestDataUtil.createPersonUpdateRequest;
import static org.mockito.ArgumentMatchers.any;
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
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PersonService personService;

    @MockitoBean
    private S3Client s3Client;

    @Test
    @DisplayName("Deve cadastrar pessoa com sucesso")
    void deveCadastrarPessoaComSucesso() throws Exception {
        // Arrange
        PersonCreationRequestDTO request = createPersonCreationRequest();
        PersonResponseDTO mockResponse = createPersonResponse();

        when(personService.registerPerson(any(PersonCreationRequestDTO.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/person/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuário surdo cadastrado com sucesso"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value("pessoa@exemplo.com"))
                .andExpect(jsonPath("$.data.name").value("João Pessoa"))
                .andExpect(jsonPath("$.data.type").value("PERSON"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.gender").value("MALE"))
                .andExpect(jsonPath("$.data.cpf").value("11122233344"))
                .andExpect(jsonPath("$.data.picture").value("picture_url"))
                .andExpect(jsonPath("$.data.birthday").value("1990-01-01"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    @DisplayName("Deve buscar todas as pessoas com sucesso")
    void deveBuscarPessoasComSucesso() throws Exception {
        // Arrange
        PersonResponseDTO mockResponse = createPersonResponse();
        when(personService.findAll()).thenReturn(List.of(mockResponse));

        // Act & Assert
        mockMvc.perform(get("/v1/person")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuários surdos encontrados com sucesso"))
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].email").value("pessoa@exemplo.com"))
                .andExpect(jsonPath("$.data[0].name").value("João Pessoa"))
                .andExpect(jsonPath("$.data[0].type").value("PERSON"))
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.data[0].gender").value("MALE"))
                .andExpect(jsonPath("$.data[0].cpf").value("11122233344"))
                .andExpect(jsonPath("$.data[0].picture").value("picture_url"))
                .andExpect(jsonPath("$.data[0].birthday").value("1990-01-01"))
                .andExpect(jsonPath("$.data[0].password").doesNotExist());
    }

    @Test
    @DisplayName("Deve buscar pessoa por ID com sucesso")
    void deveBuscarPessoaPorIdComSucesso() throws Exception {
        // Arrange
        UUID personId = UUID.randomUUID();
        PersonResponseDTO mockResponse = createPersonResponse();
        when(personService.findById(personId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/v1/person/{id}", personId)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuário surdo encontrado com sucesso"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value("pessoa@exemplo.com"))
                .andExpect(jsonPath("$.data.name").value("João Pessoa"))
                .andExpect(jsonPath("$.data.type").value("PERSON"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.gender").value("MALE"))
                .andExpect(jsonPath("$.data.cpf").value("11122233344"))
                .andExpect(jsonPath("$.data.picture").value("picture_url"))
                .andExpect(jsonPath("$.data.birthday").value("1990-01-01"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    @DisplayName("Deve deletar pessoa por ID com sucesso")
    void deveDeletarPessoaPorIdComSucesso() throws Exception {
        // Arrange
        UUID personId = UUID.randomUUID();
        doNothing().when(personService).delete(personId);

        // Act & Assert
        mockMvc.perform(delete("/v1/person/{id}", personId)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve atualizar pessoa por ID com sucesso")
    void deveAtualizarPessoaPorIdComSucesso() throws Exception {
        // Arrange
        UUID personId = UUID.randomUUID();
        PersonResponseDTO mockResponse = createPersonResponse();
        PersonDTO personDTO = createPersonUpdateRequest();
        when(personService.updateComplete(personId, personDTO)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(put("/v1/person/{id}", personId)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuário surdo atualizado com sucesso"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value("pessoa@exemplo.com"))
                .andExpect(jsonPath("$.data.name").value("João Pessoa"))
                .andExpect(jsonPath("$.data.type").value("PERSON"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.gender").value("MALE"))
                .andExpect(jsonPath("$.data.cpf").value("11122233344"))
                .andExpect(jsonPath("$.data.picture").value("picture_url"))
                .andExpect(jsonPath("$.data.birthday").value("1990-01-01"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    @DisplayName("Deve atualizar pessoa por ID parcialmente com sucesso")
    void deveAtualizarPessoaPorIdParcialmenteComSucesso() throws Exception {
        // Arrange
        UUID personId = UUID.randomUUID();
        PersonResponseDTO mockResponse = createPersonResponse();
        PersonPatchRequestDTO personDTO = createPersonPatchRequest();
        when(personService.updatePartial(personId, personDTO)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(patch("/v1/person/{id}", personId)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuário surdo atualizado com sucesso"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value("pessoa@exemplo.com"))
                .andExpect(jsonPath("$.data.name").value("João Pessoa"))
                .andExpect(jsonPath("$.data.type").value("PERSON"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.gender").value("MALE"))
                .andExpect(jsonPath("$.data.cpf").value("11122233344"))
                .andExpect(jsonPath("$.data.picture").value("picture_url"))
                .andExpect(jsonPath("$.data.birthday").value("1990-01-01"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }
}
