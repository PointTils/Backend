package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pointtils.pointtils.src.application.dto.LoginResponseDTO;
import com.pointtils.pointtils.src.application.dto.RefreshTokenResponseDTO;
import com.pointtils.pointtils.src.application.dto.TokensDTO;
import com.pointtils.pointtils.src.application.dto.UserDTO;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(String email, String password) {

        if (email == null || email.isBlank()) {
            throw new AuthenticationException("O campo email é obrigatório");
        }
        if (password == null || password.isBlank()) {
            throw new AuthenticationException("O campo senha é obrigatório");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new AuthenticationException("Formato de e-mail inválido");
        }

        if (!password.matches("^[a-zA-Z0-9!@#$%^&*()_+=-]{6,}$")) {
            throw new AuthenticationException("Formato de senha inválida");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("Usuário não encontrado");
        }

        if (UserStatus.INACTIVE.equals(user.getStatus())) {
            throw new AuthenticationException("Usuário inativo");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Credenciais inválidas");
        }

        String accessToken = jwtTokenProvider.generateToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getType().name(),
                user.getStatus().name()
        );

        TokensDTO tokensDTO = new TokensDTO(
                accessToken,
                refreshToken,
                "Bearer",
                jwtTokenProvider.getExpirationTime(),
                jwtTokenProvider.getRefreshExpirationTime()
        );

        return new LoginResponseDTO(
                true,
                "Autenticação realizada com sucesso",
                new LoginResponseDTO.Data(userDTO, tokensDTO)
        );
    }

    public RefreshTokenResponseDTO refreshToken(String token) {
        if (token == null || token.isBlank()) {
            throw new AuthenticationException("Refresh token não fornecido");
        }

        if (jwtTokenProvider.isTokenExpired(token) || !jwtTokenProvider.validateToken(token)) {
            throw new AuthenticationException("Refresh token inválido ou expirado");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("Usuário não encontrado");
        }

        String accessToken = jwtTokenProvider.generateToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return new RefreshTokenResponseDTO(
                true,
                "Token renovado com sucesso",
                new RefreshTokenResponseDTO.Data(new TokensDTO(
                        accessToken,
                        refreshToken,
                        "Bearer",
                        jwtTokenProvider.getExpirationTime(),
                        jwtTokenProvider.getRefreshExpirationTime()
                ))
        );
    }
}
