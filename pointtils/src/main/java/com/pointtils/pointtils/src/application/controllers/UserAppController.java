package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.UserAppPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.UserAppRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserAppResponseDTO;
import com.pointtils.pointtils.src.application.services.UserAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user-apps")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User App Controller", description = "Endpoints para gerenciamento de dados dos aplicativos do usuário")
public class UserAppController {

    private final UserAppService userAppService;

    @PostMapping
    @Operation(
            summary = "Registra dados de um novo aplicativo do usuário",
            description = "Registra dados de uma nova instalação do aplicativo feita por um determinado usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dados do aplicativo do usuário registrados com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Parâmetros de requisição inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseDTO<UserAppResponseDTO> createUserApp(@Valid @RequestBody UserAppRequestDTO request) {
        UserAppResponseDTO response = userAppService.createUserApp(request);
        return ApiResponseDTO.success("Dados do aplicativo do usuário criados com sucesso", response);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Busca dados de aplicativos dos usuários",
            description = "Busca dados de instalações do aplicativo por usuário ou por dispositivo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados de aplicativos encontrados com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Parâmetros de requisição inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ApiResponseDTO<List<UserAppResponseDTO>> getAllUserApps(@RequestParam(required = false) UUID userId,
                                                                   @RequestParam(required = false) String deviceId) {
        List<UserAppResponseDTO> response = userAppService.getUserApps(userId, deviceId);
        return ApiResponseDTO.success("Dados de aplicativos dos usuários encontrados com sucesso", response);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Atualiza dados de um aplicativo do usuário",
            description = "Atualiza dados de uma instalação do aplicativo de um determinado usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados do aplicativo atualizados com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Parâmetros de requisição inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ApiResponseDTO<UserAppResponseDTO> getAllUserApps(@PathVariable("id") UUID userAppId,
                                                             @Valid @RequestBody UserAppPatchRequestDTO request) {
        UserAppResponseDTO response = userAppService.updateUserApp(userAppId, request);
        return ApiResponseDTO.success("Dados do aplicativo do usuário atualizado com sucesso", response);
    }

    @DeleteMapping
    @Operation(
            summary = "Deleta dados de aplicativos dos usuários",
            description = "Deleta dados de instalações do aplicativo por usuário ou por dispositivo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dados do aplicativo deletados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de requisição inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserApps(@RequestParam(required = false) UUID userId,
                               @RequestParam(required = false) String deviceId) {
        userAppService.deleteUserApps(userId, deviceId);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deleta dados de aplicativo do usuário por ID",
            description = "Deleta dados de uma instalação do aplicativo de um usuário por ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dados do aplicativo deletados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de requisição inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserApps(@PathVariable("id") UUID userAppId) {
        userAppService.deleteUserAppById(userAppId);
    }
}
