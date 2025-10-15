package com.pointtils.pointtils.src.application.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pointtils.pointtils.src.application.dto.requests.EmailRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.services.EmailService;
import com.pointtils.pointtils.src.application.services.InterpreterService;
import com.pointtils.pointtils.src.application.services.MemoryResetTokenService;
import com.pointtils.pointtils.src.application.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "Endpoints para envio de emails")
public class EmailController {

    private final EmailService emailService;
    private final InterpreterService interpreterService;
    private final MemoryResetTokenService resetTokenService;
    private final UserService userService;

    @PostMapping("/send")
    @Operation(summary = "Enviar email simples", description = "Envia um email simples para um destinatário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> sendEmail(
            @Valid @RequestBody EmailRequestDTO emailRequest) {
        boolean success = emailService.sendSimpleEmail(emailRequest);

        Map<String, Object> data = new HashMap<>();
        data.put("to", emailRequest.getTo());

        return ResponseEntity.ok(ApiResponseDTO.success(
                success ? "Email enviado com sucesso" : "Falha ao enviar email",
                data));
    }

    @PostMapping("/send-html")
    @Operation(summary = "Enviar email HTML", description = "Envia um email formatado em HTML para um destinatário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email HTML enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> sendHtmlEmail(
            @Valid @RequestBody EmailRequestDTO emailRequest) {
        boolean success = emailService.sendHtmlEmail(emailRequest);

        Map<String, Object> data = new HashMap<>();
        data.put("to", emailRequest.getTo());

        return ResponseEntity.ok(ApiResponseDTO.success(
                success ? "Email HTML enviado com sucesso" : "Falha ao enviar email HTML",
                data));
    }

    @PostMapping("/welcome/{email}")
    @Operation(summary = "Enviar email de boas-vindas", description = "Envia email de boas-vindas para um novo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email de boas-vindas enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Email inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> sendWelcomeEmail(
            @PathVariable String email,
            @RequestParam String userName) {

        boolean success = emailService.sendWelcomeEmail(email, userName);

        Map<String, Object> data = new HashMap<>();
        data.put("to", email);
        data.put("userName", userName);

        return ResponseEntity.ok(ApiResponseDTO.success(
                success ? "Email de boas-vindas enviado com sucesso" : "Falha ao enviar email de boas-vindas",
                data));
    }

    @PostMapping("/password-reset/{email}")
    @Operation(summary = "Enviar email de recuperação de senha", description = "Envia email com token de recuperação de senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email de recuperação enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Email inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> sendPasswordResetEmail(@PathVariable String email) {
        try {
            var userOptional = userService.findByEmail(email);
            if (userOptional == null) {
                Map<String, Object> data = new HashMap<>();
                data.put("to", email);
                return ResponseEntity.status(404).body(ApiResponseDTO.error("Usuário não encontrado"));
            }

            String userName = userOptional.getDisplayName(); // ou use valor padrão
            String resetToken = resetTokenService.generateResetToken(email);
            boolean success = emailService.sendPasswordResetEmail(email, userName, resetToken);

            Map<String, Object> data = new HashMap<>();
            data.put("to", email);
            data.put("userName", userName);

            return ResponseEntity.ok(ApiResponseDTO.success(
                    success ? "Email de recuperação enviado com sucesso" : "Falha ao enviar email de recuperação",
                    data));

        } catch (Exception e) {
            log.error("Erro ao enviar email de reset de senha para {}: {}", email, e.getMessage(), e);
            Map<String, Object> data = new HashMap<>();
            data.put("to", email);
            return ResponseEntity.status(500).body(ApiResponseDTO.error("Erro interno no servidor"));
        }
    }

    @PostMapping("/appointment-confirmation/{email}")
    @Operation(summary = "Enviar confirmação de agendamento", description = "Envia email de confirmação de agendamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email de confirmação enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Email inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> sendAppointmentConfirmationEmail(
            @PathVariable String email,
            @RequestParam String userName,
            @RequestParam String appointmentDate,
            @RequestParam String interpreterName) {

        boolean success = emailService.sendAppointmentConfirmationEmail(email, userName, appointmentDate,
                interpreterName);

        Map<String, Object> data = new HashMap<>();
        data.put("to", email);
        data.put("userName", userName);
        data.put("appointmentDate", appointmentDate);
        data.put("interpreterName", interpreterName);

        return ResponseEntity.ok(ApiResponseDTO.success(
                success ? "Email de confirmação enviado com sucesso" : "Falha ao enviar email de confirmação",
                data));
    }

