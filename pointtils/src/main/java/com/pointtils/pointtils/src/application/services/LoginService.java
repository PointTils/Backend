package com.pointtils.pointtils.src.application.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pointtils.pointtils.src.application.dto.LoginResponseDTO;
import com.pointtils.pointtils.src.application.dto.TokensDTO;
import com.pointtils.pointtils.src.application.dto.UserDTO;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;
import com.pointtils.pointtils.src.infrastructure.configs.LoginAttemptService;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final JwtService jwtTokenPrivider;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    public LoginResponseDTO login(String email, String password) {

        if (loginAttemptService.isBlocked(email)) {
            throw new AuthenticationException("Muitas tentativas de login");
        }

        if (email == null || email.isBlank()) {
            throw new AuthenticationException("O campo email é obrigatório");
        }
        if (password == null || password.isBlank()) {
            throw new AuthenticationException("O campo senha é obrigatório");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("Usuário não encontrado");
        }

        if ("blocked".equals(user.getStatus())) {
            throw new AuthenticationException("Usuário bloqueado");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            loginAttemptService.loginFailed(email);
            throw new AuthenticationException("Credenciais inválidas");
        }

        String accessToken = jwtTokenPrivider.generateToken(user.getEmail());
        String refreshToken = jwtTokenPrivider.generateRefreshToken(user.getEmail());

        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getType(),
                user.getStatus()
        );

        TokensDTO tokensDTO = new TokensDTO(
                accessToken,
                refreshToken,
                "Bearer",
                jwtTokenPrivider.getExpirationTime(),
                jwtTokenPrivider.getRefreshExpirationTime()
        );

        return new LoginResponseDTO(
                true,
                "Autenticação realizada com sucesso",
                new LoginResponseDTO.Data(userDTO, tokensDTO)
        );
    }
}
