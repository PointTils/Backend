package com.pointtils.pointtils.src.application.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pointtils.pointtils.src.application.dto.LoginRequestDTO;
import com.pointtils.pointtils.src.application.dto.LoginResponseDTO;
import com.pointtils.pointtils.src.application.dto.TokensDTO;
import com.pointtils.pointtils.src.application.dto.UserDTO;
import com.pointtils.pointtils.src.application.services.LoginService;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.infrastructure.configs.LoginAttemptService;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @SuppressWarnings("removal")
    @MockBean
    private LoginAttemptService loginAttemptService;

    @SuppressWarnings("removal")
    @MockBean
    private LoginService loginService;

    @BeforeEach
    @SuppressWarnings("unused")
    void up() {
        Person person = new Person();
        person.setEmail("usuario@exemplo.com");
        person.setPassword(passwordEncoder.encode("minhasenha123"));
        person.setName("João Silva");
        person.setPhone("51999999999");
        person.setPicture("picture_url");
        person.setStatus("active");

        userRepository.save(person);
    }

    @AfterEach
    @SuppressWarnings("unused")
    void down() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve retornar 200 quando o login for válido")
    void deveRetornar200QuandoLoginValido() throws Exception {

        LoginResponseDTO mockLoginResponse = new LoginResponseDTO(
                true,
                "Autenticação realizada com sucesso",
                new LoginResponseDTO.Data(
                        new UserDTO(1L, "usuario@exemplo.com", "João Silva", "person", "active"),
                        new TokensDTO("access-token", "refresh-token", "Bearer", 3600, 604800)));

        when(loginService.login(anyString(), anyString())).thenReturn(mockLoginResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email":"usuario@exemplo.com",
                            "password":"minhasenha123"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.email").value("usuario@exemplo.com"))
                .andExpect(jsonPath("$.data.tokens.accessToken").exists());
    }

    @Test
    @DisplayName("Deve retornar 401 com credenciais inválidas")
    void deveRetornar401QuandoCredenciaisInvalidas() throws Exception {
        when(loginService.login(anyString(), anyString()))
                .thenThrow(new AuthenticationException("Credenciais inválidas"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"teste@email.com\",\"password\":\"123456\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciais inválidas"));
    }

    @Test
    @DisplayName("Deve retornar 403 quando o usuário estiver bloqueado")
    void deveRetornar403QuandoUsuarioBloqueado() throws Exception {
        when(loginService.login(anyString(), anyString()))
                .thenThrow(new AuthenticationException("Usuário bloqueado"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"teste@email.com\",\"password\":\"minhasenha123\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Usuário bloqueado"));
    }

    @Test
    @DisplayName("Deve retornar 429 quando muitas tentativas de login falharem")
    void deveRetornar429QuandoMuitasTentativas() throws Exception {
        String clientIp = "192.168.0.20";
        when(loginAttemptService.isBlocked(clientIp)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"usuario@exemplo.com\",\"password\":\"minhasenha123\"}")
                .with(request -> {
                    request.setRemoteAddr(clientIp);
                    return request;
                }))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value("Muitas tentativas de login"));
    }

    @Test
    void deveRetornar422QuandoValidacaoFalha() throws Exception {
        when(loginService.login(anyString(), anyString()))
                .thenThrow(new AuthenticationException("O campo email é obrigatório"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"\",\"password\":\"\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("O campo email é obrigatório"));
    }

    @Test
    @DisplayName("Deve permitir login quand IP não está bloqueado")
    void devePermitirLoginQuandoIpNaoBloqueado() throws Exception {
        String clientIp = "192.168.0.20";
        when(loginAttemptService.isBlocked(clientIp)).thenReturn(false);
        LoginResponseDTO mockLoginResponse = new LoginResponseDTO(
                true,
                "Autenticação realizada com sucesso",
                new LoginResponseDTO.Data(
                        new UserDTO(1L, "usuario@exemplo.com", "João Silva", "person", "active"),
                        new TokensDTO("access-token", "refresh-token", "Bearer", 3600, 604800)));

        when(loginService.login(anyString(), anyString())).thenReturn(mockLoginResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email":"usuario@exemplo.com",
                                    "password":"minhasenha123"}
                                """)
                        .with(request -> {
                            request.setRemoteAddr(clientIp);
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.email").value("usuario@exemplo.com"))
                .andExpect(jsonPath("$.data.tokens.accessToken").exists());
    }
}
