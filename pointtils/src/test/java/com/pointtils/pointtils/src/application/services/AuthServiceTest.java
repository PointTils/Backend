package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.LoginRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.LoginResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.RefreshTokenResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;
import com.pointtils.pointtils.src.infrastructure.configs.MemoryBlacklistService;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtTokenProvider;

    @InjectMocks
    private AuthService loginService;

    @Mock
    private MemoryBlacklistService memoryBlacklistService;

    @Mock
    private MemoryResetTokenService resetTokenService;

    @Test
    @DisplayName("Deve autenticar usuario pessoa com sucesso")
    void deveAutenticarUsuarioPessoaComSucesso() {
        Person person = new Person();
        person.setId(UUID.randomUUID());
        person.setEmail("test@email.com");
        person.setPassword("password123");
        person.setName("Test User");
        person.setPhone("51999999999");
        person.setPicture("picture_url");
        person.setStatus(UserStatus.ACTIVE);
        person.setType(UserTypeE.PERSON);

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
    @DisplayName("Deve falhar quando usuário estiver inativo")
    void deveFalharQuandoUsuarioInativo() {
        Person person = new Person();
        person.setEmail("usuario@exemplo.com");
        person.setPassword("123");
        person.setStatus(UserStatus.INACTIVE);

        when(userRepository.findByEmail("usuario@exemplo.com")).thenReturn(person);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.login("usuario@exemplo.com", "senha123")
        );

        assertEquals("Usuário inativo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando usuário estiver pendente")
    void deveFalharQuandoUsuarioPendente() {
        Person person = new Person();
        person.setEmail("usuario@exemplo.com");
        person.setPassword("123");
        person.setStatus(UserStatus.PENDING);

        when(userRepository.findByEmail("usuario@exemplo.com")).thenReturn(person);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.login("usuario@exemplo.com", "senha123")
        );

        assertEquals("Usuário inativo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando senha for inválida")
    void deveFalharQuandoSenhaInvalida() {
        Person person = new Person();
        person.setEmail("test@email.com");
        person.setPassword("wrongpassword");
        person.setStatus(UserStatus.ACTIVE);

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
    @DisplayName("Deve falhar quando email tiver formato inválido")
    void deveFalharQuandoEmailFormatoInvalido() {
        // Para emails inválidos, a validação ocorre antes da busca no repositório
        // então não precisamos configurar mocks
        AuthenticationException ex1 = assertThrows(
                AuthenticationException.class,
                () -> loginService.login("emailinvalido", "senha123")
        );
        assertEquals("Formato de e-mail inválido", ex1.getMessage());

        AuthenticationException ex2 = assertThrows(
                AuthenticationException.class,
                () -> loginService.login("email@", "senha123")
        );
        assertEquals("Formato de e-mail inválido", ex2.getMessage());

        AuthenticationException ex3 = assertThrows(
                AuthenticationException.class,
                () -> loginService.login("@dominio.com", "senha123")
        );
        assertEquals("Formato de e-mail inválido", ex3.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando senha tiver formato inválido")
    void deveFalharQuandoSenhaFormatoInvalido() {
        // Para senhas inválidas, a validação ocorre antes da busca no repositório
        // então não precisamos configurar mocks
        AuthenticationException ex1 = assertThrows(
                AuthenticationException.class,
                () -> loginService.login("test@email.com", "123")
        );
        assertEquals("Formato de senha inválida", ex1.getMessage());

        AuthenticationException ex2 = assertThrows(
                AuthenticationException.class,
                () -> loginService.login("test@email.com", "senha{com}chaves")
        );
        assertEquals("Formato de senha inválida", ex2.getMessage());

        AuthenticationException ex3 = assertThrows(
                AuthenticationException.class,
                () -> loginService.login("test@email.com", "senha com espaços")
        );
        assertEquals("Formato de senha inválida", ex3.getMessage());
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
        person.setStatus(UserStatus.ACTIVE);

        when(jwtTokenProvider.isTokenValid("valid_refresh_token")).thenReturn(true);
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
    @DisplayName("Deve falhar ao renovar token with refresh token inválido")
    void deveFalharAoRenovarTokenComRefreshTokenInvalido() {
        String invalidRefreshToken = "invalid_refresh_token";

        when(jwtTokenProvider.isTokenValid(invalidRefreshToken)).thenReturn(false);

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

        when(jwtTokenProvider.isTokenValid(expiredRefreshToken)).thenReturn(false);

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

        when(jwtTokenProvider.isTokenValid(validRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken(validRefreshToken)).thenReturn("exemplo@user.com");
        when(userRepository.findByEmail("exemplo@user.com")).thenReturn(null);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.refreshToken(validRefreshToken)
        );

        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve fazer logout com tokens válidos e adicionar à blacklist")
    void deveFazerLogoutComTokensValidos() {
        String accessToken = "valid_access_token";
        String refreshToken = "valid_refresh_token";

        when(jwtTokenProvider.isTokenValid(accessToken)).thenReturn(true);
        when(jwtTokenProvider.isTokenValid(refreshToken)).thenReturn(true);

        Boolean result = loginService.logout(accessToken, refreshToken);

        // Verificar que o logout foi bem-sucedido
        assertNotNull(result);
        assertTrue(result);

        // Verificar que os tokens foram adicionados à blacklist
        verify(memoryBlacklistService).addToBlacklist(accessToken);
        verify(memoryBlacklistService).addToBlacklist(refreshToken);
    }

    @Test
    @DisplayName("Deve falhar ao fazer logout com access token inválido")
    void deveFalharAoFazerLogoutComAccessTokenInvalido() {
        String invalidAccessToken = "invalid_access_token";
        String validRefreshToken = "valid_refresh_token";

        when(jwtTokenProvider.isTokenValid(invalidAccessToken)).thenReturn(false);

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

        when(jwtTokenProvider.isTokenValid(validAccessToken)).thenReturn(true);
        when(jwtTokenProvider.isTokenValid(invalidRefreshToken)).thenReturn(false);

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

        when(jwtTokenProvider.isTokenValid(expiredAccessToken)).thenReturn(false);

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

        when(jwtTokenProvider.isTokenValid(validAccessToken)).thenReturn(true);
        when(jwtTokenProvider.isTokenValid(expiredRefreshToken)).thenReturn(false);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.logout(validAccessToken, expiredRefreshToken)
        );
        assertEquals("Refresh token inválido ou expirado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve validar token de recuperação com sucesso")
    void deveValidarTokenRecuperacaoComSucesso() {
        String validResetToken = "valid_reset_token";
        String email = "test@email.com";

        Person person = new Person();
        person.setId(UUID.randomUUID());
        person.setEmail(email);
        person.setStatus(UserStatus.ACTIVE);

        when(resetTokenService.validateResetToken(validResetToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(person);

        boolean result = loginService.validateResetToken(validResetToken);

        assertTrue(result);
        verify(resetTokenService).validateResetToken(validResetToken);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Deve falhar ao validar token quando token for nulo")
    void deveFalharAoValidarTokenQuandoTokenNulo() {
        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.validateResetToken(null)
        );

        assertEquals("Token de recuperação não fornecido", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao validar token quando token for vazio")
    void deveFalharAoValidarTokenQuandoTokenVazio() {
        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.validateResetToken("")
        );

        assertEquals("Token de recuperação não fornecido", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao validar token quando token for inválido ou expirado")
    void deveFalharAoValidarTokenQuandoTokenInvalidoOuExpirado() {
        String invalidOrExpiredToken = "invalid_or_expired_token";

        when(resetTokenService.validateResetToken(invalidOrExpiredToken)).thenReturn(null);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.validateResetToken(invalidOrExpiredToken)
        );

        assertEquals("Token de recuperação inválido ou expirado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao validar token quando usuário não for encontrado")
    void deveFalharAoValidarTokenQuandoUsuarioNaoEncontrado() {
        String validToken = "valid_token";
        String email = "notfound@email.com";

        when(resetTokenService.validateResetToken(validToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(null);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.validateResetToken(validToken)
        );

        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao validar token quando usuário estiver inativo")
    void deveFalharAoValidarTokenQuandoUsuarioInativo() {
        String validToken = "valid_token";
        String email = "inactive@email.com";

        Person person = new Person();
        person.setEmail(email);
        person.setStatus(UserStatus.INACTIVE);

        when(resetTokenService.validateResetToken(validToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(person);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.validateResetToken(validToken)
        );

        assertEquals("Usuário inativo", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao resetar senha se senha for nula")
    void resetSenhaDeveLancarExcecaoSeSenhaForNula() {
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> loginService.resetPassword("token", null));
        assertEquals("Nova senha não fornecida", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao resetar senha se senha for vazia")
    void resetSenhaDeveLancarExcecaoSeSenhaForVazia() {
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> loginService.resetPassword("token", ""));
        assertEquals("Nova senha não fornecida", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao ocorreu um erro inesperado durante reset de senha")
    void resetSenhaDeveLancarExcecaoSeErroInesperadoOcorrer() {
        User mockUser = mock(User.class);
        when(resetTokenService.validateResetToken("token")).thenReturn("person1@email.com");
        when(userRepository.findByEmail("person1@email.com")).thenReturn(mockUser);
        when(passwordEncoder.encode("password")).thenThrow(new RuntimeException("Erro"));

        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> loginService.resetPassword("token", "password"));
        assertEquals("Erro ao redefinir senha: Erro", exception.getMessage());
    }

    @Test
    @DisplayName("Deve resetar senha se forem informados parametros validos")
    void deveResetarSenha() {
        User mockUser = new Person();
        when(resetTokenService.validateResetToken("token")).thenReturn("person1@email.com");
        doNothing().when(resetTokenService).invalidateResetToken("token");
        when(userRepository.findByEmail("person1@email.com")).thenReturn(mockUser);
        when(passwordEncoder.encode("password")).thenReturn("encoded");

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userArgumentCaptor.capture())).thenReturn(mockUser);

        assertTrue(loginService.resetPassword("token", "password"));
        assertEquals("encoded", userArgumentCaptor.getValue().getPassword());
    }
}
