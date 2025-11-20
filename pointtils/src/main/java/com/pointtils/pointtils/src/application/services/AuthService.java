package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.TokensDTO;
import com.pointtils.pointtils.src.application.dto.responses.LoginResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.RefreshTokenResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserLoginResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;
import com.pointtils.pointtils.src.infrastructure.configs.MemoryBlacklistService;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String USER_NOT_FOUND_MESSAGE = "Usuário não encontrado";

    private final UserRepository userRepository;
    private final JwtService jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemoryBlacklistService memoryBlacklistService;
    private final MemoryResetTokenService resetTokenService;

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
            throw new AuthenticationException(USER_NOT_FOUND_MESSAGE);
        }

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new AuthenticationException("Usuário inativo");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Credenciais inválidas");
        }

        String accessToken = jwtTokenProvider.generateToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        UserLoginResponseDTO userDTO = new UserLoginResponseDTO(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhone(),
                user.getPicture(),
                user.getType(),
                user.getStatus()
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

        if (!jwtTokenProvider.isTokenValid(token)) {
            throw new AuthenticationException("Refresh token inválido ou expirado");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException(USER_NOT_FOUND_MESSAGE);
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

    public Boolean logout(String accessToken, String refreshToken) {
        if (!jwtTokenProvider.isTokenValid(accessToken)) {
            throw new AuthenticationException("Access token inválido ou expirado");
        }
        if (!jwtTokenProvider.isTokenValid(refreshToken)) {
            throw new AuthenticationException("Refresh token inválido ou expirado");
        }

        memoryBlacklistService.addToBlacklist(accessToken);
        memoryBlacklistService.addToBlacklist(refreshToken);

        return true;
    }
	
    public boolean validateResetToken(String resetToken) {
        if (resetToken == null || resetToken.isBlank()) {
            throw new AuthenticationException("Token de recuperação não fornecido");
        }

        String email = resetTokenService.validateResetToken(resetToken);

        if (email == null) {
            throw new AuthenticationException("Token de recuperação inválido ou expirado");
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new AuthenticationException(USER_NOT_FOUND_MESSAGE);
        }

        if (UserStatus.INACTIVE.equals(user.getStatus())) {
            throw new AuthenticationException("Usuário inativo");
        }

        log.info("Token de recuperação validado com sucesso para o usuário: {}", email);
        return true;
    }

    /**
     * Redefine a senha do usuário usando um token de recuperação
     *
     * @param resetToken  Token de recuperação
     * @param newPassword Nova senha
     * @return true se a senha foi redefinida com sucesso, false caso contrário
     */
    public boolean resetPassword(String resetToken, String newPassword) {
        if (StringUtils.isBlank(newPassword)) {
            throw new AuthenticationException("Nova senha não fornecida");
        }

        if (!newPassword.matches("^[a-zA-Z0-9!@#$%^&*()_+=-]{6,}$")) {
            throw new AuthenticationException("Formato de senha inválida");
        }

		validateResetToken(resetToken);
		
        String email = resetTokenService.validateResetToken(resetToken);
		User user = userRepository.findByEmail(email);
		
        try {
            user.setPassword(passwordEncoder.encode(newPassword));

            userRepository.save(user);

            resetTokenService.invalidateResetToken(resetToken);

            log.info("Senha redefinida com sucesso para o usuário: {}", email);
            return true;

        } catch (Exception e) {
            log.error("Erro ao redefinir senha para o usuário {}: {}", email, e.getMessage());
            throw new AuthenticationException("Erro ao redefinir senha: " + e.getMessage());
        }
    }


}
