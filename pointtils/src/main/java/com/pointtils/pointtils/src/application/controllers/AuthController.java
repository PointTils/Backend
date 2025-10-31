package com.pointtils.pointtils.src.application.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.requests.LoginRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.PasswordRecoveryRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.RefreshTokenRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.LoginResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.RefreshTokenResponseDTO;
import com.pointtils.pointtils.src.application.services.AuthService;
import com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException;
import com.pointtils.pointtils.src.infrastructure.configs.LoginAttemptService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "Endpoints para autenticação de usuários")
public class AuthController {

    private final AuthService authService;

    private final LoginAttemptService loginAttemptService;

    @PostMapping("/login")
    @Operation(
            summary = "Realiza login de usuário",
            description = "Autentica o usuário com email e senha, retornando tokens de acesso e refresh"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de login inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletRequest httpRequest) {
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
    @Operation(
            summary = "Renova sessão de usuário",
            description = "Renova o token de acesso usando o refresh token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token renovado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RefreshTokenResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Refresh token não fornecido"),
            @ApiResponse(responseCode = "401", description = "Refresh token expirado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            throw new AuthenticationException("Refresh token não fornecido");
        }
        RefreshTokenResponseDTO response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @SuppressWarnings("rawtypes")
    @PostMapping("/logout")
    @Operation(
            summary = "Realiza logout de usuário",
            description = "Invalida os tokens de acesso e refresh do usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Tokens não fornecidos"),
            @ApiResponse(responseCode = "401", description = "Tokens de acesso ou refresh token inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity logout(@Valid @RequestBody RefreshTokenRequestDTO refreshToken, HttpServletRequest httpRequest) {
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
    @Operation(
            summary = "Recuperar senha usando token de recuperação",
            description = "Redefine a senha do usuário usando o token de recuperação enviado por email"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha recuperada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Token de recuperação ou nova senha inválida"),
            @ApiResponse(responseCode = "401", description = "Token de recuperação expirado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> recoverPassword(
            @Valid @RequestBody PasswordRecoveryRequestDTO request) {

        boolean success = authService.resetPassword(request.getResetToken(), request.getNewPassword());

        Map<String, Object> data = new HashMap<>();
        data.put("resetToken", request.getResetToken());

        return ResponseEntity.ok(ApiResponseDTO.success(
                success ? "Senha recuperada com sucesso" : "Falha ao recuperar senha",
                data
        ));
    }


    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

}
