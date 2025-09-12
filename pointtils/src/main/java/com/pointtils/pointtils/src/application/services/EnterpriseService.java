package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.EnterprisePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.EnterpriseRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.EnterpriseResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.Enterprise;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.core.domain.exceptions.DuplicateResourceException;
import com.pointtils.pointtils.src.infrastructure.repositories.EnterpriseRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class EnterpriseService {
	private static final String ENTERPRISE_NOT_FOUND_MSG = "Empresa não encontrada";
	private final EnterpriseRepository enterpriseRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public EnterpriseResponseDTO registerEnterprise(EnterpriseRequestDTO dto) {
		if (enterpriseRepository.existsByCnpj(dto.getCnpj())) {
			throw new DuplicateResourceException("Já existe uma empresa cadastrada com este CNPJ");
		}

		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new DuplicateResourceException("Já existe um usuário cadastrado com este email");
		}

		Enterprise enterprise = Enterprise.builder()
								.corporateReason(dto.getCorporateReason())
								.cnpj(dto.getCnpj())
								.email(dto.getEmail())
								.password(passwordEncoder.encode(dto.getPassword()))
								.phone(dto.getPhone())
								.picture(dto.getPicture())
								.status(UserStatus.ACTIVE)
								.type(UserTypeE.ENTERPRISE)
								.build();

		if (dto.getLocation() != null) {
			Location location = Location.builder()
					.uf(dto.getLocation().getUf())
					.city(dto.getLocation().getCity())
					.user(enterprise)
					.build();

			enterprise.setLocation(location);
		}
		Enterprise savedEnterprise = enterpriseRepository.save(enterprise);

		return new EnterpriseResponseDTO(savedEnterprise);
	}

	public List<EnterpriseResponseDTO> findAll() {
		List<Enterprise> enterpriseList = enterpriseRepository.findAllByStatus(UserStatus.ACTIVE);
		return enterpriseList.stream()
				.map(EnterpriseResponseDTO::new)
				.toList();
	}

	public EnterpriseResponseDTO findById(UUID id) {
		Enterprise enterprise = enterpriseRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
				.orElseThrow(() -> new EntityNotFoundException(ENTERPRISE_NOT_FOUND_MSG));
		return new EnterpriseResponseDTO(enterprise);
	}

	public EnterpriseResponseDTO patchEnterprise(
			UUID id,
			EnterprisePatchRequestDTO dto
	) {
		Enterprise enterprise = enterpriseRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
				.orElseThrow(() -> new EntityNotFoundException(ENTERPRISE_NOT_FOUND_MSG));

		if (dto.getCorporateReason() != null) enterprise.setCorporateReason(dto.getCorporateReason());
		if (dto.getCnpj() != null) enterprise.setCnpj(dto.getCnpj());
		if (dto.getEmail() != null) enterprise.setEmail(dto.getEmail());
		if (dto.getPhone() != null) enterprise.setPhone(dto.getPhone());
		if (dto.getPicture() != null) enterprise.setPicture(dto.getPicture());

		Enterprise patchedEnterprise = enterpriseRepository.save(enterprise);
		return new EnterpriseResponseDTO(patchedEnterprise);
	}

	public void delete(UUID id) {
		Enterprise enterprise = enterpriseRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ENTERPRISE_NOT_FOUND_MSG));

		enterprise.setStatus(UserStatus.INACTIVE);
		enterpriseRepository.save(enterprise);
	}
}
