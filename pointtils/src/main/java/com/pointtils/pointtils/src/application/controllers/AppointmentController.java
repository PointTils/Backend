package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.AppointmentPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.AppointmentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentFilterResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentResponseDTO;
import com.pointtils.pointtils.src.application.services.AppointmentService;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/appointments")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Appointment Controller", description = "Endpoints para gerenciamento de solicitações de agendamento")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @Operation(summary = "Cria um novo agendamento")
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> createAppointment(@Valid @RequestBody AppointmentRequestDTO dto) {

        AppointmentResponseDTO response = appointmentService.createAppointment(dto);
        ApiResponseDTO<AppointmentResponseDTO> apiResponse =
                ApiResponseDTO.success("Solicitação criada com sucesso", response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    @Operation(summary = "Lista todos os agendamentos")
    public ResponseEntity<ApiResponseDTO<List<AppointmentResponseDTO>>> findAll() {
        List<AppointmentResponseDTO> list = appointmentService.findAll();
        return ResponseEntity.ok(ApiResponseDTO.success("Solicitações encontradas com sucesso", list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca agendamento por ID")
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> findById(@PathVariable UUID id) {
        AppointmentResponseDTO item = appointmentService.findById(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Solicitação encontrada com sucesso", item));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza parcialmente um agendamento por ID")
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> updatePartial(@PathVariable UUID id,
                                                                                @RequestBody @Valid AppointmentPatchRequestDTO dto) {
        AppointmentResponseDTO updated = appointmentService.updatePartial(id, dto);
        return ResponseEntity.ok(ApiResponseDTO.success("Solicitação atualizada com sucesso", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um agendamento por ID")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    @Operation(summary = "Busca agendamentos com filtros opcionais")
    public ResponseEntity<ApiResponseDTO<List<AppointmentFilterResponseDTO>>> searchAppointments(
            @RequestParam(required = false) UUID interpreterId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) AppointmentModality modality,
            @RequestParam(required = false) String fromDateTime,
            @RequestParam(required = false) Boolean hasRating) {

        LocalDateTime from = null;
        if (fromDateTime != null && !fromDateTime.trim().isEmpty()) {
            from = LocalDateTime.parse(fromDateTime);
        }

        List<AppointmentFilterResponseDTO> appointments = appointmentService.searchAppointments(interpreterId, userId, status, modality, from, hasRating);
        return ResponseEntity.ok(ApiResponseDTO.success("Solicitações encontradas com sucesso", appointments));
    }

}
