package com.pointtils.pointtils.src.application.clients;

import com.pointtils.pointtils.src.application.dto.responses.CityIbgeResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.StateIbgeResponseDTO;
import com.pointtils.pointtils.src.core.domain.exceptions.ClientTimeoutException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IbgeClientTest {

    @Mock
    private RestTemplate restTemplate;
    private IbgeClient ibgeClient;

    @BeforeEach
    void setUp() {
        this.ibgeClient = new IbgeClient(restTemplate, "http://exemplo.com.br/estados",
                "http://exemplo.com.br/estados/{state}/municipios");
    }

    @Test
    @DisplayName("Deve obter da API externa a lista de UFs em ordem alfabética")
    void shouldGetStateListInAlphabeticalOrder() {
        var mockRsResponse = new StateIbgeResponseDTO(1L, "RS", "Rio Grande do Sul");
        var mockAcreResponse = new StateIbgeResponseDTO(2L, "AC", "Acre");
        when(restTemplate.getForEntity("http://exemplo.com.br/estados", StateIbgeResponseDTO[].class))
                .thenReturn(ResponseEntity.ok(new StateIbgeResponseDTO[]{mockRsResponse, mockAcreResponse}));

        var actualResponse = ibgeClient.getStateList();
        assertEquals(2, actualResponse.size());
        assertEquals("AC", actualResponse.get(0).getName());
        assertEquals("RS", actualResponse.get(1).getName());
    }

    @Test
    @DisplayName("Deve lancar EntityNotFoundException se API externa retornar lista de UFs nula")
    void shouldGetEntityNotFoundExceptionIfStateListIsNull() {
        when(restTemplate.getForEntity("http://exemplo.com.br/estados", StateIbgeResponseDTO[].class))
                .thenReturn(ResponseEntity.ok(null));

        assertThrows(EntityNotFoundException.class, () -> ibgeClient.getStateList());
    }

    @Test
    @DisplayName("Deve lancar EntityNotFoundException se API externa retornar lista de UFs vazia")
    void shouldGetEntityNotFoundExceptionIfStateListIsEmpty() {
        when(restTemplate.getForEntity("http://exemplo.com.br/estados", StateIbgeResponseDTO[].class))
                .thenReturn(ResponseEntity.ok(new StateIbgeResponseDTO[]{}));

        assertThrows(EntityNotFoundException.class, () -> ibgeClient.getStateList());
    }

    @Test
    @DisplayName("Deve lancar ClientTimeoutException se ocorrer timeout em chamada da API externa para obter UFs")
    void shouldGetClientTimeoutExceptionIfStateListRequestTimeout() {
        when(restTemplate.getForEntity("http://exemplo.com.br/estados", StateIbgeResponseDTO[].class))
                .thenThrow(new ResourceAccessException("Erro", new SocketTimeoutException("Timeout")));

        assertThrows(ClientTimeoutException.class, () -> ibgeClient.getStateList());
    }

    @Test
    @DisplayName("Deve obter da API externa a lista de municípios por UF em ordem alfabética")
    void shouldGetCityListInAlphabeticalOrder() {
        var mockCityResponse1 = new CityIbgeResponseDTO(1L, "Porto Alegre");
        var mockCityResponse2 = new CityIbgeResponseDTO(2L, "Canoas");
        when(restTemplate.getForEntity("http://exemplo.com.br/estados/{state}/municipios", CityIbgeResponseDTO[].class, "RS"))
                .thenReturn(ResponseEntity.ok(new CityIbgeResponseDTO[]{mockCityResponse1, mockCityResponse2}));

        var actualResponse = ibgeClient.getCityListByState("RS");
        assertEquals(2, actualResponse.size());
        assertEquals("Canoas", actualResponse.get(0).getName());
        assertEquals("Porto Alegre", actualResponse.get(1).getName());
    }

    @Test
    @DisplayName("Deve lancar EntityNotFoundException se API externa retornar lista de municípios nula")
    void shouldGetEntityNotFoundExceptionIfCityListIsNull() {
        when(restTemplate.getForEntity("http://exemplo.com.br/estados/{state}/municipios", CityIbgeResponseDTO[].class, "RS"))
                .thenReturn(ResponseEntity.ok(null));

        assertThrows(EntityNotFoundException.class, () -> ibgeClient.getCityListByState("RS"));
    }

    @Test
    @DisplayName("Deve lancar EntityNotFoundException se API externa retornar lista de municípios vazia")
    void shouldGetEntityNotFoundExceptionIfCityListIsEmpty() {
        when(restTemplate.getForEntity("http://exemplo.com.br/estados/{state}/municipios", CityIbgeResponseDTO[].class, "RS"))
                .thenReturn(ResponseEntity.ok(new CityIbgeResponseDTO[]{}));

        assertThrows(EntityNotFoundException.class, () -> ibgeClient.getCityListByState("RS"));
    }

    @Test
    @DisplayName("Deve lancar ClientTimeoutException se ocorrer timeout em chamada da API externa para obter municípios")
    void shouldGetClientTimeoutExceptionIfCityListRequestTimeout() {
        when(restTemplate.getForEntity("http://exemplo.com.br/estados/{state}/municipios", CityIbgeResponseDTO[].class, "RS"))
                .thenThrow(new ResourceAccessException("Erro", new SocketTimeoutException("Timeout")));

        assertThrows(ClientTimeoutException.class, () -> ibgeClient.getCityListByState("RS"));
    }
}
