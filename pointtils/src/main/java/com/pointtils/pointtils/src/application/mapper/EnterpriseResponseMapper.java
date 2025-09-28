package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.application.dto.responses.EnterpriseResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Enterprise;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnterpriseResponseMapper {

    private final UserSpecialtyMapper userSpecialtyMapper;

    public EnterpriseResponseDTO toResponseDTO(Enterprise enterprise) {
        return EnterpriseResponseDTO.builder()
                .id(enterprise.getId())
                .corporateReason(enterprise.getCorporateReason())
                .cnpj(enterprise.getCnpj())
                .email(enterprise.getEmail())
                .phone(enterprise.getPhone())
                .picture(enterprise.getPicture())
                .status(enterprise.getStatus().name())
                .type(enterprise.getType().name())
                .specialties(userSpecialtyMapper.toDtoList(enterprise.getSpecialties()))
                .build();
    }
}

