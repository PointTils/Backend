package com.pointtils.pointtils.src.application.services;

import org.springframework.stereotype.Service;

import com.pointtils.pointtils.src.application.dto.LoginResponseDTO;
import com.pointtils.pointtils.src.application.dto.TokensDTO;
import com.pointtils.pointtils.src.application.dto.UserDTO;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    // private final JwtService jwtTokenPrivider;
    // private final PasswordEncoder passwordEncoder;
    
    public LoginResponseDTO login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("Usuário não encontrado");
        }

        // if (!passwordEncoder.matches(password, user.getPassword())) {
        //     throw new AuthenticationException("Credenciais inválidas");
        // }

        if ("blocked".equals(user.getStatus())) {
            throw new AuthenticationException("Usuário bloqueado");
        }

        // ToDo: Integrar com geração de token JWT criada com o middleware 
        String accessToken = "token_super_maneiro"; // jwtTokenProvider.generateAccessToken(user);
        String refreshToken = "token_ainda_mais_maneiro"; // jwtTokenProvider.generateRefreshToken(user);

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
                3600, // jwtProvider.getAccessTokenExpirationSeconds(),
                604800 // jwtProvider.getRefreshTokenExpirationSeconds()
        );

        return new LoginResponseDTO(
                true,
                "Autenticação realizada com sucesso",
                new LoginResponseDTO.Data(userDTO, tokensDTO)
        );
    }
}
