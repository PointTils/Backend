package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.requests.ParametersBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ParametersResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Parameters;
import org.springframework.stereotype.Component;

@Component
public class ParametersMapper {
	public Parameters toEntity(ParametersBasicRequestDTO dto) {
		return Parameters.builder()
				.key(dto.getKey())
				.value(dto.getValue())
				.build();
	}
	
	public ParametersResponseDTO toResponseDTO(Parameters parameters) {
		return ParametersResponseDTO.builder()
				.id(parameters.getId())
				.key(parameters.getKey())
				.value(parameters.getValue())
				.build();
	}
}
