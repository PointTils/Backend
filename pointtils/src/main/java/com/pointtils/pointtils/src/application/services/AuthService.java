package com.pointtils.pointtils.src.application.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pointtils.pointtils.src.application.dto.LoginResponseDTO;
import com.pointtils.pointtils.src.application.dto.RefreshTokenResponseDTO;
import com.pointtils.pointtils.src.application.dto.TokensDTO;
import com.pointtils.pointtils.src.application.dto.UserDTO;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;
import com.pointtils.pointtils.src.infrastructure.configs.RedisBlacklistService;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtTokenPrivider;
    private final PasswordEncoder passwordEncoder;
    private final RedisBlacklistService redisBlacklistService;

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

        if ("blocked".equals(user.getStatus())) {
            throw new AuthenticationException("Usuário bloqueado");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
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

    public RefreshTokenResponseDTO refreshToken(String token) {
        if (token == null || token.isBlank()) {
            throw new AuthenticationException("Refresh token não fornecido");
        }

        if (jwtTokenPrivider.isTokenExpired(token) || !jwtTokenPrivider.validateToken(token)) {
            throw new AuthenticationException("Refresh token inválido ou expirado");
        }

        String email = jwtTokenPrivider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("Usuário não encontrado");
        }

        String accessToken = jwtTokenPrivider.generateToken(user.getEmail());
        String refreshToken = jwtTokenPrivider.generateRefreshToken(user.getEmail());

        return new RefreshTokenResponseDTO(
                true,
                "Token renovado com sucesso",
                new RefreshTokenResponseDTO.Data(new TokensDTO(
                        accessToken,
                        refreshToken,
                        "Bearer",
                        jwtTokenPrivider.getExpirationTime(),
                        jwtTokenPrivider.getRefreshExpirationTime()
                ))
        );
    }

    public Boolean logout(String accessToken, String refreshToken) {
        if (!jwtTokenPrivider.validateToken(accessToken) || jwtTokenPrivider.isTokenExpired(accessToken)) {
            throw new AuthenticationException("Access token inválido ou expirado");
        }
        if (!jwtTokenPrivider.validateToken(refreshToken) || jwtTokenPrivider.isTokenExpired(refreshToken)) {
            throw new AuthenticationException("Refresh token inválido ou expirado");
        }

        redisBlacklistService.addToBlacklist(accessToken);

        return true;
    }
}
