package com.pointtils.pointtils.src.application.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParametersPatchRequestDTO {
	
    private String key;
    private String value;
}
