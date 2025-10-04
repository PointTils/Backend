package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.ParametersBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ParametersPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponse;
import com.pointtils.pointtils.src.application.dto.responses.ParametersResponseDTO;
import com.pointtils.pointtils.src.application.services.ParametersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/parameters")
@AllArgsConstructor
@Tag(name = "Parameters Controller", description = "Endpoints para gerenciamento de parâmetros da aplicação")
public class ParametersController {
	private final ParametersService service;
	
	@PostMapping()
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Cadastra um novo parâmetro")
	public ResponseEntity<ApiResponse<ParametersResponseDTO>> create (@Valid @RequestBody ParametersBasicRequestDTO dto) {
		ParametersResponseDTO created = service.create(dto);
		ApiResponse<ParametersResponseDTO> response = new ApiResponse<>(true, "Parâmetro cadastrado com sucesso", created);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
		
	}
	
	@GetMapping()
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Busca todos os parâmetros da aplicação")
	public ResponseEntity<ApiResponse<List<ParametersResponseDTO>>> findAll() {
		List<ParametersResponseDTO> list = service.findAll();
		ApiResponse<List<ParametersResponseDTO>> response = new ApiResponse<>(true, "Parâmetros encontrados com sucesso", list);
		return ResponseEntity.ok(response);
		
	}
	
	@GetMapping("/{key}")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Busca um parâmetro por chave")
	public ResponseEntity<ApiResponse<ParametersResponseDTO>> findByKey(@PathVariable String key) {
		ParametersResponseDTO parameters = service.findByKey(key);
		ApiResponse<ParametersResponseDTO> response = new ApiResponse<>(true, "Parâmetro encontrado com sucesso", parameters);
		return ResponseEntity.ok(response);
		
	}
	
	@PutMapping("/{id}")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Atualiza um parâmetro por ID")
	public ResponseEntity<ApiResponse<ParametersResponseDTO>> put(@PathVariable UUID id, @Valid @RequestBody ParametersPatchRequestDTO dto) {
		ParametersResponseDTO patched = service.put(id, dto);
		ApiResponse<ParametersResponseDTO> response = new ApiResponse<>(true, "Parâmetro atualizado com sucesso", patched);
		return ResponseEntity.ok(response);
		
	}
	
	@DeleteMapping("/{id}")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Deleta um parâmetro por ID")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
		
	}
	
}
