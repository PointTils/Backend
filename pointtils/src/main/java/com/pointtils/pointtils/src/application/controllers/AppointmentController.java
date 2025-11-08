package com.pointtils.pointtils.src.application.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.requests.AppointmentPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.AppointmentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentFilterResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentResponseDTO;
import com.pointtils.pointtils.src.application.services.AppointmentService;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/v1/appointments")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Appointment Controller", description = "Endpoints para gerenciamento de solicitações de agendamento")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @Operation(
            summary = "Cria um novo agendamento",
            description = "Cria uma nova solicitação de agendamento no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de agendamento inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> createAppointment(
            @Valid @RequestBody AppointmentRequestDTO dto) {

    AppointmentResponseDTO response = appointmentService.createAppointment(dto);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponseDTO.success("Solicitação criada com sucesso", response));
}


    @GetMapping
    @Operation(
            summary = "Lista todos os agendamentos",
            description = "Retorna lista de todos os agendamentos cadastrados no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamentos encontrados com sucesso",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AppointmentResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor ")
    })
    public ResponseEntity<ApiResponseDTO<List<AppointmentResponseDTO>>> findAll() {
        List<AppointmentResponseDTO> list = appointmentService.findAll();
        return ResponseEntity.ok(ApiResponseDTO.success("Solicitações encontradas com sucesso", list));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Busca agendamento por ID",
            description = "Retorna os dados de um agendamento específico pelo seu ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou ausente"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> findById(@PathVariable UUID id) {
        AppointmentResponseDTO item = appointmentService.findById(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Solicitação encontrada com sucesso", item));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Atualiza parcialmente um agendamento por ID",
            description = "Atualiza campos específicos de um agendamento"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados de atualização inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> updatePartial(@PathVariable UUID id,
                                                                                @RequestBody @Valid AppointmentPatchRequestDTO dto,
                                                                                @AuthenticationPrincipal UserDetails userDetails) {
        dto.setLoggedUserEmail(userDetails.getUsername());
        AppointmentResponseDTO updated = appointmentService.updatePartial(id, dto);
        return ResponseEntity.ok(ApiResponseDTO.success("Solicitação atualizada com sucesso", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deleta um agendamento por ID",
            description = "Remove permanentemente um agendamento do sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Agendamento deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    @Operation(
            summary = "Busca agendamentos com filtros opcionais",
            description = "Permite buscar agendamentos aplicando filtros por intérprete, usuário, status, modalidade, data e avaliação"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamentos filtrados encontrados com sucesso",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AppointmentFilterResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Parâmetros de filtro inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<List<AppointmentFilterResponseDTO>>> searchAppointments(
            @RequestParam(required = false) UUID interpreterId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) AppointmentModality modality,
            @RequestParam(required = false) String fromDateTime,
            @RequestParam(required = false) Boolean hasRating,
            @RequestParam(required = false, defaultValue = "-1") int dayLimit) {

        LocalDateTime from = null;
        if (fromDateTime != null && !fromDateTime.trim().isEmpty()) {
            from = LocalDateTime.parse(fromDateTime);
        }

        List<AppointmentFilterResponseDTO> appointments = appointmentService.searchAppointments(interpreterId, userId, status, modality, from, hasRating, dayLimit);
        return ResponseEntity.ok(ApiResponseDTO.success("Solicitações encontradas com sucesso", appointments));
    }

}
