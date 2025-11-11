package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.UserPicturePostRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserResponseDTO;
import com.pointtils.pointtils.src.application.services.UserPictureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Picture Controller", description = "Endpoints para gerenciamento de fotos de usuários")
public class UserPictureController {

    private final UserPictureService userPictureService;

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
        UserResponseDTO response = userPictureService.updatePicture(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/picture")
    @Operation(summary = "Deleta foto de perfil do usuário")
    public ResponseEntity<Void> deletePicture(@PathVariable UUID id) {
        userPictureService.deletePicture(id);
        return ResponseEntity.noContent().build();
    }
}
