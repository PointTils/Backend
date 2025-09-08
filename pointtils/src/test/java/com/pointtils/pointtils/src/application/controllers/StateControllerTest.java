package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.StateDataDTO;
import com.pointtils.pointtils.src.application.dto.StateResponseDTO;
import com.pointtils.pointtils.src.application.services.StateService;
import com.pointtils.pointtils.src.infrastructure.configs.GlobalExceptionHandler;
import com.pointtils.pointtils.src.infrastructure.configs.JwtAuthenticationFilter;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = StateController.class)
@Import(JwtAuthenticationFilter.class)
class StateControllerTest {

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private StateService stateService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private StateController stateController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(stateController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .apply(springSecurity(jwtAuthenticationFilter))
                .build();
        when(jwtService.isTokenExpired(anyString())).thenReturn(Boolean.FALSE);
    }

    @Test
    @DisplayName("Deve retornar 200 e a lista de estados ao chamar o endpoint /v1/states")
    void shouldGetOkResponseForGetStatesEndpoint() throws Exception {
        StateResponseDTO mockResponse = new StateResponseDTO(true, "Sucesso",
                List.of(new StateDataDTO("RS")));

        when(stateService.getAllStates()).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/states")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sucesso"))
                .andExpect(jsonPath("$.data[0].name").value("RS"));
    }

    @Test
    @DisplayName("Deve retornar 200 e a lista de municípios ao chamar o endpoint /v1/states/{id}/cities")
    void shouldGetOkResponseForGetCitiesByStateEndpoint() throws Exception {
        StateResponseDTO mockResponse = new StateResponseDTO(true, "Sucesso",
                List.of(new StateDataDTO("Porto Alegre")));

        when(stateService.getCitiesByState("RS")).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/states/RS/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sucesso"))
                .andExpect(jsonPath("$.data[0].name").value("Porto Alegre"));
    }
}
