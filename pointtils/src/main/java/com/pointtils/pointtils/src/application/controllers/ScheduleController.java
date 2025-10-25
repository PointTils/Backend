package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.ScheduleListRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.SchedulePatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ScheduleRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.AvailableTimeSlotsResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.PaginatedScheduleResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.ScheduleResponseDTO;
import com.pointtils.pointtils.src.application.services.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/schedules")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Schedule Controller", description = "Endpoints para gerenciamento de horários de intérpretes")
public class ScheduleController {
    private final ScheduleService service;

    @PostMapping("/register")
    @Operation(summary = "Cadastra um horário para um intérprete")
    public ResponseEntity<ApiResponseDTO<ScheduleResponseDTO>> registerSchedule(@Valid @RequestBody ScheduleRequestDTO dto) {
        ScheduleResponseDTO created = service.registerSchedule(dto);
        ApiResponseDTO<ScheduleResponseDTO> response = new ApiResponseDTO<>(true, "Horário cadastrado com sucesso", created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{scheduleId}")
    @Operation(summary = "Obtém detalhes de um horário específico")
    public ResponseEntity<ApiResponseDTO<ScheduleResponseDTO>> getScheduleById(@PathVariable UUID scheduleId) {
        ScheduleResponseDTO schedule = service.findById(scheduleId);
        ApiResponseDTO<ScheduleResponseDTO> response = new ApiResponseDTO<>(true, "Detalhes do horário obtidos com sucesso", schedule);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    @Operation(summary = "Lista todos os horários cadastrados com paginação")
    public ResponseEntity<ApiResponseDTO<PaginatedScheduleResponseDTO>> listSchedules(@Valid @ModelAttribute ScheduleListRequestDTO query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        PaginatedScheduleResponseDTO schedules = service.findAll(query, pageable);
        ApiResponseDTO<PaginatedScheduleResponseDTO> response = new ApiResponseDTO<>(true, "Horários obtidos com sucesso", schedules);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{scheduleId}")
    @Operation(summary = "Atualiza dados de um horário de disponibilidade")
    public ResponseEntity<ApiResponseDTO<ScheduleResponseDTO>> updateSchedule(@PathVariable UUID scheduleId, @Valid @RequestBody SchedulePatchRequestDTO dto) {
        ScheduleResponseDTO updated = service.updateSchedule(scheduleId, dto);
        ApiResponseDTO<ScheduleResponseDTO> response = new ApiResponseDTO<>(true, "Horário atualizado com sucesso", updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "Exclui um horário de disponibilidade")
    public ResponseEntity<ApiResponseDTO<Void>> deleteSchedule(@PathVariable UUID scheduleId) {
        service.deleteById(scheduleId);
        ApiResponseDTO<Void> response = new ApiResponseDTO<>(true, "Horário excluído com sucesso", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    @Operation(summary = "Lista todos os intervalos de horários disponíveis cadastrados")
    public ResponseEntity<ApiResponseDTO<List<AvailableTimeSlotsResponseDTO>>> listAvailableSchedules(@RequestParam UUID interpreterId,
                                                                                                      @RequestParam LocalDate dateFrom,
                                                                                                      @RequestParam LocalDate dateTo) {
        List<AvailableTimeSlotsResponseDTO> availableTimeSlots = service.findAvailableSchedules(interpreterId, dateFrom, dateTo);
        ApiResponseDTO<List<AvailableTimeSlotsResponseDTO>> response = new ApiResponseDTO<>(true, "Horários disponíveis obtidos com sucesso", availableTimeSlots);
        return ResponseEntity.ok(response);
    }
}
