package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.FindAllInterpreterDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterListResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.services.InterpreterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
@RequestMapping("/v1/interpreters")
@AllArgsConstructor
@Tag(name = "Interpreter Controller", description = "Endpoints para gerenciamento de usuários intérprete")
public class InterpreterController {

    private final InterpreterService service;

    @PostMapping("/register")
    @Operation(
            summary = "Cadastra um usuário intérprete",
            description = "Registra um novo intérprete no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Intérprete cadastrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de cadastro inválidos"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<InterpreterResponseDTO>> createInterpreter(
            @Valid @RequestBody InterpreterBasicRequestDTO dto) {
        var interpreter = service.registerBasic(dto);
        ApiResponseDTO<InterpreterResponseDTO> response =
                ApiResponseDTO.success("Intérprete cadastrado com sucesso", interpreter);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Busca todos os usuários intérprete",
            description = "Retorna lista de todos os intérpretes cadastrados no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intérpretes encontrados com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<List<InterpreterListResponseDTO>>> findAll(@ModelAttribute FindAllInterpreterDTO dto) {

        List<InterpreterListResponseDTO> interpreters = service.findAll(dto);

        return ResponseEntity.ok(ApiResponseDTO.success("Intérpretes encontrados com sucesso", interpreters));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Busca um usuário intérprete por ID",
            description = "Retorna os dados de um intérprete específico pelo seu ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intérprete encontrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Intérprete não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<InterpreterResponseDTO>> findById(@PathVariable UUID id) {
        InterpreterResponseDTO interpreter = service.findById(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Intérprete encontrado com sucesso", interpreter));
    }

    @PatchMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Atualiza parcialmente um usuário intérprete por ID",
            description = "Atualiza dados específicos de um intérprete"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intérprete atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de atualização inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Intérprete não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<InterpreterResponseDTO>> patchInterpreter(@PathVariable UUID id,
                                                                                   @RequestBody @Valid InterpreterPatchRequestDTO dto) {
        InterpreterResponseDTO updated = service.updatePartial(id, dto);
        return ResponseEntity.ok(ApiResponseDTO.success("Intérprete atualizado com sucesso", updated));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Atualiza um usuário intérprete por ID",
            description = "Atualiza todos os dados de um intérprete"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intérprete atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de atualização inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Intérprete não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<InterpreterResponseDTO>> putInterpreter(@PathVariable UUID id,
                                                                                 @RequestBody @Valid InterpreterBasicRequestDTO dto) {
        InterpreterResponseDTO updated = service.updateComplete(id, dto);
        return ResponseEntity.ok(ApiResponseDTO.success("Intérprete atualizado com sucesso", updated));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Deleta um usuário intérprete por ID",
            description = "Remove permanentemente um intérprete do sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Intérprete deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Intérprete não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
