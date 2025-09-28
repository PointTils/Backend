package com.pointtils.pointtils.src.application.dto.requests;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppointmentRequestDTO {
    @Size(min = 2, max = 2, message = "UF deve ter exatamente 2 caracteres")
    private String uf;
    
    private String city;
    
    private String neighborhood;
    
    private String street;

    @JsonProperty("street_number")
    private Integer streetNumber;

    @JsonProperty("address_details")
    private String addressDetails;
    
    @Schema(description = "Modalidade do atendimento",
        allowableValues = {"ONLINE", "PERSONALLY"},
        example = "ONLINE")
    private AppointmentModality modality;
    
    @NotNull(message = "Data é obrigatória")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Data do agendamento",
        type = "string", format = "date", example = "2025-09-23")
    private LocalDate date;

    private String description;

    @JsonProperty("interpreter_id")
    @NotNull(message = "ID do intérprete é obrigatório")
    private UUID interpreterId;

    @JsonProperty("user_id")
    @NotNull(message = "ID do usuário é obrigatório")
    private UUID userId;

    @JsonProperty("start_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(description = "Hora de início",
        type = "string",
        example = "09:30:00",
        pattern = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$")
    private LocalTime startTime;

    @JsonProperty("end_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(description = "Hora de término",
        type = "string",
        example = "10:30:00",
        pattern = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$")
    private LocalTime endTime;
}
