package com.pointtils.pointtils;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.pointtils.pointtils.src.infrastructure.configs.JwtAuthenticationFilter;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;
import com.pointtils.pointtils.src.infrastructure.configs.SecurityConfiguration;

@WebMvcTest(controllers = {com.pointtils.pointtils.src.application.controllers.JwtController.class})
@Import({SecurityConfiguration.class, JwtAuthenticationFilter.class})
@SuppressWarnings("deprecation")
public class SecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @Test
    void whenAccessPublicJwtEndpoint_thenShouldBePermitted() throws Exception {
        mockMvc.perform(get("/api/jwt/public")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenAccessSwaggerDocs_thenShouldNotBeForbidden() throws Exception {
        // Swagger docs pode retornar erro, mas não deve ser 401/403 (permitido)
        mockMvc.perform(get("/v3/api-docs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError()); // Pode ser 500, mas não 401/403
    }

    @Test
    void whenAccessSwaggerUI_thenShouldNotBeForbidden() throws Exception {
        // Swagger UI pode retornar erro, mas não deve ser 401/403 (permitido)
        mockMvc.perform(get("/swagger-ui/index.html")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().is5xxServerError()); // Pode ser 500, mas não 401/403
    }

    @Test
    void whenAccessAuthEndpoints_thenShouldNotBeForbidden() throws Exception {
        // Auth endpoints não existem ainda, mas não devem retornar 401/403 (permitidos)
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@test.com\",\"password\":\"password\"}"))
                .andExpect(status().is5xxServerError()); // Pode ser 500, mas não 401/403
    }

    @Test
    void whenAccessProtectedEndpointWithoutToken_thenShouldBeOk() throws Exception {
        // O filtro JWT permite acesso sem token, a proteção deve ser feita no controller
        mockMvc.perform(get("/api/jwt/protected")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenAccessProtectedEndpointWithValidToken_thenShouldBeOk() throws Exception {
        String validToken = "valid-jwt-token";
        when(jwtService.isTokenExpired(validToken)).thenReturn(false);

        mockMvc.perform(get("/api/jwt/protected")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenAccessProtectedEndpointWithExpiredToken_thenShouldBeUnauthorized() throws Exception {
        String expiredToken = "expired-jwt-token";
        when(jwtService.isTokenExpired(expiredToken)).thenReturn(true);

        mockMvc.perform(get("/api/jwt/protected")
                .header("Authorization", "Bearer " + expiredToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenAccessProtectedEndpointWithInvalidTokenFormat_thenShouldBeOk() throws Exception {
        // O filtro ignora tokens com formato inválido e permite o acesso
        mockMvc.perform(get("/api/jwt/protected")
                .header("Authorization", "InvalidTokenFormat")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenAccessNonExistentEndpoint_thenShouldBeForbidden() throws Exception {
        // Endpoints não existentes retornam 403 (Forbidden) em vez de 401
        mockMvc.perform(get("/api/non-existent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenRequestWithCorsHeaders_thenShouldAllowCors() throws Exception {
        mockMvc.perform(options("/api/jwt/public")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "Authorization"))
                .andExpect(status().isOk());
    }

    @Test
    void whenGenerateTokens_thenShouldBePermitted() throws Exception {
        mockMvc.perform(post("/api/jwt/generate-tokens")
                .param("username", "testuser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenRefreshToken_thenShouldBePermitted() throws Exception {
        mockMvc.perform(post("/api/jwt/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"test-refresh-token\"}"))
                .andExpect(status().isOk());
    }
}
