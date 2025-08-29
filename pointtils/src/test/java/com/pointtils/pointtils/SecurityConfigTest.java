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

@WebMvcTest(controllers = {com.pointtils.pointtils.src.application.controllers.JwtTestController.class})
@Import({SecurityConfiguration.class, JwtAuthenticationFilter.class})
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;


    @Test
    void whenUnauthenticated_thenShouldBeUnauthorized() throws Exception {

        mockMvc.perform(get("/api/private")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenAuthenticatedWithValidToken_thenShouldBeOk() throws Exception {

        String validToken = "valid-jwt-token";
        when(jwtService.isTokenExpired(validToken)).thenReturn(false);

        mockMvc.perform(get("/api/public")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}