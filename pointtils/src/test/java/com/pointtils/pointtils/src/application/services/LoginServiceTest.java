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
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;
import com.pointtils.pointtils.src.infrastructure.configs.LoginAttemptService;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtTokenProvider;

    @Mock
    private LoginAttemptService loginAttemptService;
    
    @InjectMocks
    private LoginService loginService;

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
                () -> loginService.login("emailnaoexiste@email.com", "senha")
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
                () -> loginService.login("usuario@exemplo.com", "123")
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
}
