package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.UserAppRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.UserAppResponseDTO;
import com.pointtils.pointtils.src.application.services.UserAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra dados de um novo aplicativo do usuário")
    public ApiResponseDTO<UserAppResponseDTO> createUserApp(@Valid @RequestBody UserAppRequestDTO dto) {
        UserAppResponseDTO response = userAppService.createUserApp(dto);
        return ApiResponseDTO.success("Dados do aplicativo do usuário criados com sucesso", response);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca dados de aplicativos dos usuários")
    public ApiResponseDTO<List<UserAppResponseDTO>> getAllUserApps(@RequestParam(required = false) UUID userId,
                                                                   @RequestParam(required = false) String deviceId) {
        List<UserAppResponseDTO> response = userAppService.getUserApps(userId, deviceId);
        return ApiResponseDTO.success("Dados de aplicativos dos usuários encontrados com sucesso", response);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deleta dados de aplicativos dos usuários")
    public void deleteUserApps(@RequestParam(required = false) UUID userId,
                               @RequestParam(required = false) String deviceId) {
        userAppService.deleteUserApps(userId, deviceId);
    }
}
