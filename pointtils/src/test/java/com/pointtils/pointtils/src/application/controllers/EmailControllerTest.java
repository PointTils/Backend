package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.EmailRequestDTO;
import com.pointtils.pointtils.src.application.services.EmailService;
import com.pointtils.pointtils.src.infrastructure.configs.JwtService;
import com.pointtils.pointtils.src.infrastructure.configs.MemoryBlacklistService;
import com.pointtils.pointtils.src.infrastructure.configs.TestEmailConfiguration;
import com.pointtils.pointtils.src.infrastructure.configs.TestSecurityConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
@Import({TestEmailConfiguration.class, TestSecurityConfiguration.class})
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private MemoryBlacklistService memoryBlacklistService;

    @Test
    @DisplayName("Deve enviar email simples com sucesso")
    void deveEnviarEmailSimplesComSucesso() throws Exception {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "destinatario@exemplo.com",
            "Assunto do Email",
            "Corpo do email",
            "PointTils"
        );

        when(emailService.sendSimpleEmail(any(EmailRequestDTO.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/v1/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "to": "destinatario@exemplo.com",
                        "subject": "Assunto do Email",
                        "body": "Corpo do email",
                        "fromName": "PointTils"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email enviado com sucesso"))
                .andExpect(jsonPath("$.to").value("destinatario@exemplo.com"));
    }

    @Test
    @DisplayName("Deve retornar erro quando falhar ao enviar email simples")
    void deveRetornarErroQuandoFalharAoEnviarEmailSimples() throws Exception {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "destinatario@exemplo.com",
            "Assunto do Email",
            "Corpo do email",
            "PointTils"
        );

        when(emailService.sendSimpleEmail(any(EmailRequestDTO.class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/v1/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "to": "destinatario@exemplo.com",
                        "subject": "Assunto do Email",
                        "body": "Corpo do email",
                        "fromName": "PointTils"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Falha ao enviar email"))
                .andExpect(jsonPath("$.to").value("destinatario@exemplo.com"));
    }

    @Test
    @DisplayName("Deve enviar email HTML com sucesso")
    void deveEnviarEmailHtmlComSucesso() throws Exception {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "destinatario@exemplo.com",
            "Assunto do Email HTML",
            "<html><body><h1>Email HTML</h1></body></html>",
            "PointTils"
        );

        when(emailService.sendHtmlEmail(any(EmailRequestDTO.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/v1/email/send-html")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "to": "destinatario@exemplo.com",
                        "subject": "Assunto do Email HTML",
                        "body": "<html><body><h1>Email HTML</h1></body></html>",
                        "fromName": "PointTils"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email HTML enviado com sucesso"))
                .andExpect(jsonPath("$.to").value("destinatario@exemplo.com"));
    }

    @Test
    @DisplayName("Deve retornar erro quando falhar ao enviar email HTML")
    void deveRetornarErroQuandoFalharAoEnviarEmailHtml() throws Exception {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "destinatario@exemplo.com",
            "Assunto do Email HTML",
            "<html><body><h1>Email HTML</h1></body></html>",
            "PointTils"
        );

        when(emailService.sendHtmlEmail(any(EmailRequestDTO.class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/v1/email/send-html")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "to": "destinatario@exemplo.com",
                        "subject": "Assunto do Email HTML",
                        "body": "<html><body><h1>Email HTML</h1></body></html>",
                        "fromName": "PointTils"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Falha ao enviar email HTML"))
                .andExpect(jsonPath("$.to").value("destinatario@exemplo.com"));
    }

    @Test
    @DisplayName("Deve enviar email de boas-vindas com sucesso")
    void deveEnviarEmailDeBoasVindasComSucesso() throws Exception {
        // Arrange
        when(emailService.sendWelcomeEmail("novousuario@exemplo.com", "João Silva"))
            .thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/v1/email/welcome/novousuario@exemplo.com")
                .param("userName", "João Silva"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email de boas-vindas enviado com sucesso"))
                .andExpect(jsonPath("$.to").value("novousuario@exemplo.com"))
                .andExpect(jsonPath("$.userName").value("João Silva"));
    }

    @Test
    @DisplayName("Deve enviar email de recuperação de senha com sucesso")
    void deveEnviarEmailDeRecuperacaoDeSenhaComSucesso() throws Exception {
        // Arrange
        when(emailService.sendPasswordResetEmail("usuario@exemplo.com", "Maria Silva", "ABC123"))
            .thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/v1/email/password-reset/usuario@exemplo.com")
                .param("userName", "Maria Silva")
                .param("resetToken", "ABC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email de recuperação enviado com sucesso"))
                .andExpect(jsonPath("$.to").value("usuario@exemplo.com"))
                .andExpect(jsonPath("$.userName").value("Maria Silva"));
    }

    @Test
    @DisplayName("Deve enviar email de confirmação de agendamento com sucesso")
    void deveEnviarEmailDeConfirmacaoDeAgendamentoComSucesso() throws Exception {
        // Arrange
        when(emailService.sendAppointmentConfirmationEmail(
            "cliente@exemplo.com", "Carlos Santos", "15/12/2024 às 14:00", "Ana Interprete"))
            .thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/v1/email/appointment-confirmation/cliente@exemplo.com")
                .param("userName", "Carlos Santos")
                .param("appointmentDate", "15/12/2024 às 14:00")
                .param("interpreterName", "Ana Interprete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email de confirmação enviado com sucesso"))
                .andExpect(jsonPath("$.to").value("cliente@exemplo.com"))
                .andExpect(jsonPath("$.userName").value("Carlos Santos"))
                .andExpect(jsonPath("$.appointmentDate").value("15/12/2024 às 14:00"))
                .andExpect(jsonPath("$.interpreterName").value("Ana Interprete"));
    }

    @Test
    @DisplayName("Deve retornar erro quando email for inválido")
    void deveRetornarErroQuandoEmailForInvalido() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/v1/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "to": "email-invalido",
                        "subject": "Assunto do Email",
                        "body": "Corpo do email",
                        "fromName": "PointTils"
                    }
                    """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Deve retornar erro quando campos obrigatórios estiverem faltando")
    void deveRetornarErroQuandoCamposObrigatoriosEstiveremFaltando() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/v1/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "subject": "Assunto do Email",
                        "body": "Corpo do email"
                    }
                    """))
                .andExpect(status().isBadRequest());
    }
}
