package com.pointtils.pointtils.src.application.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pointtils.pointtils.src.application.dto.RefreshTokenRequestDTO;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class JwtControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar um token público")
    void deveRetornarTokenPublico() throws Exception {
        when(jwtService.generateToken("user")).thenReturn("fake-jwt-token");

        mockMvc.perform(get("/api/jwt/public"))
                .andExpect(status().isOk())
                .andExpect(content().string("fake-jwt-token"));
    }

    @Test
    @DisplayName("Deve retornar recurso protegido")
    void deveRetornarRecursoProtegido() throws Exception {
        mockMvc.perform(get("/api/jwt/protected"))
                .andExpect(status().isOk())
                .andExpect(content().string("Authenticated."));
    }

    @Test
    @DisplayName("Deve gerar access token e refresh token para um usuário")
    void deveGerarAcessTokenERefreshTokenParaUmUsuario() throws Exception {
        String username = "testuser";
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(jwtService.generateToken(username)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(username)).thenReturn(refreshToken);

        mockMvc.perform(post("/api/jwt/generate-tokens")
                        .param("username", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokens.accessToken").value(accessToken))
                .andExpect(jsonPath("$.data.tokens.refreshToken").value(refreshToken));
    }

    @Test
    @DisplayName("Deve gerar novo access token a partir de um refresh token válido")
    void deveGerarNovoAcessTokenAPartirDeUmRefreshTokenValido() throws Exception {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("valid-refresh-token");
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        when(jwtService.generateToken("user")).thenReturn(newAccessToken);
        when(jwtService.generateRefreshToken("user")).thenReturn(newRefreshToken);

        mockMvc.perform(post("/api/jwt/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokens.accessToken").value(newAccessToken))
                .andExpect(jsonPath("$.data.tokens.refreshToken").value(newRefreshToken));
    }
}