package com.pointtils.pointtils.src.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StateResponseDTO {

    private boolean success;
    private String message;
    private List<StateDataDTO> data;
}
