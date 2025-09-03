package com.pointtils.pointtils.src.application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.RefreshTokenRequestDTO;
import com.pointtils.pointtils.src.application.dto.RefreshTokenResponseDTO;
import com.pointtils.pointtils.src.application.dto.TokensDTO;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/jwt")
@Tag(name = "JWT Controller", description = "Endpoints para geração e teste de tokens JWT")
public class JwtController {

    @Autowired
    private JwtService jwtService;

    @GetMapping("/public")
    @Operation(summary = "Gera um token de acesso público")
    public ResponseEntity<String> getPublicResource() {
        String token = jwtService.generateToken("user");
        return ResponseEntity.ok(token);
    }

    @GetMapping("/protected")
    @Operation(summary = "Recurso protegido que requer autenticação")
    public ResponseEntity<String> getProtectedResource() {
        return ResponseEntity.ok("Authenticated.");
    }

    @PostMapping("/generate-tokens")
    @Operation(summary = "Gera access token e refresh token para um usuário")
    public ResponseEntity<RefreshTokenResponseDTO> generateTokens(@RequestParam String username) {
        String accessToken = jwtService.generateToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);

        TokensDTO tokensDTO = new TokensDTO(accessToken, refreshToken, "Bearer", jwtService.getExpirationTime(),
                jwtService.getRefreshExpirationTime());
        RefreshTokenResponseDTO response = new RefreshTokenResponseDTO(true, "Tokens gerados com sucesso",
                new RefreshTokenResponseDTO.Data(tokensDTO));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Gera um novo access token usando um refresh token válido")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        // Para simplificar, vamos assumir que qualquer refresh token válido pode gerar
        // um novo access token
        // Em uma implementação real, você validaria o refresh token e extrairia o
        // subject dele
        String username = "user"; // Em produção, extrair do refresh token
        String newAccessToken = jwtService.generateToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);

        TokensDTO tokensDTO = new TokensDTO(newAccessToken, newRefreshToken, "Bearer", jwtService.getExpirationTime(),
                jwtService.getRefreshExpirationTime());
        RefreshTokenResponseDTO response = new RefreshTokenResponseDTO(true, "Tokens gerados com sucesso",
                new RefreshTokenResponseDTO.Data(tokensDTO));
        return ResponseEntity.ok(response);
    }
}
