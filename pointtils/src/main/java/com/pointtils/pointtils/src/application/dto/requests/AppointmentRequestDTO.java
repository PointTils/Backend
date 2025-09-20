package com.pointtils.pointtils.src.application.dto.requests;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "UF é obrigatório")
    @Size(min = 2, max = 2, message = "UF deve ter exatamente 2 caracteres")
    private String uf;
    
    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100, message = "Cidade não pode ter mais de 100 caracteres")
    private String city;
    
    @NotBlank(message = "Modalidade é obrigatória")
    private String modality;
    
    @NotNull(message = "Data é obrigatória")
    private LocalDate date;
    
    private String description;
    
    private String status;
    
    @NotNull(message = "ID do intérprete é obrigatório")
    private UUID interpreterId;
    
    @NotNull(message = "ID do usuário é obrigatório")
    private UUID userId;
    
    private LocalTime startTime;
    

    private LocalTime endTime;
}
