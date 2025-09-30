package com.pointtils.pointtils.src.application.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDataResponseDTO {

    private UUID id;
    private String name;
    private String picture;
    private String document;
    private BigDecimal rating;
    private List<SpecialtyResponseDTO> specialties;
}
