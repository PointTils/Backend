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

import com.pointtils.pointtils.src.application.dto.LoginRequestDTO;
import com.pointtils.pointtils.src.application.dto.LoginResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    // TODO - Integrar com JwtService ao fazer merge
    // @Mock
    // private JwtService jwtService;
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
        // when(jwtService.generateToken(person)).thenReturn("mocked_jwt_token");
        // when(jwtService.generateRefreshToken(person)).thenReturn("fakeRefreshToken");

        LoginRequestDTO request = new LoginRequestDTO("test@email.com", "password123");

        LoginResponseDTO response = loginService.login(request.getEmail(), request.getPassword());

        assertNotNull(response);
        assertEquals("test@email.com", response.getData().user().getEmail());
        // assertEquals("fakeRefreshToken", response.getData().tokens().getAccess_token());
    }

    @Test
    void deveFalharQuandoUsuarioNaoEncontrado() {
        when(userRepository.findByEmail("emailnaoexiste@email.com")).thenReturn(null);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> loginService.login("emailnaoexiste@email.com", "senha")
        );

        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
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
}