    @GetMapping("/template/{key}")
    @Operation(summary = "Buscar template por chave", description = "Retorna um template HTML armazenado no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Template não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getTemplateByKey(@PathVariable String key) {
        String template = emailService.getTemplateByKey(key);

        Map<String, Object> data = new HashMap<>();
        data.put("key", key);
        data.put("template", template);

        return ResponseEntity.ok(ApiResponseDTO.success(
                !template.contains("Template não encontrado") ? "Template encontrado com sucesso"
                        : "Template não encontrado",
                data));
    }

    @PostMapping("/interpreter-registration-request")
    @Operation(summary = "Enviar solicitação de cadastro de intérprete", description = "Envia email para administradores com solicitação de cadastro de intérprete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email de solicitação enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> sendInterpreterRegistrationRequest(
            @RequestParam String adminEmail,
            @RequestParam String interpreterName,
            @RequestParam String cpf,
            @RequestParam String cnpj,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String acceptLink,
            @RequestParam String rejectLink) {

        boolean success = emailService.sendInterpreterRegistrationRequestEmail(
                adminEmail, interpreterName, cpf, cnpj, email, phone, acceptLink, rejectLink);

        Map<String, Object> data = new HashMap<>();
        data.put("adminEmail", adminEmail);
        data.put("interpreterName", interpreterName);

        return ResponseEntity.ok(ApiResponseDTO.success(
                success ? "Email de solicitação enviado com sucesso" : "Falha ao enviar email de solicitação",
                data));
    }

    @PatchMapping("/interpreter/{id}/approve")
    @Operation(summary = "Aprovar cadastro de intérprete", description = "Aprova o cadastro de um intérprete e envia email de confirmação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastro aprovado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Intérprete não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> approveInterpreter(@PathVariable String id) {
        try {
            UUID interpreterId = UUID.fromString(id);
            boolean success = interpreterService.approveInterpreter(interpreterId);

            Map<String, Object> data = new HashMap<>();
            data.put("interpreterId", id);
            data.put("status", "ACTIVE");

            return ResponseEntity.ok(ApiResponseDTO.success(
                    success ? "Cadastro do intérprete aprovado com sucesso" : "Falha ao aprovar cadastro do intérprete",
                    data));

        } catch (Exception e) {
            log.error("Erro ao aprovar cadastro do intérprete {}: {}", id, e.getMessage(), e);
            Map<String, Object> data = new HashMap<>();
            data.put("interpreterId", id);

            return ResponseEntity.status(500).body(ApiResponseDTO.error("Erro ao aprovar cadastro do intérprete"));
        }
    }

    @PatchMapping("/interpreter/{id}/reject")
    @Operation(summary = "Recusar cadastro de intérprete", description = "Recusa o cadastro de um intérprete e envia email de notificação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastro recusado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Intérprete não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> rejectInterpreter(@PathVariable String id) {
        try {
            UUID interpreterId = UUID.fromString(id);
            boolean success = interpreterService.rejectInterpreter(interpreterId);

            Map<String, Object> data = new HashMap<>();
            data.put("interpreterId", id);
            data.put("status", "INACTIVE");

            return ResponseEntity.ok(ApiResponseDTO.success(
                    success ? "Cadastro do intérprete recusado com sucesso" : "Falha ao recusar cadastro do intérprete",
                    data));

        } catch (Exception e) {
            log.error("Erro ao recusar cadastro do intérprete {}: {}", id, e.getMessage(), e);
            Map<String, Object> data = new HashMap<>();
            data.put("interpreterId", id);

            return ResponseEntity.status(500).body(ApiResponseDTO.error("Erro ao recusar cadastro do intérprete"));
        }
    }
}
