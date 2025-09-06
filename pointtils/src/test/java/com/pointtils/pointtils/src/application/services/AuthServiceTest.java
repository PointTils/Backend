package com.pointtils.pointtils.src.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pointtils.pointtils.src.application.dto.LoginRequestDTO;
import com.pointtils.pointtils.src.application.dto.LoginResponseDTO;
import com.pointtils.pointtils.src.application.dto.RefreshTokenResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;
import com.pointtils.pointtils.src.infrastructure.configs.LoginAttemptService;
import com.pointtils.pointtils.src.infrastructure.configs.RedisBlacklistService;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtTokenProvider;

    @Mock
    @SuppressWarnings("unused")
    private LoginAttemptService loginAttemptService;
    
    @InjectMocks
    private AuthService loginService;

    @Mock
    private RedisBlacklistService redisBlacklistService;

    @Test
    @DisplayName("Deve autenticar usuario pessoa com sucesso")
    void deveAutenticarUsuarioPessoaComSucesso() {
        Person person = new Person();
        person.setId(1L);
        person.setEmail("test@email.com");
        person.setPassword("password123");
        person.setName("Test User");
        person.setPhone("51999999999");
        person.setPicture("picture_url");
        person.setStatus("active");

        when(userRepository.findByEmail("test@email.com")).thenReturn(person);
        when(passwordEncoder.matches("password123", "password123")).thenReturn(true);
        when(jwtTokenProvider.generateToken(person.getEmail())).thenReturn("mocked_jwt_token");
        when(jwtTokenProvider.generateRefreshToken(person.getEmail())).thenReturn("mocked_jwt_refresh_token");

        LoginRequestDTO request = new LoginRequestDTO("test@email.com", "password123");

        LoginResponseDTO response = loginService.login(request.getEmail(), request.getPassword());

        assertNotNull(response);
        assertEquals("test@email.com", response.getData().user().getEmail());
        assertEquals("mocked_jwt_token", response.getData().tokens().getAccessToken());
        assertEquals("mocked_jwt_refresh_token", response.getData().tokens().getRefreshToken());
    }

    @Test
    @DisplayName("Deve falhar quando usuário não for encontrado")
    void deveFalharQuandoUsuarioNaoEncontrado() {
        when(userRepository.findByEmail("emailnaoexiste@email.com")).thenReturn(null);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.login("emailnaoexiste@email.com", "senha123")
        );

        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando usuário estiver bloqueado")
    void deveFalharQuandoUsuarioBloqueado() {
        Person person = new Person();
        person.setEmail("usuario@exemplo.com");
        person.setPassword("123");
        person.setStatus("blocked");

        when(userRepository.findByEmail("usuario@exemplo.com")).thenReturn(person);
        
        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.login("usuario@exemplo.com", "senha123")
        );

        assertEquals("Usuário bloqueado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando senha for inválida")
    void deveFalharQuandoSenhaInvalida() {
        Person person = new Person();
        person.setEmail("test@email.com");
        person.setPassword("wrongpassword");
        person.setStatus("active");

        when(userRepository.findByEmail("test@email.com")).thenReturn(person);
        when(passwordEncoder.matches("correctpassword", "wrongpassword")).thenReturn(false);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.login("test@email.com", "correctpassword")
        );

        assertEquals("Credenciais inválidas", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando email for nulo ou vazio")
    void deveFalharQuandoEmailNuloOuVazio() {
        AuthenticationException ex1 = assertThrows(
                AuthenticationException.class,
                () -> loginService.login(null, "senha")
        );
        assertEquals("O campo email é obrigatório", ex1.getMessage());
        AuthenticationException ex2 = assertThrows(
                AuthenticationException.class,
                () -> loginService.login("", "senha")
        );
        assertEquals("O campo email é obrigatório", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando senha for nula ou vazia")
    void deveFalharQuandoSenhaNulaOuVazia() {
        AuthenticationException ex1 = assertThrows(
                AuthenticationException.class,
                () -> loginService.login("email@email.com", null)
        );
        assertEquals("O campo senha é obrigatório", ex1.getMessage());
        AuthenticationException ex2 = assertThrows(
                AuthenticationException.class,
                () -> loginService.login("email@email.com", "")
        );
        assertEquals("O campo senha é obrigatório", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve renovar token com refresh token válido")
    void deveRenovarTokenComRefreshTokenValido() {
        Person person = new Person();
        person.setEmail("exemplo@user.com");
        person.setStatus("active");

        when(jwtTokenProvider.isTokenExpired("valid_refresh_token")).thenReturn(false);
        when(jwtTokenProvider.validateToken("valid_refresh_token")).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken("valid_refresh_token")).thenReturn("exemplo@user.com");
        when(userRepository.findByEmail("exemplo@user.com")).thenReturn(person);
        when(jwtTokenProvider.generateToken(person.getEmail())).thenReturn("new_access_token");
        when(jwtTokenProvider.generateRefreshToken(person.getEmail())).thenReturn("new_refresh_token");

        RefreshTokenResponseDTO response = loginService.refreshToken("valid_refresh_token");
        assertNotNull(response);
        assertEquals("new_access_token", response.getData().tokens().getAccessToken());
        assertEquals("new_refresh_token", response.getData().tokens().getRefreshToken());
    }

    @Test
    @DisplayName("Deve falhar ao renovar token com refresh token nulo ou vazio")
    void deveFalharAoRenovarTokenComRefreshTokenNuloOuVazio() {
        AuthenticationException ex1 = assertThrows(
                AuthenticationException.class,
                () -> loginService.refreshToken(null)
        );
        assertEquals("Refresh token não fornecido", ex1.getMessage());
        AuthenticationException ex2 = assertThrows(
                AuthenticationException.class,
                () -> loginService.refreshToken("")
        );
        assertEquals("Refresh token não fornecido", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao renovar token com refresh token inválido")
    void deveFalharAoRenovarTokenComRefreshTokenInvalido() {
        String invalidRefreshToken = "invalid_refresh_token";

        when(jwtTokenProvider.validateToken(invalidRefreshToken)).thenReturn(false);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.refreshToken(invalidRefreshToken)
        );

        assertEquals("Refresh token inválido ou expirado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao renovar token com refresh token expirado")
    void deveFalharAoRenovarTokenComRefreshTokenExpirado() {
        String expiredRefreshToken = "expired_refresh_token";

        when(jwtTokenProvider.isTokenExpired(expiredRefreshToken)).thenReturn(true);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.refreshToken(expiredRefreshToken)
        );

        assertEquals("Refresh token inválido ou expirado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao renovar token quando usuário não for encontrado")
    void deveFalharAoRenovarTokenQuandoUsuarioNaoForEncontrado() {
        String validRefreshToken = "valid_refresh_token";

        when(jwtTokenProvider.isTokenExpired(validRefreshToken)).thenReturn(false);
        when(jwtTokenProvider.validateToken(validRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(validRefreshToken)).thenReturn("exemplo@user.com");
        when(userRepository.findByEmail("exemplo@user.com")).thenReturn(null);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.refreshToken(validRefreshToken)
        );

        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve fazer logout com tokens válidos")
    void deveFazerLogoutComTokensValidos() {
        String accessToken = "valid_access_token";
        String refreshToken = "valid_refresh_token";

        when(jwtTokenProvider.validateToken(accessToken)).thenReturn(true);
        when(jwtTokenProvider.isTokenExpired(accessToken)).thenReturn(false);
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.isTokenExpired(refreshToken)).thenReturn(false);

        loginService.logout(accessToken, refreshToken);

        assertNotNull(redisBlacklistService);        
    }

    @Test
    @DisplayName("Deve falhar ao fazer logout com access token inválido")
    void deveFalharAoFazerLogoutComAccessTokenInvalido() {
        String invalidAccessToken = "invalid_access_token";
        String validRefreshToken = "valid_refresh_token";

        when(jwtTokenProvider.validateToken(invalidAccessToken)).thenReturn(false);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.logout(invalidAccessToken, validRefreshToken)
        );
        assertEquals("Access token inválido ou expirado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao fazer logout com refresh token inválido")
    void deveFalharAoFazerLogoutComRefreshTokenInvalido() {
        String validAccessToken = "valid_access_token";
        String invalidRefreshToken = "invalid_refresh_token";

        when(jwtTokenProvider.validateToken(validAccessToken)).thenReturn(true);
        when(jwtTokenProvider.validateToken(invalidRefreshToken)).thenReturn(false);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.logout(validAccessToken, invalidRefreshToken)
        );
        assertEquals("Refresh token inválido ou expirado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao fazer logout com access token expirado")
    void deveFalharAoFazerLogoutComAccessTokenExpirado() {
        String expiredAccessToken = "expired_access_token";
        String validRefreshToken = "valid_refresh_token";

        when(jwtTokenProvider.validateToken(expiredAccessToken)).thenReturn(true);
        when(jwtTokenProvider.isTokenExpired(expiredAccessToken)).thenReturn(true);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.logout(expiredAccessToken, validRefreshToken)
        );
        assertEquals("Access token inválido ou expirado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao fazer logout com refresh token expirado")
    void deveFalharAoFazerLogoutComRefreshTokenExpirado() {
        String validAccessToken = "valid_access_token";
        String expiredRefreshToken = "expired_refresh_token";

        when(jwtTokenProvider.validateToken(validAccessToken)).thenReturn(true);
        when(jwtTokenProvider.isTokenExpired(validAccessToken)).thenReturn(false);
        when(jwtTokenProvider.validateToken(expiredRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.isTokenExpired(expiredRefreshToken)).thenReturn(true);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.logout(validAccessToken, expiredRefreshToken)
        );
        assertEquals("Refresh token inválido ou expirado", ex.getMessage());
    }
}
