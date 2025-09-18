package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.ScheduleRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponse;
import com.pointtils.pointtils.src.application.dto.responses.ScheduleResponseDTO;
import com.pointtils.pointtils.src.application.services.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@RestController
@RequestMapping("/v1/schedules")
@AllArgsConstructor
@Tag(name = "Schedule Controller", description = "Endpoints para gerenciamento de horários de intérpretes")
public class ScheduleController {
    private final ScheduleService service;

    @PostMapping("/register")
    @Operation(summary = "Cadastra um horário para um intérprete")
    public ResponseEntity<ApiResponse<ScheduleResponseDTO>> registerSchedule(@Valid @RequestBody ScheduleRequestDTO dto) {
        ScheduleResponseDTO created = service.registerSchedule(dto);
        ApiResponse<ScheduleResponseDTO> response = new ApiResponse<>(true, "Horário cadastrado com sucesso", created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{scheduleId}")
    @Operation(summary = "Obtém detalhes de um horário específico")
    public ResponseEntity<ApiResponse<ScheduleResponseDTO>> getScheduleById(@PathVariable UUID scheduleId) {
        ScheduleResponseDTO schedule = service.findById(scheduleId);
        ApiResponse<ScheduleResponseDTO> response = new ApiResponse<>(true, "Detalhes do horário obtidos com sucesso", schedule);
        return ResponseEntity.ok(response);
    }
}
