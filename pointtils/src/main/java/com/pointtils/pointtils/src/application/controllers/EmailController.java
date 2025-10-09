package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.EmailRequestDTO;
import com.pointtils.pointtils.src.application.services.EmailService;
import com.pointtils.pointtils.src.application.services.InterpreterService;
import com.pointtils.pointtils.src.application.services.MemoryResetTokenService;
import com.pointtils.pointtils.src.infrastructure.repositories.PersonRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "Endpoints para envio de emails")
public class EmailController {

    private final EmailService emailService;
    private final InterpreterService interpreterService;
    private final MemoryResetTokenService resetTokenService;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    @PostMapping("/send")
    @Operation(summary = "Enviar email simples", description = "Envia um email simples para um destinatário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email enviado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Map<String, Object>> sendEmail(@Valid @RequestBody EmailRequestDTO emailRequest) {
        boolean success = emailService.sendSimpleEmail(emailRequest);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "Email enviado com sucesso" : "Falha ao enviar email");
        response.put("to", emailRequest.getTo());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-html")
    @Operation(summary = "Enviar email HTML", description = "Envia um email formatado em HTML para um destinatário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email HTML enviado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Map<String, Object>> sendHtmlEmail(@Valid @RequestBody EmailRequestDTO emailRequest) {
        boolean success = emailService.sendHtmlEmail(emailRequest);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "Email HTML enviado com sucesso" : "Falha ao enviar email HTML");
        response.put("to", emailRequest.getTo());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/welcome/{email}")
    @Operation(summary = "Enviar email de boas-vindas", description = "Envia email de boas-vindas para um novo usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email de boas-vindas enviado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Email inválido"),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Map<String, Object>> sendWelcomeEmail(
            @PathVariable String email,
            @RequestParam String userName) {
        
        boolean success = emailService.sendWelcomeEmail(email, userName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "Email de boas-vindas enviado com sucesso" : "Falha ao enviar email de boas-vindas");
        response.put("to", email);
        response.put("userName", userName);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password-reset/{email}")
    @Operation(summary = "Enviar email de recuperação de senha", description = "Envia email com token de recuperação de senha")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email de recuperação enviado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Email inválido"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Map<String, Object>> sendPasswordResetEmail(@PathVariable String email) {
        try {
            log.info("=== INICIANDO ENVIO DE EMAIL DE RESET DE SENHA ===");
            log.info("Email: {}", email);
            
            // Verificar se o usuário existe
            log.info("Buscando usuário no banco...");
            var user = userRepository.findByEmail(email);
            if (user == null) {
                log.warn("Usuário não encontrado para email: {}", email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Usuário não encontrado");
                response.put("to", email);
                return ResponseEntity.status(404).body(response);
            }
            
            log.info("Usuário encontrado: ID={}", user.getId());
            
            // Usar nome padrão temporariamente para testar
            String userName = "Maria Souza";
            log.info("Usando nome padrão: {}", userName);
            
            // Gerar reset token
            log.info("Gerando token de reset...");
            String resetToken = resetTokenService.generateResetToken(email);
            log.info("Token gerado: {}", resetToken);
            
            // Enviar email
            log.info("Enviando email...");
            boolean success = emailService.sendPasswordResetEmail(email, userName, resetToken);
            log.info("Email enviado com sucesso: {}", success);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Email de recuperação enviado com sucesso" : "Falha ao enviar email de recuperação");
            response.put("to", email);
            response.put("userName", userName);
            
            log.info("=== FINALIZANDO ENVIO DE EMAIL DE RESET DE SENHA ===");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("=== ERRO CRÍTICO NO ENVIO DE EMAIL DE RESET DE SENHA ===");
            log.error("Email: {}", email);
            log.error("Mensagem de erro: {}", e.getMessage());
            log.error("Stack trace completo:", e);
            log.error("=== FIM DO ERRO CRÍTICO ===");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro interno no servidor: " + e.getMessage());
            response.put("to", email);
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/appointment-confirmation/{email}")
    @Operation(summary = "Enviar confirmação de agendamento", description = "Envia email de confirmação de agendamento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email de confirmação enviado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Email inválido"),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Map<String, Object>> sendAppointmentConfirmationEmail(
            @PathVariable String email,
            @RequestParam String userName,
            @RequestParam String appointmentDate,
            @RequestParam String interpreterName) {
        
        boolean success = emailService.sendAppointmentConfirmationEmail(email, userName, appointmentDate, interpreterName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "Email de confirmação enviado com sucesso" : "Falha ao enviar email de confirmação");
        response.put("to", email);
        response.put("userName", userName);
        response.put("appointmentDate", appointmentDate);
        response.put("interpreterName", interpreterName);
        
        return ResponseEntity.ok(response);
    }      

    @GetMapping("/template/{key}")
    @Operation(summary = "Buscar template por chave", description = "Retorna um template HTML armazenado no banco de dados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Template encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Template não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Map<String, Object>> getTemplateByKey(@PathVariable String key) {
        String template = emailService.getTemplateByKey(key);
        
        Map<String, Object> response = new HashMap<>();
        response.put("key", key);
        response.put("template", template);
        response.put("found", !template.contains("Template não encontrado"));
        response.put("message", !template.contains("Template não encontrado") ? 
            "Template encontrado com sucesso" : "Template não encontrado, retornando padrão");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/interpreter-registration-request")
    @Operation(summary = "Enviar solicitação de cadastro de intérprete", description = "Envia email para administradores com solicitação de cadastro de intérprete")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email de solicitação enviado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Map<String, Object>> sendInterpreterRegistrationRequest(
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
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "Email de solicitação enviado com sucesso" : "Falha ao enviar email de solicitação");
        response.put("adminEmail", adminEmail);
        response.put("interpreterName", interpreterName);
        
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/interpreter/{id}/approve")
    @Operation(summary = "Aprovar cadastro de intérprete", description = "Aprova o cadastro de um intérprete e envia email de confirmação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cadastro aprovado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Intérprete não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Map<String, Object>> approveInterpreter(@PathVariable String id) {
        try {
            UUID interpreterId = UUID.fromString(id);
            boolean success = interpreterService.approveInterpreter(interpreterId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Cadastro do intérprete aprovado com sucesso" : "Falha ao aprovar cadastro do intérprete");
            response.put("interpreterId", id);
            response.put("status", "ACTIVE");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao aprovar cadastro do intérprete {}: {}", id, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao aprovar cadastro do intérprete: " + e.getMessage());
            response.put("interpreterId", id);
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @PatchMapping("/interpreter/{id}/reject")
    @Operation(summary = "Recusar cadastro de intérprete", description = "Recusa o cadastro de um intérprete e envia email de notificação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cadastro recusado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Intérprete não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Map<String, Object>> rejectInterpreter(@PathVariable String id) {
        try {
            UUID interpreterId = UUID.fromString(id);
            boolean success = interpreterService.rejectInterpreter(interpreterId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Cadastro do intérprete recusado com sucesso" : "Falha ao recusar cadastro do intérprete");
            response.put("interpreterId", id);
            response.put("status", "INACTIVE");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao recusar cadastro do intérprete {}: {}", id, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao recusar cadastro do intérprete: " + e.getMessage());
            response.put("interpreterId", id);
            
            return ResponseEntity.status(500).body(response);
        }
    }

}
