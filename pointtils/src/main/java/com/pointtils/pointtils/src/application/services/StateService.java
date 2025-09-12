package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.clients.IbgeClient;
import com.pointtils.pointtils.src.application.dto.StateResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StateService {

    private final IbgeClient ibgeClient;

    public StateResponseDTO getAllStates() {
        return new StateResponseDTO(true, "UFs encontradas com sucesso", ibgeClient.getStateList());
    }

    public StateResponseDTO getCitiesByState(String state) {
        return new StateResponseDTO(true, "Municípios encontrados com sucesso",
                ibgeClient.getCityListByState(state));
    }
}
