package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.EmailRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterRegistrationEmailDTO;
import com.pointtils.pointtils.src.core.domain.entities.Parameters;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.ParametersRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do EmailService")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ParametersRepository parametersRepository;

    @Mock
    private S3Service s3service;

    @Mock
    private InterpreterRepository interpreterRepository;

    @InjectMocks
    private EmailService emailService;

    private EmailRequestDTO emailRequestDTO;
    private String testEmail;
    private String testUserName;
    private String testToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "emailFrom", "test@pointtils.com");
        ReflectionTestUtils.setField(emailService, "senderName", "PointTils Test");

        testEmail = "user@example.com";
        testUserName = "João Silva";
        testToken = "reset-token-123";

        emailRequestDTO = new EmailRequestDTO(
                testEmail,
                "Assunto Teste",
                "Corpo do email teste",
                "PointTils");
    }

    @Test
    @DisplayName("Deve enviar email simples com sucesso")
    void deveEnviarEmailSimplesComSucesso() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        boolean result = emailService.sendSimpleEmail(emailRequestDTO);

        assertTrue(result);
        verify(mailSender).send(any(SimpleMailMessage.class));

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("test@pointtils.com", sentMessage.getFrom());
        assertArrayEquals(new String[]{testEmail}, sentMessage.getTo());
        assertEquals("Assunto Teste", sentMessage.getSubject());
        assertEquals("Corpo do email teste", sentMessage.getText());
    }

    @Test
    @DisplayName("Deve retornar false quando emailRequestDTO for nulo no envio simples")
    void deveRetornarFalseQuandoEmailRequestDTOForNuloNoEnvioSimples() {
        boolean result = emailService.sendSimpleEmail(null);

        assertFalse(result);
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deve retornar false quando ocorrer exceção no envio simples")
    void deveRetornarFalseQuandoOcorrerExcecaoNoEnvioSimples() {
        doThrow(new RuntimeException("Erro de conexão")).when(mailSender).send(any(SimpleMailMessage.class));

        boolean result = emailService.sendSimpleEmail(emailRequestDTO);

        assertFalse(result);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deve enviar email HTML com sucesso")
    void deveEnviarEmailHTMLComSucesso() {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        boolean result = emailService.sendHtmlEmail(emailRequestDTO);

        assertTrue(result);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve enviar email com anexos com sucesso")
    void deveEnviarEmailComAnexosComSucesso() {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        byte[] attachment1 = "Conteúdo do anexo 1".getBytes();
        byte[] attachment2 = "Conteúdo do anexo 2".getBytes();
        List<byte[]> attachments = List.of(attachment1, attachment2);
        String attachmentName1 = "anexo1.txt";
        String attachmentName2 = "anexo2.txt";
        List<String> attachmentNames = List.of(attachmentName1, attachmentName2);

        boolean result = emailService.sendEmailWithAttachments(emailRequestDTO, attachments, attachmentNames);

        assertTrue(result);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve retornar false quando emailRequestDTO for nulo no envio HTML")
    void deveRetornarFalseQuandoEmailRequestDTOForNuloNoEnvioHTML() {
        boolean result = emailService.sendHtmlEmail(null);

        assertFalse(result);
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve retornar false quando ocorrer exceção no envio HTML")
    void deveRetornarFalseQuandoOcorrerExcecaoNoEnvioHTML() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Erro de conexão"));

        boolean result = emailService.sendHtmlEmail(emailRequestDTO);

        assertFalse(result);
        verify(mailSender).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve enviar email de boas-vindas com sucesso")
    void deveEnviarEmailBoasVindasComSucesso() {
        String welcomeTemplate = "<html><body><h1>Bem-vindo {{nome}}!</h1><p>Ano: {{ano}}</p><p>De: {{senderName}}</p></body></html>";
        Parameters parameter = new Parameters();
        parameter.setKey("WELCOME_EMAIL");
        parameter.setValue(welcomeTemplate);

        when(parametersRepository.findByKey("WELCOME_EMAIL")).thenReturn(Optional.of(parameter));

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        boolean result = emailService.sendWelcomeEmail(testEmail, testUserName);

        assertTrue(result);
        verify(parametersRepository).findByKey("WELCOME_EMAIL");
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve enviar email de recuperação de senha com sucesso")
    void deveEnviarEmailRecuperacaoSenhaComSucesso() {
        String resetTemplate = "<html><body><h1>Olá {{nome}}!</h1><p>Token: {{resetToken}}</p><p>Ano: {{ano}}</p></body></html>";
        Parameters parameter = new Parameters();
        parameter.setKey("PASSWORD_RESET");
        parameter.setValue(resetTemplate);

        when(parametersRepository.findByKey("PASSWORD_RESET")).thenReturn(Optional.of(parameter));

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        boolean result = emailService.sendPasswordResetEmail(testEmail, testUserName, testToken);

        assertTrue(result);
        verify(parametersRepository).findByKey("PASSWORD_RESET");
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve enviar email de confirmação de agendamento com sucesso")
    void deveEnviarEmailConfirmacaoAgendamentoComSucesso() {
        String appointmentTemplate = "<html><body><h1>Olá {{nome}}!</h1><p>Data: {{appointmentDate}}</p><p>Intérprete: {{interpreterName}}</p></body></html>";
        Parameters parameter = new Parameters();
        parameter.setKey("APPOINTMENT_CONFIRMATION");
        parameter.setValue(appointmentTemplate);

        when(parametersRepository.findByKey("APPOINTMENT_CONFIRMATION")).thenReturn(Optional.of(parameter));

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        boolean result = emailService.sendAppointmentConfirmationEmail(
                testEmail, testUserName, "2024-12-01 10:00", "Maria Intérprete");

        assertTrue(result);
        verify(parametersRepository).findByKey("APPOINTMENT_CONFIRMATION");
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve enviar email de solicitação de cadastro de intérprete com sucesso")
    void deveEnviarEmailSolicitacaoCadastroInterpreteComSucesso() {
        String interpreterTemplate = "<html><body><h1>Nova solicitação</h1><p>Nome: {{nome}}</p><p>CPF: {{cpf}}</p><p>CNPJ: {{cnpj}}</p><p>Email: {{email}}</p><p>Telefone: {{telefone}}</p><a href=\"{link_api}\">Aceitar</a><a href=\"{link_api}\">Recusar</a></body></html>";
        Parameters parameter = new Parameters();
        List<MultipartFile> files = List.of();
        parameter.setKey("PENDING_INTERPRETER_ADMIN");
        parameter.setValue(interpreterTemplate);

        when(parametersRepository.findByKey("PENDING_INTERPRETER_ADMIN")).thenReturn(Optional.of(parameter));

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        InterpreterRegistrationEmailDTO dto = InterpreterRegistrationEmailDTO.builder()
                .adminEmail("admin@pointtils.com")
                .interpreterName("João")
                .cpf("123")
                .cnpj("456")
                .email("email")
                .phone("phone")
                .acceptLink("http://accept")
                .rejectLink("http://reject")
                .files(files)
                .build();


        boolean result = emailService.sendInterpreterRegistrationRequestEmail(dto);


        assertTrue(result);
        verify(parametersRepository).findByKey("PENDING_INTERPRETER_ADMIN");
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve enviar email de feedback aprovado para intérprete com sucesso")
    void deveEnviarEmailFeedbackAprovadoParaInterpreteComSucesso() {
        String feedbackTemplate = "<html><body><h1>Olá {{nome}}!</h1><p>{{respostaSolicitacao}}</p><p>Ano: {{ano}}</p></body></html>";
        Parameters parameter = new Parameters();
        parameter.setKey("PENDING_INTERPRETER");
        parameter.setValue(feedbackTemplate);

        when(parametersRepository.findByKey("PENDING_INTERPRETER")).thenReturn(Optional.of(parameter));

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        boolean result = emailService.sendInterpreterFeedbackEmail(testEmail, testUserName, true);

        assertTrue(result);
        verify(parametersRepository).findByKey("PENDING_INTERPRETER");
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve enviar email de feedback rejeitado para intérprete com sucesso")
    void deveEnviarEmailFeedbackRejeitadoParaInterpreteComSucesso() {
        String feedbackTemplate = "<html><body><h1>Olá {{nome}}!</h1><p>{{respostaSolicitacao}}</p><p>Ano: {{ano}}</p></body></html>";
        Parameters parameter = new Parameters();
        parameter.setKey("PENDING_INTERPRETER");
        parameter.setValue(feedbackTemplate);

        when(parametersRepository.findByKey("PENDING_INTERPRETER")).thenReturn(Optional.of(parameter));

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        boolean result = emailService.sendInterpreterFeedbackEmail(testEmail, testUserName, false);

        assertTrue(result);
        verify(parametersRepository).findByKey("PENDING_INTERPRETER");
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve buscar template por chave com sucesso")
    void deveBuscarTemplatePorChaveComSucesso() {
        String expectedTemplate = "<html><body>Template de teste</body></html>";
        Parameters parameter = new Parameters();
        parameter.setKey("TEST_KEY");
        parameter.setValue(expectedTemplate);

        when(parametersRepository.findByKey("TEST_KEY")).thenReturn(Optional.of(parameter));

        String result = emailService.getTemplateByKey("TEST_KEY");

        assertEquals(expectedTemplate, result);
        verify(parametersRepository).findByKey("TEST_KEY");
    }

    @Test
    @DisplayName("Deve retornar template padrão quando chave não encontrada")
    void deveRetornarTemplatePadraoQuandoChaveNaoEncontrada() {
        when(parametersRepository.findByKey("UNKNOWN_KEY")).thenReturn(Optional.empty());

        String result = emailService.getTemplateByKey("UNKNOWN_KEY");

        assertTrue(result.contains("Template não encontrado"));
        assertTrue(result.contains("UNKNOWN_KEY"));
        verify(parametersRepository).findByKey("UNKNOWN_KEY");
    }

    @Test
    @DisplayName("Deve processar template de boas-vindas corretamente")
    void deveProcessarTemplateBoasVindasCorretamente() {
        String template = "Olá {{nome}}! Bem-vindo ao {{senderName}}. Ano: {{ano}}";
        Parameters parameter = new Parameters();
        parameter.setKey("WELCOME_EMAIL");
        parameter.setValue(template);

        when(parametersRepository.findByKey("WELCOME_EMAIL")).thenReturn(Optional.of(parameter));

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendWelcomeEmail(testEmail, testUserName);

        verify(parametersRepository).findByKey("WELCOME_EMAIL");
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve processar template com valores nulos sem erro")
    void deveProcessarTemplateComValoresNulosSemErro() {
        String template = "Olá {{nome}}! Token: {{resetToken}}";
        Parameters parameter = new Parameters();
        parameter.setKey("PASSWORD_RESET");
        parameter.setValue(template);

        when(parametersRepository.findByKey("PASSWORD_RESET")).thenReturn(Optional.of(parameter));

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        boolean result = emailService.sendPasswordResetEmail(testEmail, null, null);

        assertTrue(result);
        verify(parametersRepository).findByKey("PASSWORD_RESET");
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve usar template padrão quando template do banco for nulo")
    void deveUsarTemplatePadraoQuandoTemplateDoBancoForNulo() {
        when(parametersRepository.findByKey("WELCOME_EMAIL")).thenReturn(Optional.empty());

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        boolean result = emailService.sendWelcomeEmail(testEmail, testUserName);

        assertTrue(result);
        verify(parametersRepository).findByKey("WELCOME_EMAIL");
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve processar template de confirmação de agendamento com todos os placeholders")
    void deveProcessarTemplateConfirmacaoAgendamentoComTodosPlaceholders() {
        String template = "Olá {{nome}}! Seu agendamento para {{appointmentDate}} com {{interpreterName}} foi confirmado. Ano: {{ano}}, Enviado por: {{senderName}}";
        Parameters parameter = new Parameters();
        parameter.setKey("APPOINTMENT_CONFIRMATION");
        parameter.setValue(template);

        when(parametersRepository.findByKey("APPOINTMENT_CONFIRMATION")).thenReturn(Optional.of(parameter));

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        boolean result = emailService.sendAppointmentConfirmationEmail(
                testEmail, testUserName, "2024-12-01 10:00", "Maria Intérprete");

        assertTrue(result);
        verify(parametersRepository).findByKey("APPOINTMENT_CONFIRMATION");
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve processar links de aceitar e recusar corretamente no template de intérprete")
    void deveProcessarLinksAceitarRecusarCorretamenteNoTemplateInterprete() {
        String template = "<p>Nome: {{nome}}</p><a href=\"{link_api}\" style=\"background-color: #008000;\">Aceitar</a><a href=\"{link_api}\" style=\"background-color: #FF0000;\">Recusar</a>";
        Parameters parameter = new Parameters();
        List<MultipartFile> files = List.of();
        parameter.setKey("PENDING_INTERPRETER_ADMIN");
        parameter.setValue(template);

        when(parametersRepository.findByKey("PENDING_INTERPRETER_ADMIN")).thenReturn(Optional.of(parameter));

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        InterpreterRegistrationEmailDTO dto = InterpreterRegistrationEmailDTO.builder()
                .adminEmail("admin@pointtils.com")
                .interpreterName("João")
                .cpf("123")
                .cnpj("456")
                .email("email")
                .phone("phone")
                .acceptLink("http://accept")
                .rejectLink("http://reject")
                .files(files)
                .build();

        boolean result = emailService.sendInterpreterRegistrationRequestEmail(dto);

        assertTrue(result);
        verify(parametersRepository).findByKey("PENDING_INTERPRETER_ADMIN");
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve tratar exceção ao processar template no envio de email de boas-vindas")
    void deveTratarExcecaoAoProcessarTemplateNoEnvioEmailBoasVindas() {
        when(parametersRepository.findByKey("WELCOME_EMAIL")).thenThrow(new RuntimeException("Erro no banco"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> emailService.sendWelcomeEmail(testEmail, testUserName));

        assertEquals("Erro no banco", exception.getMessage());
        verify(parametersRepository).findByKey("WELCOME_EMAIL");
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve validar que ano atual é inserido corretamente em todos os templates")
    void deveValidarQueAnoAtualEInseridoCorretamenteEmTodosTemplates() {
        String template = "Ano atual: {{ano}}";
        Parameters parameter = new Parameters();
        parameter.setValue(template);

        when(parametersRepository.findByKey(anyString())).thenReturn(Optional.of(parameter));

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        assertTrue(emailService.sendWelcomeEmail(testEmail, testUserName));
        assertTrue(emailService.sendPasswordResetEmail(testEmail, testUserName, testToken));
        assertTrue(emailService.sendAppointmentConfirmationEmail(testEmail, testUserName, "2024-12-01", "Intérprete"));
        assertTrue(emailService.sendInterpreterFeedbackEmail(testEmail, testUserName, true));

        verify(parametersRepository, times(4)).findByKey(anyString());
        verify(mailSender, times(4)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve manter comportamento consistente quando senderName for nulo ou vazio")
    void deveManterComportamentoConsistenteQuandoSenderNameForNuloOuVazio() {
        ReflectionTestUtils.setField(emailService, "senderName", "");

        String template = "Enviado por: {{senderName}}";
        Parameters parameter = new Parameters();
        parameter.setValue(template);

        when(parametersRepository.findByKey("WELCOME_EMAIL")).thenReturn(Optional.of(parameter));

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        boolean result = emailService.sendWelcomeEmail(testEmail, testUserName);

        assertTrue(result);
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve processar template com todos os placeholders corretamente")
    void deveProcessarTemplateComTodosPlaceholdersCorretamente() {
        String template = "Olá {{nome}}, bem-vindo! Ano: {{ano}}, Enviado por: {{senderName}}";
        Parameters parameter = new Parameters();
        parameter.setValue(template);

        when(parametersRepository.findByKey("WELCOME_EMAIL")).thenReturn(Optional.of(parameter));

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        boolean result = emailService.sendWelcomeEmail(testEmail, testUserName);

        assertTrue(result);
        verify(parametersRepository).findByKey("WELCOME_EMAIL");
        verify(mailSender).send(any(MimeMessage.class));

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());
    }

    @Test
    @DisplayName("Deve lidar com template vazio graciosamente")
    void deveLidarComTemplateVazioGraciosamente() {
        Parameters parameter = new Parameters();

        when(parametersRepository.findByKey("WELCOME_EMAIL")).thenReturn(Optional.of(parameter));

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        boolean result = emailService.sendWelcomeEmail(testEmail, testUserName);

        assertTrue(result);
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve lidar com exceção ao enviar email com anexos")
    void deveLidarComExcecaoAoEnviarEmailComAnexos() {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Erro ao enviar email")).when(mailSender).send(any(MimeMessage.class));
        byte[] attachment = "Conteúdo do anexo".getBytes();
        List<byte[]> attachments = List.of(attachment);
        List<String> attachmentNames = List.of("anexo.txt");
        boolean result = emailService.sendEmailWithAttachments(emailRequestDTO, attachments, attachmentNames);
        assertFalse(result);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve retornar false ao enviar email com anexos quando emailRequestDTO for nulo")
    void deveRetornarFalseAoEnviarEmailComAnexosQuandoEmailRequestDTOForNulo() {
        byte[] attachment = "Conteúdo do anexo".getBytes();
        List<byte[]> attachments = List.of(attachment);
        List<String> attachmentNames = List.of("anexo.txt");
        boolean result = emailService.sendEmailWithAttachments(null, attachments, attachmentNames);
        assertFalse(result);
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve retornar false ao enviar email com anexos quando ocorrer exceção")
    void deveRetornarFalseAoEnviarEmailComAnexosQuandoOcorrerExcecao() {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Erro ao enviar email")).when(mailSender).send(any(MimeMessage.class));
        boolean result = emailService.sendEmailWithAttachments(emailRequestDTO, null, null);
        assertFalse(result);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve processar anexos corretamente no envio de solicitação de cadastro de intérprete")
    void deveProcessarAnexosCorretamenteNoEnvioSolicitacaoCadastroInterprete() throws Exception {
        // Mock dos arquivos
        MultipartFile file1 = org.mockito.Mockito.mock(MultipartFile.class);
        MultipartFile file2 = org.mockito.Mockito.mock(MultipartFile.class);
        List<MultipartFile> files = List.of(file1, file2);

        lenient().when(file1.getOriginalFilename()).thenReturn("doc1.pdf");
        lenient().when(file1.getBytes()).thenReturn("conteudo1".getBytes());
        lenient().when(file2.getOriginalFilename()).thenReturn("doc2.pdf");
        lenient().when(file2.getBytes()).thenReturn("conteudo2".getBytes());

        // Template armazenado no banco
        String template = "<p>Nome: {{nome}}</p><a href=\"{link_api}\">Aceitar</a>";
        Parameters parameter = new Parameters();
        parameter.setKey("PENDING_INTERPRETER_ADMIN");
        parameter.setValue(template);

        when(parametersRepository.findByKey("PENDING_INTERPRETER_ADMIN"))
                .thenReturn(Optional.of(parameter));

        // Mock do envio do email
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        InterpreterRegistrationEmailDTO dto = InterpreterRegistrationEmailDTO.builder()
                .adminEmail("admin@pointtils.com")
                .interpreterName("João")
                .cpf("123")
                .cnpj("456")
                .email("email")
                .phone("phone")
                .acceptLink("http://accept")
                .rejectLink("http://reject")
                .files(files)
                .build();


        // Execução do método
        boolean result = emailService.sendInterpreterRegistrationRequestEmail(dto);

        // Verificações
        assertTrue(result);
        verify(parametersRepository).findByKey("PENDING_INTERPRETER_ADMIN");
        verify(mailSender).send(any(MimeMessage.class));
    }


    @Test
    void deveProcessarTemplateAdminFeedbackCorretamente() {
        String template = "Mensagem: {{message}}, Ano: {{ano}}";
        Parameters parameter = new Parameters();
        parameter.setKey("ADMIN_FEEDBACK");
        parameter.setValue(template);

        when(parametersRepository.findByKey("ADMIN_FEEDBACK")).thenReturn(Optional.of(parameter));

        String emailResponse = "Cadastro aprovado";

        String result = emailService.getAdminRegistrationFeedbackHtml(emailResponse);

        String expected = "Mensagem: Cadastro aprovado, Ano: " + Year.now().getValue();
        assertEquals(expected, result);
    }

    @Test
    void deveUsarTemplatePadraoSeNaoEncontrarNoBanco() {

        when(parametersRepository.findByKey("ADMIN_FEEDBACK")).thenReturn(Optional.empty());

        String emailResponse = "Cadastro aprovado";

        String result = emailService.getAdminRegistrationFeedbackHtml(emailResponse);

        String expectedStart = "<html><body><h1>Template não encontrado</h1>";
        assertTrue(result.startsWith(expectedStart));
    }
}