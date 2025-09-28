package com.pointtils.pointtils.src.application.mapper;

import com.pointtils.pointtils.src.core.domain.entities.Enterprise;
import com.pointtils.pointtils.src.core.domain.entities.Specialty;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EnterpriseResponseMapperTest {

    @Spy
    private UserSpecialtyMapper userSpecialtyMapper = new UserSpecialtyMapper();
    @InjectMocks
    private EnterpriseResponseMapper enterpriseResponseMapper;

    @Test
    @DisplayName("Deve mapear dados da empresa para DTO de resposta")
    void shouldMapEnterpriseToDto() {
        UUID id = UUID.randomUUID();
        Specialty specialty = new Specialty("Intérprete de Libras");
        specialty.setId(UUID.randomUUID());
        Enterprise enterprise = Enterprise.builder()
                .id(id)
                .email("meuemail@gmail.com")
                .password("minhasenha123")
                .phone("54963636363")
                .picture("minhafoto")
                .status(UserStatus.ACTIVE)
                .type(UserTypeE.ENTERPRISE)
                .corporateReason("Empresa S.A.")
                .cnpj("12345678000199")
                .specialties(Set.of(specialty))
                .build();

        var actualDTO = enterpriseResponseMapper.toResponseDTO(enterprise);
        assertEquals(id, actualDTO.getId());
        assertEquals("Empresa S.A.", actualDTO.getCorporateReason());
        assertEquals("12345678000199", actualDTO.getCnpj());
        assertEquals("meuemail@gmail.com", actualDTO.getEmail());
        assertEquals("54963636363", actualDTO.getPhone());
        assertEquals("minhafoto", actualDTO.getPicture());
        assertEquals(UserStatus.ACTIVE.name(), actualDTO.getStatus());
        assertEquals(UserTypeE.ENTERPRISE.name(), actualDTO.getType());
        assertThat(actualDTO.getSpecialties())
                .hasSize(1)
                .anyMatch(s -> "Intérprete de Libras".equals(s.getName()));
    }
}
