package com.pointtils.pointtils.src.application.dto;

import com.pointtils.pointtils.src.core.domain.entities.Person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
// @NoArgsConstructor
@AllArgsConstructor


public class SurdoResponseDTO {

    public SurdoResponseDTO ( Person person) {
    }
}
