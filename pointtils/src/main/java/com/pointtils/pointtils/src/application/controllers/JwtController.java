package com.pointtils.pointtils.src.application.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.TokensDTO;
import com.pointtils.pointtils.src.application.dto.requests.RefreshTokenRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.RefreshTokenResponseDTO;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jwt")
@Tag(name = "JWT Controller", description = "Endpoints para geração e teste de tokens JWT")
public class JwtController {
    private final JwtService jwtService;

    @GetMapping("/public")
    @Operation(
            summary = "Gera um token de acesso público",
            description = "Endpoint público que gera um token JWT para teste"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token gerado com sucesso",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<String> getPublicResource() {
        String token = jwtService.generateToken("user");
        return ResponseEntity.ok(token);
    }

    @GetMapping("/protected")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Recurso protegido que requer autenticação",
            description = "Endpoint protegido que verifica se o token JWT é válido"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Acesso autorizado",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<String> getProtectedResource() {
        return ResponseEntity.ok("Authenticated.");
    }

    @PostMapping("/generate-tokens")
    @Operation(
            summary = "Gera access token e refresh token para um usuário",
            description = "Gera novos tokens JWT (access e refresh) para um usuário específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens gerados com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RefreshTokenResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Nome de usuário inváido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<RefreshTokenResponseDTO> generateTokens(
            @Parameter(description = "Nome do usuário", required = true) @RequestParam String username) {
        String accessToken = jwtService.generateToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);
        TokensDTO tokensDTO = new TokensDTO(accessToken, refreshToken, "Bearer", jwtService.getExpirationTime(),
                jwtService.getRefreshExpirationTime());
        RefreshTokenResponseDTO response = new RefreshTokenResponseDTO(true, "Tokens gerados com sucesso",
                new RefreshTokenResponseDTO.Data(tokensDTO));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    @Operation(
            summary = "Gera um novo access token usando um refresh token válido",
            description = "Renova o access token utilizando um refresh token válido"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token renovado com sucesso",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = RefreshTokenResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Refresh token não fornecido"),
        @ApiResponse(responseCode = "401", description = "Refresh token inválido"),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        // Validar o refresh token e extrair o subject (username) dele
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            throw new com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException("Refresh token não fornecido");
        }

        if (!jwtService.isTokenValid(request.getRefreshToken())) {
            throw new com.pointtils.pointtils.src.core.domain.exceptions.AuthenticationException("Refresh token inválido ou expirado");
        }

        String username = jwtService.getEmailFromToken(request.getRefreshToken());
        String newAccessToken = jwtService.generateToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);

        TokensDTO tokensDTO = new TokensDTO(newAccessToken, newRefreshToken, "Bearer", jwtService.getExpirationTime(),
                jwtService.getRefreshExpirationTime());
        RefreshTokenResponseDTO response = new RefreshTokenResponseDTO(true, "Tokens gerados com sucesso",
                new RefreshTokenResponseDTO.Data(tokensDTO));
        return ResponseEntity.ok(response);
    }
}
