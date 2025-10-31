package com.pointtils.pointtils.src.application.controllers;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pointtils.pointtils.src.application.dto.requests.UserPicturePostRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.application.services.UserPicturePostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Picture Controller", description = "Endpoints para gerenciamento de fotos de perfil dos usuários")
public class UserPicturePostController {

    private final UserPicturePostService userService;

    @PostMapping(value = "/{id}/picture", consumes = "multipart/form-data")
    @Operation(
            summary = "Faz upload da foto de perfil do usuário",
            description = "Permite fazer upload de uma imagem para ser usada como foto de perfil do usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foto de perfil atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Arquivo inválido ou formato não suportado"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "413", description = "Arquivo muito grande"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor"),
            @ApiResponse(responseCode = "503", description = "Serviço de upload temporariamente indisponível",
                    content = @Content(mediaType = "text/plain"))
    })
    public ResponseEntity<UserResponseDTO> uploadPicture(
            @Parameter(description = "ID do usuário", required = true) @PathVariable UUID id,
            @Parameter(description = "Arquivo de imagem (JPG, PNG, GIF)", required = true) 
            @RequestParam("file") MultipartFile file) throws IOException {
        UserPicturePostRequestDTO request = new UserPicturePostRequestDTO(id, file);
        UserResponseDTO response = userService.updatePicture(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Handler para quando o upload de fotos está desabilitado
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    @Operation(
            summary = "Manipula erro quando upload está desabilitado",
            description = "Retorna erro quando o serviço de upload de fotos está temporariamente desabilitado"
    )
    @ApiResponse(responseCode = "503", description = "Serviço de upload temporariamente indisponível",
            content = @Content(mediaType = "text/plain"))
    public ResponseEntity<String> handleUnsupportedOperation(UnsupportedOperationException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ex.getMessage());
    }
}
