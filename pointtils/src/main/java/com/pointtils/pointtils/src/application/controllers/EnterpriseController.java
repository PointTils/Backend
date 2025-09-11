package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.EnterprisePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponse;
import com.pointtils.pointtils.src.application.dto.requests.EnterpriseRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.EnterpriseResponseDTO;
import com.pointtils.pointtils.src.application.services.EnterpriseService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/enterprise-users")
@AllArgsConstructor
public class EnterpriseController {
	private final EnterpriseService service;
	
	@PostMapping("register/enterprise")
	public ResponseEntity<ApiResponse<EnterpriseResponseDTO>> createUser(@Valid @RequestBody EnterpriseRequestDTO dto) {
		EnterpriseResponseDTO created = service.registerEnterprise(dto);
		ApiResponse<EnterpriseResponseDTO> response = new ApiResponse<>(true, "Empresa cadastrada com sucesso", created);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping()
	public ResponseEntity<List<EnterpriseResponseDTO>> findAll() {
		List<EnterpriseResponseDTO> enterpriseList = service.findAll();
		
		if (enterpriseList.isEmpty()) return ResponseEntity.noContent().build();
		
		return ResponseEntity.ok(enterpriseList);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<EnterpriseResponseDTO> findById(@PathVariable UUID id) {
		try {
			EnterpriseResponseDTO enterprise = service.findById(id);
			return ResponseEntity.ok(enterprise);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<EnterpriseResponseDTO> updateUser(@PathVariable UUID id, @RequestBody @Valid EnterprisePatchRequestDTO dto) {
		EnterpriseResponseDTO updated = service.patchEnterprise(id, dto);
		return ResponseEntity.ok(updated);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
