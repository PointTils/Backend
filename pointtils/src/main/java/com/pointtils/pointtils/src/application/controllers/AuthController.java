package com.pointtils.pointtils.src.application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.LoginRequestDTO;
import com.pointtils.pointtils.src.application.dto.LoginResponseDTO;
import com.pointtils.pointtils.src.application.dto.RefreshTokenResponseDTO;
import com.pointtils.pointtils.src.application.services.AuthService;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.infrastructure.configs.LoginAttemptService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

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
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@RequestBody String token) {
        RefreshTokenResponseDTO response = authService.refreshToken(token);
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
