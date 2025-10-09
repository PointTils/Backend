package com.pointtils.pointtils.src.application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.requests.LoginRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.LoginResponseDTO;
import com.pointtils.pointtils.src.application.dto.requests.RefreshTokenRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.RefreshTokenResponseDTO;
import com.pointtils.pointtils.src.application.dto.requests.PasswordRecoveryRequestDTO;
import com.pointtils.pointtils.src.application.services.AuthService;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.infrastructure.configs.LoginAttemptService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "Endpoints para autenticação de usuários")
public class AuthController {

    private final AuthService authService;

    private final LoginAttemptService loginAttemptService;

    @PostMapping("/login")
    @Operation(summary = "Realiza login de usuário")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest, HttpServletRequest httpRequest) {
        String clientIp = getClientIP(httpRequest);

        if (loginAttemptService.isBlocked(clientIp)) {
            throw new AuthenticationException("Muitas tentativas de login");
        }

        try {
            LoginResponseDTO response = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            loginAttemptService.loginSucceeded(clientIp);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            loginAttemptService.loginFailed(clientIp);
            throw e;
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renova sessão de usuário")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            throw new AuthenticationException("Refresh token não fornecido");
        }
        RefreshTokenResponseDTO response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @SuppressWarnings("rawtypes")
    @PostMapping("/logout")
    @Operation(summary = "Realiza logout de usuário")
    public ResponseEntity logout(@RequestBody RefreshTokenRequestDTO refreshToken, HttpServletRequest httpRequest) {
        String header = httpRequest.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ") || header.length() <= 7) {
            throw new AuthenticationException("Access token não fornecido");
        }
        if (refreshToken.getRefreshToken() == null || refreshToken.getRefreshToken().isBlank()) {
            throw new AuthenticationException("Refresh token não fornecido");
        }

        String accessToken = header.substring(7);
        boolean success = authService.logout(accessToken, refreshToken.getRefreshToken());

        if (!success) {
            throw new InternalError("Erro ao fazer logout");
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping("/recover-password")
    @Operation(summary = "Recuperar senha usando token de recuperação")
    public ResponseEntity<Map<String, Object>> recoverPassword(@RequestBody PasswordRecoveryRequestDTO request) {
        boolean success = authService.resetPassword(request.getResetToken(), request.getNewPassword());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "Senha recuperada com sucesso" : "Falha ao recuperar senha");
        
        return ResponseEntity.ok(response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

}
