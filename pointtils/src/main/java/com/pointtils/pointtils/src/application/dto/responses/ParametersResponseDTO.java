package com.pointtils.pointtils.src.application.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ParametersResponseDTO {
	private UUID id;
	private String key;
	private String value;
	
}
