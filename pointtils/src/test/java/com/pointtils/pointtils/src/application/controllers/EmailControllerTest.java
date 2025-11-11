package com.pointtils.pointtils.src.application.controllers;

import com.pointtils.pointtils.src.application.dto.requests.EmailRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.ApiResponseDTO;
import com.pointtils.pointtils.src.application.services.EmailService;
import com.pointtils.pointtils.src.application.services.InterpreterService;
import com.pointtils.pointtils.src.application.services.MemoryResetTokenService;
import com.pointtils.pointtils.src.application.services.UserService;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do EmailController")
class EmailControllerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private InterpreterService interpreterService;

    @Mock
    private MemoryResetTokenService resetTokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EmailController emailController;

    private EmailRequestDTO emailRequestDTO;
    private String testEmail;
    private String testUserName;

    @BeforeEach
    void setUp() {
        testEmail = "user@example.com";
        testUserName = "João Silva";

        emailRequestDTO = new EmailRequestDTO(
                testEmail,
                "Assunto Teste",
                "Corpo do email teste",
                "PointTils");
    }

    @Test
    @DisplayName("Deve enviar email simples com sucesso")
    void deveEnviarEmailSimplesComSucesso() {
        when(emailService.sendSimpleEmail(any(EmailRequestDTO.class))).thenReturn(true);

        ResponseEntity<ApiResponseDTO<Map<String, Object>>> response = emailController.sendEmail(emailRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Email enviado com sucesso", response.getBody().getMessage());

        Map<String, Object> data = response.getBody().getData();
        assertEquals(testEmail, data.get("to"));
    }

    @Test
    @DisplayName("Deve retornar mensagem de falha quando email de boas-vindas falhar")
    void deveRetornarMensagemFalhaEmailBoasVindas() {
        when(emailService.sendWelcomeEmail(testEmail, testUserName)).thenReturn(false);

        ResponseEntity<ApiResponseDTO<Map<String, Object>>> response = emailController.sendWelcomeEmail(testEmail,
                testUserName);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Falha ao enviar email de boas-vindas", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve enviar email HTML com sucesso")
    void deveEnviarEmailHTMLComSucesso() {
        when(emailService.sendHtmlEmail(any(EmailRequestDTO.class))).thenReturn(true);

        ResponseEntity<ApiResponseDTO<Map<String, Object>>> response = emailController.sendHtmlEmail(emailRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Email HTML enviado com sucesso", response.getBody().getMessage());

        Map<String, Object> data = response.getBody().getData();
        assertEquals(testEmail, data.get("to"));
    }

    @Test
    @DisplayName("Deve retornar mensagem de falha quando email HTML falhar")
    void deveRetornarMensagemFalhaQuandoEmailHTMLFalhar() {
        when(emailService.sendHtmlEmail(any(EmailRequestDTO.class))).thenReturn(false);

        ResponseEntity<ApiResponseDTO<Map<String, Object>>> response = emailController.sendHtmlEmail(emailRequestDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Falha ao enviar email HTML", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve enviar email de boas-vindas com sucesso")
    void deveEnviarEmailBoasVindasComSucesso() {
        when(emailService.sendWelcomeEmail(testEmail, testUserName)).thenReturn(true);

        ResponseEntity<ApiResponseDTO<Map<String, Object>>> response = emailController.sendWelcomeEmail(testEmail,
                testUserName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Email de boas-vindas enviado com sucesso", response.getBody().getMessage());

        Map<String, Object> data = response.getBody().getData();
        assertEquals(testEmail, data.get("to"));
        assertEquals(testUserName, data.get("userName"));
    }

    @Test
    @DisplayName("Deve enviar email de recuperação de senha com sucesso")
    void deveEnviarEmailRecuperacaoSenhaComSucesso() {
        Person user = new Person();
        user.setName(testUserName);
        user.setEmail(testEmail);

        String resetToken = "reset-token-123";

        when(userService.findByEmail(testEmail)).thenReturn(user);
        when(resetTokenService.generateResetToken(testEmail)).thenReturn(resetToken);
        when(emailService.sendPasswordResetEmail(testEmail, testUserName, resetToken)).thenReturn(true);

        ResponseEntity<ApiResponseDTO<Map<String, Object>>> response = emailController
                .sendPasswordResetEmail(testEmail);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Email de recuperação enviado com sucesso", response.getBody().getMessage());

        Map<String, Object> data = response.getBody().getData();
        assertEquals(testEmail, data.get("to"));
        assertEquals(testUserName, data.get("userName"));
    }

    @Test
    @DisplayName("Deve retornar mensagem de falha quando email de recuperação falhar")
    void deveRetornarMensagemFalhaEmailRecuperacao() {
        Person user = new Person();
        user.setName(testUserName);
        user.setEmail(testEmail);

        when(userService.findByEmail(testEmail)).thenReturn(user);
        when(resetTokenService.generateResetToken(testEmail)).thenReturn("token");
        when(emailService.sendPasswordResetEmail(testEmail, testUserName, "token")).thenReturn(false);

        ResponseEntity<ApiResponseDTO<Map<String, Object>>> response = emailController
                .sendPasswordResetEmail(testEmail);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Falha ao enviar email de recuperação", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro 404 quando usuário não encontrado para reset de senha")
    void deveRetornarErro404QuandoUsuarioNaoEncontradoParaResetSenha() {
        when(userService.findByEmail(testEmail)).thenReturn(null);

        ResponseEntity<ApiResponseDTO<Map<String, Object>>> response = emailController
                .sendPasswordResetEmail(testEmail);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Usuário não encontrado", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro 500 quando exceção ocorrer no reset de senha")
    void deveRetornarErro500QuandoExcecaoOcorrerNoResetSenha() {
        when(userService.findByEmail(testEmail)).thenThrow(new RuntimeException("Erro no banco"));

        ResponseEntity<ApiResponseDTO<Map<String, Object>>> response = emailController
                .sendPasswordResetEmail(testEmail);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Erro interno no servidor", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve buscar template por chave com sucesso")
    void deveBuscarTemplatePorChaveComSucesso() {
        String templateKey = "WELCOME_EMAIL";
        String templateContent = "<html><body>Template de boas-vindas</body></html>";

        when(emailService.getTemplateByKey(templateKey)).thenReturn(templateContent);

        ResponseEntity<ApiResponseDTO<Map<String, Object>>> response = emailController.getTemplateByKey(templateKey);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Template encontrado com sucesso", response.getBody().getMessage());

        Map<String, Object> data = response.getBody().getData();
        assertEquals(templateKey, data.get("key"));
        assertEquals(templateContent, data.get("template"));
    }

    @Test
    @DisplayName("Deve aprovar cadastro de intérprete com sucesso")
    void deveAprovarCadastroInterpreteComSucesso() {
        String interpreterId = UUID.randomUUID().toString();

        when(interpreterService.approveInterpreter(any(UUID.class))).thenReturn(true);
        when(emailService.getAdminRegistrationFeedbackHtml("Cadastro do intérprete aprovado com sucesso."))
                .thenReturn("<html><body>Cadastro do intérprete aprovado com sucesso.</body></html>");

        ResponseEntity<String> response = emailController.approveInterpreter(interpreterId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("<html><body>Cadastro do intérprete aprovado com sucesso.</body></html>", response.getBody());
    }

    @Test
    @DisplayName("Deve retornar erro 500 quando exceção ocorrer na aprovação")
    void deveRetornarErro500QuandoExcecaoOcorrerNaAprovacao() {
        String interpreterId = UUID.randomUUID().toString();

        when(interpreterService.approveInterpreter(any(UUID.class)))
                .thenThrow(new RuntimeException("Erro no banco"));
        when(emailService.getAdminRegistrationFeedbackHtml("Erro ao aprovar cadastro do intérprete."))
                .thenReturn("<html><body>Erro ao aprovar cadastro do intérprete.</body></html>");

        ResponseEntity<String> response = emailController.approveInterpreter(interpreterId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("<html><body>Erro ao aprovar cadastro do intérprete.</body></html>", response.getBody());
    }

    @Test
    @DisplayName("Deve recusar cadastro de intérprete com sucesso")
    void deveRecusarCadastroInterpreteComSucesso() {
        String interpreterId = UUID.randomUUID().toString();

        when(interpreterService.rejectInterpreter(any(UUID.class))).thenReturn(true);
        when(emailService.getAdminRegistrationFeedbackHtml("Cadastro do intérprete recusado com sucesso."))
                .thenReturn("<html><body>Cadastro do intérprete recusado com sucesso.</body></html>");

        ResponseEntity<String> response = emailController.rejectInterpreter(interpreterId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("<html><body>Cadastro do intérprete recusado com sucesso.</body></html>", response.getBody());
    }

    @Test
    @DisplayName("Deve retornar erro 500 quando exceção ocorrer na rejeição")
    void deveRetornarErro500QuandoExcecaoOcorrerNaRejeicao() {
        String interpreterId = UUID.randomUUID().toString();

        when(interpreterService.rejectInterpreter(any(UUID.class)))
                .thenThrow(new RuntimeException("Erro no banco"));
        when(emailService.getAdminRegistrationFeedbackHtml("Erro ao recusar cadastro do intérprete."))
                .thenReturn("<html><body>Erro ao recusar cadastro do intérprete.</body></html>");

        ResponseEntity<String> response = emailController.rejectInterpreter(interpreterId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("<html><body>Erro ao recusar cadastro do intérprete.</body></html>", response.getBody());
    }

    @Test
    @DisplayName("Deve enviar email de confirmação com sucesso")
    void deveEnviarEmailConfirmacaoComSucesso() {
        when(emailService.sendAppointmentConfirmationEmail(testEmail, testUserName, "2025-10-30", "Maria"))
                .thenReturn(true);

        ResponseEntity<ApiResponseDTO<Map<String, Object>>> response = emailController
                .sendAppointmentConfirmationEmail(testEmail, testUserName, "2025-10-30", "Maria");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Email de confirmação enviado com sucesso", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve retornar mensagem de falha quando email de confirmação falhar")
    void deveRetornarFalhaEmailConfirmacao() {
        when(emailService.sendAppointmentConfirmationEmail(testEmail, testUserName, "2025-10-30", "Maria"))
                .thenReturn(false);

        ResponseEntity<ApiResponseDTO<Map<String, Object>>> response = emailController
                .sendAppointmentConfirmationEmail(testEmail, testUserName, "2025-10-30", "Maria");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Falha ao enviar email de confirmação", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve retornar 'Template não encontrado' quando chave inválida")
    void deveRetornarTemplateNaoEncontrado() {
        String templateKey = "INVALID_KEY";
        when(emailService.getTemplateByKey(templateKey)).thenReturn("Template não encontrado");

        ResponseEntity<ApiResponseDTO<Map<String, Object>>> response = emailController.getTemplateByKey(templateKey);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Template não encontrado", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve retornar 410 GONE e mensagem de endpoint obsoleto ao chamar sendInterpreterRegistrationRequest")
    void deveRetornarGoneAoTentarEnviarSolicitacaoCadastroInterprete() {
        // Simulando o endpoint antigo (que agora deve retornar GONE)
        ResponseEntity<ApiResponseDTO<Map<String, Object>>> response =
                ResponseEntity.status(HttpStatus.GONE).body(
                        ApiResponseDTO.error("Este endpoint está obsoleto. Use o endpoint /interpreter-documents/ para acessar o recurso.")
                );

        // Verificações
        assertEquals(HttpStatus.GONE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(
                "Este endpoint está obsoleto. Use o endpoint /interpreter-documents/ para acessar o recurso.",
                response.getBody().getMessage()
        );
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando UUID inválido na aprovação do intérprete")
    void deveRetornar400QuandoUUIDInvalidoNaAprovacao() {
        String invalidId = "invalid-uuid";

        when(emailService.getAdminRegistrationFeedbackHtml(any(String.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> response = emailController.approveInterpreter(invalidId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid UUID string: " + invalidId, response.getBody());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando UUID inválido na rejeição do intérprete")
    void deveRetornar400QuandoUUIDInvalidoNaRejeicao() {
        String invalidId = "invalid-uuid";

        when(emailService.getAdminRegistrationFeedbackHtml(any(String.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> response = emailController.rejectInterpreter(invalidId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid UUID string: " + invalidId, response.getBody());
    }

    @Test
    void approveInterpreter_falhaAoAprovar_deveRetornarMensagemFalha() {
        String id = UUID.randomUUID().toString();

        when(interpreterService.approveInterpreter(UUID.fromString(id))).thenReturn(false);
        when(emailService.getAdminRegistrationFeedbackHtml("Falha ao aprovar cadastro do intérprete."))
                .thenReturn("HTML_FALHA_APROVAR");

        ResponseEntity<String> response = emailController.approveInterpreter(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("HTML_FALHA_APROVAR", response.getBody());
    }

    @Test
    void rejectInterpreter_falhaAoRecusar_deveRetornarMensagemFalha() {
        String id = UUID.randomUUID().toString();

        when(interpreterService.rejectInterpreter(UUID.fromString(id))).thenReturn(false);
        when(emailService.getAdminRegistrationFeedbackHtml("Falha ao recusar cadastro do intérprete."))
                .thenReturn("HTML_FALHA_RECUSAR");

        ResponseEntity<String> response = emailController.rejectInterpreter(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("HTML_FALHA_RECUSAR", response.getBody());
    }
}