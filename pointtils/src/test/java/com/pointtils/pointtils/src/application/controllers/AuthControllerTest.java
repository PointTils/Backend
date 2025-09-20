package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.LoginResponseDTO;
import com.pointtils.pointtils.src.application.dto.RefreshTokenResponseDTO;
import com.pointtils.pointtils.src.application.dto.TokensDTO;
import com.pointtils.pointtils.src.application.dto.UserDTO;
import com.pointtils.pointtils.src.application.services.AuthService;
import com.pointtils.pointtils.src.core.domain.entities.Enterprise;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;
import com.pointtils.pointtils.src.infrastructure.configs.LoginAttemptService;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtTokenProvider;

    @MockitoBean
    private LoginAttemptService loginAttemptService;

    @MockitoBean
    private AuthService authService;

    @BeforeEach
    @SuppressWarnings("unused")
    void up() {
        Person person = new Person();
        person.setEmail("usuario@exemplo.com");
        person.setPassword(passwordEncoder.encode("minhasenha123"));
        person.setName("João Silva");
        person.setPhone("51999999999");
        person.setPicture("picture_url");
        person.setStatus(UserStatus.ACTIVE);
        person.setType(UserTypeE.PERSON);

        userRepository.save(person);

        Enterprise enterprise = new Enterprise();
        enterprise.setEmail("enterprise@exemplo.com");
        enterprise.setPassword(passwordEncoder.encode("enterprise123"));
        enterprise.setCorporateReason("Empresa Exemplo");
        enterprise.setPhone("51888888888");
        enterprise.setPicture("enterprise_picture_url");
        enterprise.setStatus(UserStatus.ACTIVE);
        enterprise.setType(UserTypeE.ENTERPRISE);

        userRepository.save(enterprise);
    }

    @AfterEach
    @SuppressWarnings("unused")
    void down() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve retornar 200 quando o login de Pessoa for válido")
    void deveRetornar200QuandoLoginDePessoaValido() throws Exception {

        LoginResponseDTO mockLoginResponse = new LoginResponseDTO(
                true,
                "Autenticação realizada com sucesso",
                new LoginResponseDTO.Data(
                                 new UserDTO(
                                                        UUID.randomUUID(), "usuario@exemplo.com",
                                                        null,
                                                        null,
                                                        UserTypeE.PERSON,
                                                        UserStatus.ACTIVE),
                        new TokensDTO("access-token", "refresh-token", "Bearer", 3600,
                                604800)));

        when(authService.login(anyString(), anyString())).thenReturn(mockLoginResponse);

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
                .andExpect(jsonPath("$.data.user.type").value("person"))
                .andExpect(jsonPath("$.data.tokens.accessToken").exists())
                .andExpect(jsonPath("$.data.tokens.refreshToken").exists());
    }

    @Test
    @DisplayName("Deve retornar 200 quando o login de Empresa for válido")
    void deveRetornar200QuandoLoginDeEmpresaValido() throws Exception {

        LoginResponseDTO mockLoginResponse = new LoginResponseDTO(
                true,
                "Autenticação realizada com sucesso",
                new LoginResponseDTO.Data(
                                        new UserDTO(
                                                        UUID.randomUUID(), "usuario@exemplo.com",
                                                        null,
                                                        null,
                                                        UserTypeE.PERSON,
                                                        UserStatus.ACTIVE),
                        new TokensDTO("access-token", "refresh-token", "Bearer", 3600,
                                604800)));

        when(authService.login(anyString(), anyString())).thenReturn(mockLoginResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email":"enterprise@exemplo.com",
                                    "password":"enterprise123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.email").value("enterprise@exemplo.com"))
                .andExpect(jsonPath("$.data.user.type").value("enterprise"))
                .andExpect(jsonPath("$.data.tokens.accessToken").exists())
                .andExpect(jsonPath("$.data.tokens.refreshToken").exists());
    }

    @Test
    @DisplayName("Deve retornar 401 com credenciais inválidas")
    void deveRetornar401QuandoCredenciaisInvalidas() throws Exception {
        when(authService.login(anyString(), anyString()))
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
        when(authService.login(anyString(), anyString()))
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
    @DisplayName("Deve retornar 400 quando email for nulo ou vazio")
    void deveRetornar400QuandoEmailForNuloOuVazio() throws Exception {
        when(authService.login(anyString(), anyString()))
                .thenThrow(new AuthenticationException("O campo email é obrigatório"));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\",\"password\":\"senha123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O campo email é obrigatório"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando senha for nula ou vazia")
    void deveRetornar400QuandoSenhaForNulaOuVazia() throws Exception {
        when(authService.login(anyString(), anyString()))
                .thenThrow(new AuthenticationException("O campo senha é obrigatório"));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"usuario@test.com\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O campo senha é obrigatório"));
    }

    @Test
    @DisplayName("Deve retornar 422 quando o email for inválido")
    void deveRetornar422QuandoEmailForInvalido() throws Exception {
        when(authService.login(anyString(), anyString()))
                .thenThrow(new AuthenticationException("Formato de e-mail inválido"));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"usuario123\",\"password\":\"senha123\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Formato de e-mail inválido"));
    }

    @Test
    @DisplayName("Deve retornar 422 quando a senha for inválida")
    void deveRetornar422QuandoSenhaForInvalida() throws Exception {
        when(authService.login(anyString(), anyString()))
                .thenThrow(new AuthenticationException("Formato de senha inválida"));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"usuario@test.com\",\"password\":\"    \"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Formato de senha inválida"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando o usuário não for encontrado")
    void deveRetornar404QuandoUsuarioNaoForEncontrado() throws Exception {
        when(authService.login(anyString(), anyString()))
                .thenThrow(new AuthenticationException("Usuário não encontrado"));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\" \",\"password\":\"senha123\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
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
                                 new UserDTO(
                                                        UUID.randomUUID(), "usuario@exemplo.com",
                                                        null,
                                                        null,
                                                        UserTypeE.PERSON,
                                                        UserStatus.ACTIVE),
                        new TokensDTO("access-token", "refresh-token", "Bearer", 3600,
                                604800)));

        when(authService.login(anyString(), anyString())).thenReturn(mockLoginResponse);

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

    @Test
    @DisplayName("Deve renovar token com refresh token válido")
    void deveRenovarTokenComRefreshTokenValido() throws Exception {
        RefreshTokenResponseDTO mockResponse = new RefreshTokenResponseDTO(
                true,
                "Token renovado com sucesso",
                new RefreshTokenResponseDTO.Data(
                        new TokensDTO("new-access-token", "new-refresh-token", "Bearer",
                                3600, 604800)));
        when(authService.refreshToken(anyString())).thenReturn(mockResponse);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "refresh_token":"valid-refresh-token"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokens.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.data.tokens.refreshToken").value("new-refresh-token"));
    }

    @Test
    @DisplayName("Deve retornar 401 ao renovar token com refresh token inválido")
    void deveRetornar401AoRenovarTokenComRefreshTokenInvalido() throws Exception {
        when(authService.refreshToken(anyString()))
                .thenThrow(new AuthenticationException("Refresh token inválido ou expirado"));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "refresh_token":"invalid-refresh-token"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Refresh token inválido ou expirado"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao renovar token sem fornecer refresh token")
    void deveRetornar400AoRenovarTokenSemFornecerRefreshToken() throws Exception {
        when(authService.refreshToken(anyString()))
                .thenThrow(new AuthenticationException("Refresh token não fornecido"));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "refresh_token":""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Refresh token não fornecido"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao renovar token quando usuário não for encontrado")
    void deveRetornar404AoRenovarTokenQuandoUsuarioNaoForEncontrado() throws Exception {
        when(authService.refreshToken(anyString()))
                .thenThrow(new AuthenticationException("Usuário não encontrado"));
        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "refresh_token":"valid-but-user-not-found"
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 200 quando logout for bem-sucedido")
    void deveRetornar200QuandoLogoutForBemSucedido() throws Exception {
        String accessToken = jwtTokenProvider.generateToken("user@exemplo.com");
        String refreshToken = jwtTokenProvider.generateRefreshToken("user@exemplo.com");

        String refreshTokenJson = "{ \"refreshToken\": \"" + refreshToken + "\" }";

        when(authService.logout(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/auth/logout")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshTokenJson))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 400 quando logout for chamado sem token de acesso")
    void deveRetornar400QuandoLogoutForChamadoSemTokenDeAcesso() throws Exception {
        String refreshToken = jwtTokenProvider.generateRefreshToken("user@exemplo.com");
        String refreshTokenJson = "{ \"refreshToken\": \"" + refreshToken + "\" }";

        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/auth/logout")
                .header("Authorization", "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshTokenJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Access token não fornecido"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando logout for chamado sem refresh token")
    void deveRetornar400QuandoLogoutForChamadoSemRefreshToken() throws Exception {
        String accessToken = jwtTokenProvider.generateToken("user@exemplo.com");
        String refreshTokenJson = "{ \"refreshToken\": \"\" }";
        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/auth/logout")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshTokenJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Refresh token não fornecido"));
    }

    @Test
    @DisplayName("Deve retornar 401 quando logout for chamado com token de acesso inválido")
    void deveRetornar401QuandoLogoutForChamadoComTokenDeAcessoInvalido() throws Exception {
        String refreshToken = jwtTokenProvider.generateRefreshToken("user@exemplo.com");
        String refreshTokenJson = "{ \"refreshToken\": \"" + refreshToken + "\" }";

        when(authService.logout(anyString(), anyString()))
                .thenThrow(new AuthenticationException("Access token inválido ou expirado"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/auth/logout")
                .header("Authorization", "Bearer invalid-access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshTokenJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Access token inválido ou expirado"));
    }

    @Test
    @DisplayName("Deve retornar 401 quando logout for chamado com refresh token inválido")
    void deveRetornar401QuandoLogoutForChamadoComRefreshTokenInvalido() throws Exception {
        String accessToken = jwtTokenProvider.generateToken("user@exemplo.com");
        String refreshTokenJson = "{ \"refreshToken\": \"invalid-refresh-token\" }";

        when(authService.logout(anyString(), anyString()))
                .thenThrow(new AuthenticationException("Refresh token inválido ou expirado"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/auth/logout")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(refreshTokenJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Refresh token inválido ou expirado"));
    }
}
