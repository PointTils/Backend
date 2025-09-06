package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.clients.IbgeClient;
import com.pointtils.pointtils.src.application.dto.LocationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final IbgeClient ibgeClient;

    public LocationResponseDTO getAllStates() {
        return new LocationResponseDTO(true, "UFs encontradas com sucesso", ibgeClient.getStateList());
    }

    public LocationResponseDTO getCitiesByState(String state) {
        return new LocationResponseDTO(true, "Municípios encontrados com sucesso",
                ibgeClient.getCityListByState(state));
    }
}
