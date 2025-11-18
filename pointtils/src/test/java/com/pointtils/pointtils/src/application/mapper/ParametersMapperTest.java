package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.requests.ParametersBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ParametersResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Parameters;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParametersMapperTest {

    private final ParametersMapper parametersMapper = new ParametersMapper();

    @Test
    void shouldMapToEntity() {
        ParametersBasicRequestDTO requestDTO = new ParametersBasicRequestDTO();
        requestDTO.setKey("KEY");
        requestDTO.setValue("VALUE");


        Parameters mappedParameter = parametersMapper.toEntity(requestDTO);
        assertEquals("KEY", mappedParameter.getKey());
        assertEquals("VALUE", mappedParameter.getValue());
    }

    @Test
    void shouldMapToResponseDTO() {
        UUID parameterId = UUID.randomUUID();
        Parameters parameter = Parameters.builder()
                .id(parameterId)
                .key("KEY")
                .value("VALUE")
                .build();

        ParametersResponseDTO responseDTO = parametersMapper.toResponseDTO(parameter);
        assertEquals(parameterId, responseDTO.getId());
        assertEquals("KEY", responseDTO.getKey());
        assertEquals("VALUE", responseDTO.getValue());
    }
}
