package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.ParametersBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ParametersPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ParametersResponseDTO;
import com.pointtils.pointtils.src.application.mapper.ParametersMapper;
import com.pointtils.pointtils.src.core.domain.entities.Parameters;
import com.pointtils.pointtils.src.infrastructure.repositories.ParametersRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ParametersService {
	private static final String PARAMETER_NOT_FOUND_MSG = "Parâmetro não encontrado";
	private static final String PARAMETER_DUPLICATED_MSG = "Já existe um parâmetro cadastrado com esta chave";
	private final ParametersRepository repository;
	private final ParametersMapper mapper;
	
	public ParametersResponseDTO create(ParametersBasicRequestDTO dto) {
		if (repository.existsByKey(dto.getKey())) {
			throw new EntityExistsException(PARAMETER_DUPLICATED_MSG);
		}
		
		Parameters savedParameters = repository.save(mapper.toEntity(dto));
		
		return mapper.toResponseDTO(savedParameters);
		
	}
	
	public List<ParametersResponseDTO> findAll() {
		List<Parameters> parametersList = repository.findAll();
		return parametersList.stream()
				.map(mapper::toResponseDTO)
				.toList();
		
	}
	
	public ParametersResponseDTO findByKey(String key) {
		Parameters parameters = repository.findByKey(key)
				.orElseThrow(() -> new EntityNotFoundException(PARAMETER_NOT_FOUND_MSG));
		return  mapper.toResponseDTO(parameters);
		
	}
	
	public ParametersResponseDTO put(UUID id, ParametersPatchRequestDTO dto) {
		Parameters parameters = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(PARAMETER_NOT_FOUND_MSG));
		
		if (dto.getKey() != null) parameters.setKey(dto.getKey());
		if (dto.getValue() != null) parameters.setValue(dto.getValue());
		
		Parameters patchedParameters = repository.save(parameters);
		return mapper.toResponseDTO(patchedParameters);
	}
	
	public void delete(UUID id) {
		repository.deleteById(id);
	}
	
}
