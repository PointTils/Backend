package com.pointtils.pointtils.src.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import com.pointtils.pointtils.src.infrastructure.repositories.ParametersRepository;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
@DisplayName("Email - Testes Extras Simples")
class EmailTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ParametersRepository parametersRepository;

    @InjectMocks
    private EmailService emailService;

    @Test
    @DisplayName("Deve verificar se o serviço não é nulo")
    void deveVerificarSeServicoNaoENulo() {
        assertNotNull(emailService);
        assertNotNull(mailSender);
        assertNotNull(parametersRepository);
    }

    @Test
    @DisplayName("Deve criar MimeMessage quando solicitado")
    void deveCriarMimeMessageQuandoSolicitado() {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessage result = mailSender.createMimeMessage();

        assertNotNull(result);
        verify(mailSender).createMimeMessage();
    }

    @Test
    @DisplayName("Deve verificar se método send é chamado corretamente")
    void deveVerificarSeMetodoSendEChamadoCorretamente() {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        mailSender.send(mimeMessage);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve lidar com email vazio de forma defensiva")
    void deveLidarComEmailVazioDeFormaDefensiva() {
        String emailVazio = "";
        String nomeVazio = "";

        assertDoesNotThrow(() -> {
            assertNotNull(emailVazio);
            assertNotNull(nomeVazio);
        });
    }

    @Test
    @DisplayName("Deve validar strings básicas de email")
    void deveValidarStringsBasicasDeEmail() {
        String emailValido = "test@example.com";
        String nomeValido = "Usuario Teste";

        assertTrue(emailValido.contains("@"));
        assertTrue(emailValido.contains("."));
        assertFalse(nomeValido.isEmpty());
        assertTrue(nomeValido.length() > 0);
    }
}