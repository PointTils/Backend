package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.EmailRequestDTO;
import com.pointtils.pointtils.src.application.services.EmailService;
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

@Slf4j
@RestController
@RequestMapping("/v1/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "Endpoints para envio de emails")
public class EmailController {

    private final EmailService emailService;

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
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Map<String, Object>> sendPasswordResetEmail(
            @PathVariable String email,
            @RequestParam String userName,
            @RequestParam String resetToken) {
        
        boolean success = emailService.sendPasswordResetEmail(email, userName, resetToken);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "Email de recuperação enviado com sucesso" : "Falha ao enviar email de recuperação");
        response.put("to", email);
        response.put("userName", userName);
        
        return ResponseEntity.ok(response);
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

    @GetMapping("/test")
    @Operation(summary = "Teste de configuração de email", description = "Endpoint para testar a configuração do Brevo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teste executado com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro na configuração de email")
    })
    public ResponseEntity<Map<String, Object>> testEmailConfiguration() {
        String testEmail = "test@pointtils.com";
        String testSubject = "Teste de Configuração - PointTils";
        String testBody = "Este é um email de teste para verificar se a configuração do Brevo está funcionando corretamente.";
        
        EmailRequestDTO testRequest = new EmailRequestDTO(testEmail, testSubject, testBody, "PointTils Test");
        boolean success = emailService.sendSimpleEmail(testRequest);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "Teste de email executado com sucesso" : "Falha no teste de email");
        response.put("testEmail", testEmail);
        response.put("configuration", "Brevo SMTP");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-html")
    @Operation(summary = "Teste de email HTML", description = "Endpoint para testar envio de emails HTML")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teste HTML executado com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro na configuração de email")
    })
    public ResponseEntity<Map<String, Object>> testHtmlEmail() {
        String testEmail = "test@pointtils.com";
        String testSubject = "Teste de Email HTML - PointTils";
        String testHtml = """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9;">
                    <h1 style="color: #667eea;">✨ Email HTML Funcionando!</h1>
                    <p>Se você está vendo este email formatado, significa que:</p>
                    <ul style="background-color: white; padding: 20px; border-radius: 5px;">
                        <li>✅ Conexão SMTP com Brevo estabelecida</li>
                        <li>✅ Configuração do Spring Boot correta</li>
                        <li>✅ Envio de emails HTML funcionando</li>
                    </ul>
                    <div style="margin: 20px 0; padding: 15px; background-color: #d4edda; border-left: 4px solid #28a745; border-radius: 4px;">
                        <strong>🎉 Parabéns!</strong> Sua integração está 100%% operacional!
                    </div>
                </div>
            </body>
            </html>
            """;
        
        EmailRequestDTO testRequest = new EmailRequestDTO(testEmail, testSubject, testHtml, "PointTils Test");
        boolean success = emailService.sendHtmlEmail(testRequest);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "Teste de email HTML executado com sucesso" : "Falha no teste de email HTML");
        response.put("testEmail", testEmail);
        response.put("type", "HTML");
        
        return ResponseEntity.ok(response);
    }

}
