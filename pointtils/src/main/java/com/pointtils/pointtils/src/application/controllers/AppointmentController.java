package com.pointtils.pointtils.src.application.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.requests.AppointmentRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponse;
import com.pointtils.pointtils.src.application.dto.responses.AppointmentResponseDTO;
import com.pointtils.pointtils.src.application.services.AppointmentService;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/v1/appointments")
@AllArgsConstructor
public class AppointmentController {
    
    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> createAppointment(@RequestBody AppointmentRequestDTO dto) {

        AppointmentResponseDTO response = appointmentService.createAppointment(dto);
        ApiResponse<AppointmentResponseDTO> apiResponse = 
            ApiResponse.success("Agendamento alocado na agenda com sucesso", response);
        return ResponseEntity.ok(apiResponse);
    }
    
}
