package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.EmailRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.services.EmailService;
import com.pointtils.pointtils.src.application.services.InterpreterService;
import com.pointtils.pointtils.src.application.services.MemoryResetTokenService;
import com.pointtils.pointtils.src.application.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "Endpoints para envio de emails")
public class EmailController {

    private static final String USER_NAME = "userName";

    private final EmailService emailService;
    private final InterpreterService interpreterService;
    private final MemoryResetTokenService resetTokenService;
    private final UserService userService;

    @PostMapping("/send")
    @Operation(
            summary = "Enviar email simples",
            description = "Envia um email simples para um destinatário",
            parameters = {
                    @Parameter(name = "to", description = "Email do destinatário", required = true),
                    @Parameter(name = "subject", description = "Assunto do email", required = true),
                    @Parameter(name = "body", description = "Corpo do email", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email enviado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor"),
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
    @Operation(
            summary = "Enviar email HTML",
            description = "Envia um email formatado em HTML para um destinatário",
            parameters = {
                    @Parameter(name = "to", description = "Email do destinatário", required = true),
                    @Parameter(name = "subject", description = "Assunto do email", required = true),
                    @Parameter(name = "body", description = "Corpo do email", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email HTML enviado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor"),
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
    @Operation(
            summary = "Enviar email de boas-vindas",
            description = "Envia email de boas-vindas para um novo usuário",
            parameters = {
                    @Parameter(name = "email", description = "Email do destinatário", required = true),
                    @Parameter(name = "userName", description = "Nome do usuário", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email de boas-vindas enviado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor"),
    })
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> sendWelcomeEmail(
            @PathVariable String email,
            @RequestParam String userName) {

        boolean success = emailService.sendWelcomeEmail(email, userName);

        Map<String, Object> data = new HashMap<>();
        data.put("to", email);
        data.put(USER_NAME, userName);

        return ResponseEntity.ok(ApiResponseDTO.success(
                success ? "Email de boas-vindas enviado com sucesso" : "Falha ao enviar email de boas-vindas",
                data));
    }

    @PostMapping("/password-reset/{email}")
    @Operation(
            summary = "Enviar email de recuperação de senha",
            description = "Envia email com token de recuperação de senha",
            parameters = {
                    @Parameter(name = "email", description = "Email do destinatário", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email de recuperação enviado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor"),
    })
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> sendPasswordResetEmail(@PathVariable String email) {
        try {
            var userOptional = userService.findByEmail(email);
            if (userOptional == null) {
                return ResponseEntity.status(404).body(ApiResponseDTO.error("Usuário não encontrado"));
            }

            String userName = userOptional.getDisplayName(); // ou use valor padrão
            String resetToken = resetTokenService.generateResetToken(email);
            boolean success = emailService.sendPasswordResetEmail(email, userName, resetToken);

            Map<String, Object> data = new HashMap<>();
            data.put("to", email);
            data.put(USER_NAME, userName);

            return ResponseEntity.ok(ApiResponseDTO.success(
                    success ? "Email de recuperação enviado com sucesso" : "Falha ao enviar email de recuperação",
                    data));

        } catch (Exception e) {
            log.error("Erro ao enviar email de reset de senha para {}: {}", email, e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponseDTO.error("Erro interno no servidor"));
        }
    }

    @PostMapping("/appointment-confirmation/{email}")
    @Operation(
            summary = "Enviar confirmação de agendamento",
            description = "Envia email de confirmação de agendamento",
            parameters = {
                    @Parameter(name = "email", description = "Email do destinatário", required = true),
                    @Parameter(name = "userName", description = "Nome do usuário", required = true),
                    @Parameter(name = "appointmentDate", description = "Data do agendamento", required = true),
                    @Parameter(name = "interpreterName", description = "Nome do intérprete", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email de confirmação enviado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor"),
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
        data.put(USER_NAME, userName);
        data.put("appointmentDate", appointmentDate);
        data.put("interpreterName", interpreterName);

        return ResponseEntity.ok(ApiResponseDTO.success(
                success ? "Email de confirmação enviado com sucesso" : "Falha ao enviar email de confirmação",
                data));
    }

    @GetMapping("/template/{key}")
    @Operation(
            summary = "Buscar template por chave",
            description = "Retorna um template HTML armazenado no banco de dados",
            parameters = {
                    @Parameter(name = "key", description = "Chave do template", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template encontrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class))
            ),
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

    @GetMapping("/interpreter/{id}/approve")
    @Operation(
            summary = "Aprovar cadastro de intérprete",
            description = "Aprova o cadastro de um intérprete e envia email de confirmação",
            parameters = {
                    @Parameter(name = "id", description = "ID do intérprete", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastro aprovado com sucesso",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "400", description = "ID inválido",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "404", description = "Intérprete não encontrado",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "text/html"))
    })
    public ResponseEntity<String> approveInterpreter(@PathVariable String id) {
        try {
            UUID interpreterId = UUID.fromString(id);
            boolean success = interpreterService.approveInterpreter(interpreterId);

            String responseMessage = success
                    ? "Cadastro do intérprete aprovado com sucesso."
                    : "Falha ao aprovar cadastro do intérprete.";

            return ResponseEntity.ok(emailService.getAdminRegistrationFeedbackHtml(responseMessage));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(emailService.getAdminRegistrationFeedbackHtml(ex.getMessage()));
        } catch (Exception e) {
            log.error("Erro ao aprovar cadastro do intérprete {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(emailService.getAdminRegistrationFeedbackHtml("Erro ao aprovar cadastro do intérprete."));
        }
    }

    @GetMapping("/interpreter/{id}/reject")
    @Operation(
            summary = "Recusar cadastro de intérprete",
            description = "Recusa o cadastro de um intérprete e envia email de notificação",
            parameters = {
                    @Parameter(name = "id", description = "ID do intérprete", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastro recusado com sucesso",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "400", description = "ID inválido",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "404", description = "Intérprete não encontrado",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "text/html"))
    })
    public ResponseEntity<String> rejectInterpreter(@PathVariable String id) {
        try {
            UUID interpreterId = UUID.fromString(id);
            boolean success = interpreterService.rejectInterpreter(interpreterId);

            String responseMessage = success
                    ? "Cadastro do intérprete recusado com sucesso."
                    : "Falha ao recusar cadastro do intérprete.";

            return ResponseEntity.ok(emailService.getAdminRegistrationFeedbackHtml(responseMessage));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(emailService.getAdminRegistrationFeedbackHtml(ex.getMessage()));
        } catch (Exception e) {
            log.error("Erro ao recusar cadastro do intérprete {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(emailService.getAdminRegistrationFeedbackHtml("Erro ao recusar cadastro do intérprete."));
        }
    }
}
