package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.clients.IbgeClient;
import com.pointtils.pointtils.src.application.dto.StateDataDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StateServiceTest {

    @Mock
    private IbgeClient ibgeClient;
    @InjectMocks
    private StateService stateService;

    @Test
    @DisplayName("Deve buscar todas as UFs")
    void shouldGetAllStates() {
        var firstMockedData = new StateDataDTO("RS");
        var secondMockedData = new StateDataDTO("SC");
        when(ibgeClient.getStateList()).thenReturn(List.of(firstMockedData, secondMockedData));

        var response = stateService.getAllStates();
        assertTrue(response.isSuccess());
        assertEquals("UFs encontradas com sucesso", response.getMessage());
        assertThat(response.getData())
                .hasSize(2)
                .containsExactly(firstMockedData, secondMockedData);
    }

    @Test
    @DisplayName("Deve buscar todos os municípios de uma determinada UF")
    void shouldGetCitiesByState() {
        var firstMockedData = new StateDataDTO("Porto Alegre");
        when(ibgeClient.getCityListByState("RS")).thenReturn(List.of(firstMockedData));

        var response = stateService.getCitiesByState("RS");
        assertTrue(response.isSuccess());
        assertEquals("Municípios encontrados com sucesso", response.getMessage());
        assertThat(response.getData())
                .hasSize(1)
                .containsExactly(firstMockedData);
    }
}
