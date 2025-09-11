package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.EnterprisePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.EnterpriseRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.EnterpriseResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Enterprise;
import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.core.domain.exceptions.DuplicateResourceException;
import com.pointtils.pointtils.src.infrastructure.repositories.EnterpriseRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnterpriseServiceTest {
    @Mock
    private EnterpriseRepository enterpriseRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private EnterpriseService enterpriseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerEnterprise_shouldSaveAndReturnResponseDTO() {
        EnterpriseRequestDTO dto = new EnterpriseRequestDTO();
        dto.setCorporateReason("Corp");
        dto.setCnpj("12345678901234");
        dto.setEmail("test@corp.com");
        dto.setPassword("pass");
        dto.setPhone("123456789");
        dto.setPicture("pic.png");
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setUf("RS");
        locationDTO.setCity("Porto Alegre");
        dto.setLocation(locationDTO);

        when(enterpriseRepository.existsByCnpj(dto.getCnpj())).thenReturn(false);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded");
        when(enterpriseRepository.save(any(Enterprise.class))).thenAnswer(i -> i.getArgument(0));

        EnterpriseResponseDTO response = enterpriseService.registerEnterprise(dto);
        assertEquals(dto.getCorporateReason(), response.getCorporateReason());
        assertEquals(dto.getCnpj(), response.getCnpj());
        assertEquals(dto.getEmail(), response.getEmail());
        assertEquals(dto.getPhone(), response.getPhone());
        assertEquals(dto.getPicture(), response.getPicture());
    }

    @Test
    void registerEnterprise_shouldThrowDuplicateCnpj() {
        EnterpriseRequestDTO dto = new EnterpriseRequestDTO();
        dto.setCnpj("123");
        when(enterpriseRepository.existsByCnpj(dto.getCnpj())).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> enterpriseService.registerEnterprise(dto));
    }

    @Test
    void registerEnterprise_shouldThrowDuplicateEmail() {
        EnterpriseRequestDTO dto = new EnterpriseRequestDTO();
        dto.setCnpj("123");
        dto.setEmail("mail");
        when(enterpriseRepository.existsByCnpj(dto.getCnpj())).thenReturn(false);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> enterpriseService.registerEnterprise(dto));
    }

    @Test
    void findAll_shouldReturnListOfActiveEnterprises() {
        Enterprise ent = Enterprise.builder()
                .corporateReason("Corp")
                .cnpj("123")
                .email("mail")
                .status(UserStatus.ACTIVE)
                .type(UserTypeE.ENTERPRISE)
                .build();
        when(enterpriseRepository.findAllByStatus(UserStatus.ACTIVE)).thenReturn(List.of(ent));
        List<EnterpriseResponseDTO> result = enterpriseService.findAll();
        assertEquals(1, result.size());
        assertEquals("Corp", result.get(0).getCorporateReason());
    }

    @Test
    void findById_shouldReturnEnterpriseResponseDTO() {
        UUID id = UUID.randomUUID();
        Enterprise ent = Enterprise.builder()
                .corporateReason("Corp")
                .cnpj("123")
                .email("mail")
                .status(UserStatus.ACTIVE)
                .type(UserTypeE.ENTERPRISE)
                .build();
        when(enterpriseRepository.findByIdAndStatus(id, UserStatus.ACTIVE)).thenReturn(Optional.of(ent));
        EnterpriseResponseDTO result = enterpriseService.findById(id);
        assertEquals("Corp", result.getCorporateReason());
    }

    @Test
    void findById_shouldThrowEntityNotFound() {
        UUID id = UUID.randomUUID();
        when(enterpriseRepository.findByIdAndStatus(id, UserStatus.ACTIVE)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> enterpriseService.findById(id));
    }

    @Test
    void patchEnterprise_shouldUpdateFields() {
        UUID id = UUID.randomUUID();
        Enterprise ent = Enterprise.builder()
                .corporateReason("Corp")
                .cnpj("123")
                .email("mail")
                .status(UserStatus.ACTIVE)
                .type(UserTypeE.ENTERPRISE)
                .build();
        EnterprisePatchRequestDTO dto = new EnterprisePatchRequestDTO();
        dto.setCorporateReason("NewCorp");
        dto.setCnpj("456");
        dto.setEmail("newmail");
        dto.setPhone("987");
        dto.setPicture("newpic.png");
        when(enterpriseRepository.findByIdAndStatus(id, UserStatus.ACTIVE)).thenReturn(Optional.of(ent));
        when(enterpriseRepository.save(any(Enterprise.class))).thenAnswer(i -> i.getArgument(0));
        EnterpriseResponseDTO result = enterpriseService.patchEnterprise(id, dto);
        assertEquals("NewCorp", result.getCorporateReason());
        assertEquals("456", result.getCnpj());
        assertEquals("newmail", result.getEmail());
        assertEquals("987", result.getPhone());
        assertEquals("newpic.png", result.getPicture());
    }

    @Test
    void patchEnterprise_shouldThrowEntityNotFound() {
        UUID id = UUID.randomUUID();
        EnterprisePatchRequestDTO dto = new EnterprisePatchRequestDTO();
        when(enterpriseRepository.findByIdAndStatus(id, UserStatus.ACTIVE)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> enterpriseService.patchEnterprise(id, dto));
    }

    @Test
    void delete_shouldSetStatusInactive() {
        UUID id = UUID.randomUUID();
        Enterprise ent = Enterprise.builder()
                .corporateReason("Corp")
                .cnpj("123")
                .email("mail")
                .status(UserStatus.ACTIVE)
                .type(UserTypeE.ENTERPRISE)
                .build();
        when(enterpriseRepository.findById(id)).thenReturn(Optional.of(ent));
        when(enterpriseRepository.save(any(Enterprise.class))).thenAnswer(i -> i.getArgument(0));
        enterpriseService.delete(id);
        assertEquals(UserStatus.INACTIVE, ent.getStatus());
    }

    @Test
    void delete_shouldThrowEntityNotFound() {
        UUID id = UUID.randomUUID();
        when(enterpriseRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> enterpriseService.delete(id));
    }
}
