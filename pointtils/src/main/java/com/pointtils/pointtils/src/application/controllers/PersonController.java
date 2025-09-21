package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.PersonCreationRequestDTO;
import com.pointtils.pointtils.src.application.dto.PersonDTO;
import com.pointtils.pointtils.src.application.dto.requests.PersonPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponse;
import com.pointtils.pointtils.src.application.services.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/person")
@AllArgsConstructor
@Tag(name = "Person Controller", description = "Endpoints para gerenciamento de usuários surdos")
public class PersonController {
    private final PersonService service;

    @PostMapping("/register")
    @Operation(summary = "Cadastra um usuário surdo")
    public ResponseEntity<ApiResponse<PersonDTO>> createUser(@Valid @RequestBody PersonCreationRequestDTO dto) {
        PersonDTO created = service.registerPerson(dto);
        ApiResponse<PersonDTO> response = ApiResponse.success("Usuário surdo cadastrado com sucesso", created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Busca todos os usuários surdos")
    public ResponseEntity<ApiResponse<List<PersonDTO>>> findAll() {
        List<PersonDTO> personUsers = service.findAll();
        ApiResponse<List<PersonDTO>> response =
                ApiResponse.success("Usuários surdos encontrados com sucesso", personUsers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Busca um usuário surdo por ID")
    public ResponseEntity<ApiResponse<PersonDTO>> findById(@PathVariable UUID id) {
        PersonDTO person = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Usuário surdo encontrado com sucesso", person));
    }

    @PatchMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualiza parcialmente um usuário surdo por ID")
    public ResponseEntity<ApiResponse<PersonDTO>> updateUser(@PathVariable UUID id,
                                                             @RequestBody @Valid PersonPatchRequestDTO dto) {
        PersonDTO updated = service.updatePartial(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Usuário surdo atualizado com sucesso", updated));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualiza um usuário surdo por ID")
    public ResponseEntity<ApiResponse<PersonDTO>> updateComplete(@PathVariable UUID id,
                                                                 @RequestBody @Valid PersonDTO dto) {
        PersonDTO updated = service.updateComplete(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Usuário surdo atualizado com sucesso", updated));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Deleta um usuário surdo por ID")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
