package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.EmailRequestDTO;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailService(javaMailSender);
    }

    @Test
    @DisplayName("Deve enviar email simples com sucesso")
    void deveEnviarEmailSimplesComSucesso() {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "destinatario@exemplo.com",
            "Assunto do Email",
            "Corpo do email",
            "PointTils"
        );

        // Act
        boolean result = emailService.sendSimpleEmail(emailRequest);

        // Assert
        assertTrue(result);
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deve retornar false quando ocorrer erro ao enviar email simples")
    void deveRetornarFalseQuandoErroAoEnviarEmailSimples() {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "destinatario@exemplo.com",
            "Assunto do Email",
            "Corpo do email",
            "PointTils"
        );

        doThrow(new RuntimeException("Erro de SMTP"))
            .when(javaMailSender)
            .send(any(SimpleMailMessage.class));

        // Act
        boolean result = emailService.sendSimpleEmail(emailRequest);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Deve enviar email HTML com sucesso")
    void deveEnviarEmailHtmlComSucesso() {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "destinatario@exemplo.com",
            "Assunto do Email HTML",
            "<html><body><h1>Email HTML</h1></body></html>",
            "PointTils"
        );

        // Configurar o mock apenas para este teste
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        // Act
        boolean result = emailService.sendHtmlEmail(emailRequest);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Deve retornar false quando ocorrer erro ao enviar email HTML")
    void deveRetornarFalseQuandoErroAoEnviarEmailHtml() {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "destinatario@exemplo.com",
            "Assunto do Email HTML",
            "<html><body><h1>Email HTML</h1></body></html>",
            "PointTils"
        );

        // Configurar o mock para lançar exceção
        when(javaMailSender.createMimeMessage()).thenThrow(new RuntimeException("Erro de SMTP"));

        // Act
        boolean result = emailService.sendHtmlEmail(emailRequest);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Deve enviar email de boas-vindas com sucesso")
    void deveEnviarEmailDeBoasVindasComSucesso() {
        // Arrange
        String email = "novousuario@exemplo.com";
        String userName = "João Silva";

        // Configurar o mock para este teste
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        // Act
        boolean result = emailService.sendWelcomeEmail(email, userName);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Deve enviar email de recuperação de senha com sucesso")
    void deveEnviarEmailDeRecuperacaoDeSenhaComSucesso() {
        // Arrange
        String email = "usuario@exemplo.com";
        String userName = "Maria Silva";
        String resetToken = "ABC123";

        // Configurar o mock para este teste
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        // Act
        boolean result = emailService.sendPasswordResetEmail(email, userName, resetToken);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Deve enviar email de confirmação de agendamento com sucesso")
    void deveEnviarEmailDeConfirmacaoDeAgendamentoComSucesso() {
        // Arrange
        String email = "cliente@exemplo.com";
        String userName = "Carlos Santos";
        String appointmentDate = "15/12/2024 às 14:00";
        String interpreterName = "Ana Interprete";

        // Configurar o mock para este teste
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        // Act
        boolean result = emailService.sendAppointmentConfirmationEmail(
            email, userName, appointmentDate, interpreterName);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Deve configurar propriedades de email corretamente")
    void deveConfigurarPropriedadesDeEmailCorretamente() {
        // Arrange
        ReflectionTestUtils.setField(emailService, "emailFrom", "test@pointtils.com");
        ReflectionTestUtils.setField(emailService, "senderName", "PointTils Test");

        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "destinatario@exemplo.com",
            "Assunto do Email",
            "Corpo do email",
            "PointTils"
        );

        // Act
        boolean result = emailService.sendSimpleEmail(emailRequest);

        // Assert
        assertTrue(result);
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deve retornar false quando emailRequest for nulo")
    void deveRetornarFalseQuandoEmailRequestForNulo() {
        // Act
        boolean result = emailService.sendSimpleEmail(null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar false quando emailRequest HTML for nulo")
    void deveRetornarFalseQuandoEmailRequestHtmlForNulo() {
        // Act
        boolean result = emailService.sendHtmlEmail(null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar false quando ocorrer erro no MimeMessageHelper")
    void deveRetornarFalseQuandoErroNoMimeMessageHelper() {
        // Arrange
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "destinatario@exemplo.com",
            "Assunto do Email HTML",
            "<html><body><h1>Email HTML</h1></body></html>",
            "PointTils"
        );

        // Configurar o mock para lançar exceção no MimeMessage
        when(javaMailSender.createMimeMessage()).thenThrow(new RuntimeException("Erro no MimeMessage"));

        // Act
        boolean result = emailService.sendHtmlEmail(emailRequest);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar false quando ocorrer erro no envio de email de boas-vindas")
    void deveRetornarFalseQuandoErroNoEnvioDeEmailDeBoasVindas() {
        // Arrange
        String email = "novousuario@exemplo.com";
        String userName = "João Silva";

        // Configurar o mock para lançar exceção
        when(javaMailSender.createMimeMessage()).thenThrow(new RuntimeException("Erro de SMTP"));

        // Act
        boolean result = emailService.sendWelcomeEmail(email, userName);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar false quando ocorrer erro no envio de email de recuperação de senha")
    void deveRetornarFalseQuandoErroNoEnvioDeEmailDeRecuperacaoDeSenha() {
        // Arrange
        String email = "usuario@exemplo.com";
        String userName = "Maria Silva";
        String resetToken = "ABC123";

        // Configurar o mock para lançar exceção
        when(javaMailSender.createMimeMessage()).thenThrow(new RuntimeException("Erro de SMTP"));

        // Act
        boolean result = emailService.sendPasswordResetEmail(email, userName, resetToken);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar false quando ocorrer erro no envio de email de confirmação de agendamento")
    void deveRetornarFalseQuandoErroNoEnvioDeEmailDeConfirmacaoDeAgendamento() {
        // Arrange
        String email = "cliente@exemplo.com";
        String userName = "Carlos Santos";
        String appointmentDate = "15/12/2024 às 14:00";
        String interpreterName = "Ana Interprete";

        // Configurar o mock para lançar exceção
        when(javaMailSender.createMimeMessage()).thenThrow(new RuntimeException("Erro de SMTP"));

        // Act
        boolean result = emailService.sendAppointmentConfirmationEmail(
            email, userName, appointmentDate, interpreterName);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Deve usar valores padrão quando propriedades não estiverem configuradas")
    void deveUsarValoresPadraoQuandoPropriedadesNaoEstiveremConfiguradas() {
        // Arrange
        ReflectionTestUtils.setField(emailService, "emailFrom", null);
        ReflectionTestUtils.setField(emailService, "senderName", null);

        EmailRequestDTO emailRequest = new EmailRequestDTO(
            "destinatario@exemplo.com",
            "Assunto do Email",
            "Corpo do email",
            "PointTils"
        );

        // Act
        boolean result = emailService.sendSimpleEmail(emailRequest);

        // Assert
        assertTrue(result);
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }
}
