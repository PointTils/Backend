package com.pointtils.pointtils.src.application.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {
    
    @NotBlank(message = "Destinatário é obrigatório")
    @Email(message = "Destinatário deve ser um email válido")
    private String to;
    
    @NotBlank(message = "Assunto é obrigatório")
    private String subject;
    
    @NotBlank(message = "Corpo do email é obrigatório")
    private String body;
    
    private String fromName = "PointTils";
}
