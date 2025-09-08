package com.pointtils.pointtils.src.application.controllers;

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
public class AuthController {

    private final AuthService authService;

    private final LoginAttemptService loginAttemptService;

    @PostMapping("/login")
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
    public ResponseEntity<RefreshTokenResponseDTO> postMethodName(@RequestBody String refresh_token) {
        RefreshTokenResponseDTO response = authService.refreshToken(refresh_token);
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
