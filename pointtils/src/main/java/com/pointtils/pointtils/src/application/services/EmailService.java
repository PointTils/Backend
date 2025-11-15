package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.email.AppointmentUpdateEmailDTO;
import com.pointtils.pointtils.src.application.dto.email.ProcessAdminTemplateDTO;
import com.pointtils.pointtils.src.application.dto.requests.EmailRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterRegistrationEmailDTO;
import com.pointtils.pointtils.src.core.domain.entities.Parameters;
import com.pointtils.pointtils.src.infrastructure.repositories.ParametersRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String EMAIL_REQUEST_NULL = "EmailRequestDTO não pode ser nulo";
    private static final String PLACEHOLDER_NOME = "{{nome}}";
    private static final String PLACEHOLDER_CPF = "{{cpf}}";
    private static final String PLACEHOLDER_CNPJ = "{{cnpj}}";
    private static final String PLACEHOLDER_EMAIL = "{{email}}";
    private static final String PLACEHOLDER_TELEFONE = "{{telefone}}";
    private static final String PLACEHOLDER_VIDEO = "{{video}}";
    private static final String PLACEHOLDER_ANO = "{{ano}}";
    private static final String PLACEHOLDER_ACCEPT = "{{link_accept_api}}";
    private static final String PLACEHOLDER_REJECT = "{{link_reject_api}}";
    private static final String PLACEHOLDER_SEND_NAME = "{{senderName}}";
    private static final String PLACEHOLDER_RESPOSTA_SOLICITACAO = "{{respostaSolicitacao}}";

    private final JavaMailSender mailSender;
    private final ParametersRepository parametersRepository;

    @Value("${app.mail.from:noreply@pointtils.com}")
    private String emailFrom;

    @Value("${app.mail.name:PointTils}")
    private String senderName;

    /**
     * Envia email simples de texto
     *
     * @param emailRequest DTO com informações do email
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendSimpleEmail(EmailRequestDTO emailRequest) {
        if (emailRequest == null) {
            log.error(EMAIL_REQUEST_NULL);
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(emailRequest.getTo());
            message.setSubject(emailRequest.getSubject());
            message.setText(emailRequest.getBody());

            mailSender.send(message);
            log.info("Email enviado com sucesso para: {}", emailRequest.getTo());
            return true;

        } catch (Exception e) {
            log.error("Erro ao enviar email para {}: {}", emailRequest.getTo(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Envia email HTML
     *
     * @param emailRequest DTO com informações do email
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendHtmlEmail(EmailRequestDTO emailRequest) {
        if (emailRequest == null) {
            log.error(EMAIL_REQUEST_NULL);
            return false;
        }

        try {
            sendEmailMessage(emailRequest, null, null);
            log.info("Email HTML enviado com sucesso para: {}", emailRequest.getTo());
            return true;
        } catch (Exception e) {
            log.error("Erro ao enviar email HTML para {}: {}", emailRequest.getTo(), e.getMessage());
            return false;
        }
    }

    public boolean sendEmailWithAttachments(EmailRequestDTO emailRequest, List<byte[]> attachments, List<String> attachmentNames) {
        if (emailRequest == null) {
            log.error(EMAIL_REQUEST_NULL);
            return false;
        }

        try {
            sendEmailMessage(emailRequest, attachments, attachmentNames);
            log.info("Email com anexos enviado com sucesso para: {}", emailRequest.getTo());
            return true;
        } catch (Exception e) {
            log.error("Erro ao enviar email com anexos para {}: {}", emailRequest.getTo(), e.getMessage());
            return false;
        }
    }

    /**
     * Envia email de boas-vindas
     *
     * @param email    Email do destinatário
     * @param userName Nome do usuário
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendWelcomeEmail(String email, String userName) {
        String template = getTemplateByKey("WELCOME_EMAIL");
        String html = processWelcomeTemplate(template, userName);
        EmailRequestDTO emailRequest = new EmailRequestDTO(
                email,
                "Bem-vindo(a) ao PointTils!",
                html,
                senderName);
        return sendHtmlEmail(emailRequest);
    }

    /**
     * Envia email de recuperação de senha
     *
     * @param email      Email do destinatário
     * @param userName   Nome do usuário
     * @param resetToken Token de recuperação
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendPasswordResetEmail(String email, String userName, String resetToken) {
        String template = getTemplateByKey("PASSWORD_RESET");
        String html = processPasswordResetTemplate(template, userName, resetToken);
        EmailRequestDTO emailRequest = new EmailRequestDTO(
                email,
                "Recuperação de Senha - PointTils",
                html,
                senderName);
        return sendHtmlEmail(emailRequest);
    }

    /**
     * Envia email de confirmação de agendamento
     *
     * @param email           Email do destinatário
     * @param userName        Nome do usuário
     * @param appointmentDate Data do agendamento
     * @param interpreterName Nome do intérprete
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendAppointmentConfirmationEmail(String email, String userName, String appointmentDate,
                                                    String interpreterName) {
        String template = getTemplateByKey("APPOINTMENT_CONFIRMATION");
        String html = processAppointmentConfirmationTemplate(template, userName, appointmentDate, interpreterName);
        EmailRequestDTO emailRequest = new EmailRequestDTO(
                email,
                "Confirmação de Agendamento - PointTils",
                html,
                senderName);
        return sendHtmlEmail(emailRequest);
    }

    /**
     * Envia email de solicitação de cadastro de intérprete para administradores
     *
     * @param dto Dados a serem enviados por e-mail
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendInterpreterRegistrationRequestEmail(InterpreterRegistrationEmailDTO dto) {
        // Buscar template do banco de dados
        String template = getTemplateByKey("PENDING_INTERPRETER_ADMIN");

        List<byte[]> attachmentList = new ArrayList<>();
        List<String> attachmentNames = new ArrayList<>();

        for (MultipartFile file : dto.getFiles()) {
            try {
                byte[] documentBytes = file.getBytes();
                attachmentNames.add(file.getOriginalFilename());
                attachmentList.add(documentBytes);
            } catch (Exception e) {
                log.error("Erro ao baixar documento '{}' do intérprete '{}': {}", file.getName(), dto.getInterpreterName(),
                        e.getMessage());
            }
        }

        String html = processTemplate(
                ProcessAdminTemplateDTO.builder()
                        .template(template)
                        .interpreterName(dto.getInterpreterName())
                        .cpf(dto.getCpf())
                        .cnpj(dto.getCnpj())
                        .email(dto.getEmail())
                        .phone(dto.getPhone())
                        .videoUrl(dto.getVideoUrl())
                        .acceptLink(dto.getAcceptLink())
                        .rejectLink(dto.getRejectLink())
                        .build()
        );

        EmailRequestDTO emailRequest = new EmailRequestDTO(
                dto.getAdminEmail(),
                "Nova Solicitação de Cadastro de Intérprete - PointTils",
                html,
                senderName);
        return sendEmailWithAttachments(emailRequest, attachmentList, attachmentNames);
    }

    /**
     * Busca template HTML do banco de dados
     *
     * @param key Chave do template
     * @return Template HTML ou template padrão se não encontrado
     */
    public String getTemplateByKey(String key) {
        Optional<Parameters> parameter = parametersRepository.findByKey(key);
        if (parameter.isPresent()) {
            return parameter.get().getValue();
        }
        log.warn("Template com chave '{}' não encontrado no banco de dados", key);
        return getDefaultTemplate(key);
    }

    /**
     * Constrói HTML a ser acessado pelo administrador ao clicar na aprovacao ou
     * rejeicao do cadastro
     *
     * @param emailResponse mensagem de retorno a ser enviada para o admin
     * @return string com o HTML construído
     */
    public String getAdminRegistrationFeedbackHtml(String emailResponse) {
        String emailTemplate = getTemplateByKey("ADMIN_FEEDBACK");
        return emailTemplate
                .replace("{{message}}", emailResponse)
                .replace(PLACEHOLDER_ANO, String.valueOf(Year.now().getValue()));
    }

    /**
     * Envia email de feedback sobre cadastro para o intérprete
     *
     * @param interpreterEmail Email do intérprete
     * @param interpreterName  Nome do intérprete
     * @param approved         true se o cadastro foi aprovado, false se foi negado
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendInterpreterFeedbackEmail(String interpreterEmail, String interpreterName, boolean approved) {
        String html = createInterpreterFeedbackTemplate(interpreterName, approved);
        String subject = approved ? "Cadastro Aprovado - PointTils" : "Cadastro Não Aprovado - PointTils";

        EmailRequestDTO emailRequest = new EmailRequestDTO(
                interpreterEmail,
                subject,
                html,
                senderName);
        return sendHtmlEmail(emailRequest);
    }

    /**
     * Retorna template padrão baseado na chave
     *
     * @param key Chave do template
     * @return Template padrão
     */
    private String getDefaultTemplate(String key) {
        log.error(
                "Template com chave '{}' não encontrado no banco de dados. Verifique se a migration V12 foi executada.",
                key);
        return "<html><body><h1>Template não encontrado</h1><p>Template com chave '" + key
                + "' não está disponível no banco de dados.</p></body></html>";
    }

    /**
     * Processa template de boas-vindas
     *
     * @param template Template do banco
     * @param userName Nome do usuário
     * @return Template processado
     */
    private String processWelcomeTemplate(String template, String userName) {
        if (template == null) {
            return getDefaultTemplate("WELCOME_EMAIL");
        }

        return template
                .replace(PLACEHOLDER_NOME, userName != null ? userName : "")
                .replace(PLACEHOLDER_ANO, String.valueOf(Year.now().getValue()))
                .replace(PLACEHOLDER_SEND_NAME, senderName);
    }

    /**
     * Processa template de recuperação de senha
     *
     * @param template   Template do banco
     * @param userName   Nome do usuário
     * @param resetToken Token de recuperação
     * @return Template processado
     */
    private String processPasswordResetTemplate(String template, String userName, String resetToken) {
        if (template == null) {
            return getDefaultTemplate("PASSWORD_RESET");
        }

        return template
                .replace(PLACEHOLDER_NOME, userName != null ? userName : "")
                .replace("{{resetToken}}", resetToken != null ? resetToken : "")
                .replace(PLACEHOLDER_ANO, String.valueOf(Year.now().getValue()))
                .replace(PLACEHOLDER_SEND_NAME, senderName);
    }

    /**
     * Processa template de confirmação de agendamento
     *
     * @param template        Template do banco
     * @param userName        Nome do usuário
     * @param appointmentDate Data do agendamento
     * @param interpreterName Nome do intérprete
     * @return Template processado
     */
    private String processAppointmentConfirmationTemplate(String template, String userName, String appointmentDate,
                                                          String interpreterName) {
        if (template == null) {
            return getDefaultTemplate("APPOINTMENT_CONFIRMATION");
        }

        return template
                .replace(PLACEHOLDER_NOME, userName != null ? userName : "")
                .replace("{{appointmentDate}}", appointmentDate != null ? appointmentDate : "")
                .replace("{{interpreterName}}", interpreterName != null ? interpreterName : "")
                .replace(PLACEHOLDER_ANO, String.valueOf(Year.now().getValue()))
                .replace(PLACEHOLDER_SEND_NAME, senderName);
    }

    /**
     * Processa o template HTML substituindo os placeholders pelos valores reais
     *
     * @param dto Dados a serem preenchidos no template HTML
     * @return Template HTML processado
     */
    private String processTemplate(ProcessAdminTemplateDTO dto) {
        if (dto.getTemplate() == null) {
            return getDefaultTemplate("PENDING_INTERPRETER_ADMIN");
            // Substituir placeholders do template do banco
        }

        String videoUrl = StringUtils.isNotBlank(dto.getVideoUrl())
                ? "<a href=\"" + dto.getVideoUrl() + "\" target=\"_blank\" rel=\"noreferrer noopener\">Assistir</a>"
                : "";

        return dto.getTemplate()
                .replace(PLACEHOLDER_NOME, dto.getInterpreterName() != null ? dto.getInterpreterName() : "")
                .replace(PLACEHOLDER_CPF, dto.getCpf() != null ? dto.getCpf() : "")
                .replace(PLACEHOLDER_CNPJ, dto.getCnpj() != null ? dto.getCnpj() : "")
                .replace(PLACEHOLDER_EMAIL, dto.getEmail() != null ? dto.getEmail() : "")
                .replace(PLACEHOLDER_TELEFONE, dto.getPhone() != null ? dto.getPhone() : "")
                .replace(PLACEHOLDER_ACCEPT, dto.getAcceptLink() != null ? dto.getAcceptLink() : "")
                .replace(PLACEHOLDER_REJECT, dto.getRejectLink() != null ? dto.getRejectLink() : "")
                .replace(PLACEHOLDER_ANO, String.valueOf(Year.now().getValue()))
                .replace(PLACEHOLDER_VIDEO, videoUrl);
    }


    /**
     * Cria template de feedback para o intérprete
     *
     * @param interpreterName Nome do intérprete
     * @param approved        true se o cadastro foi aprovado, false se foi negado
     * @return Template HTML processado
     */
    private String createInterpreterFeedbackTemplate(String interpreterName, boolean approved) {
        // Buscar template do banco de dados
        String template = getTemplateByKey("PENDING_INTERPRETER");

        // Definir a mensagem de resposta baseada na aprovação
        String respostaSolicitacao = approved ? "seu cadastro foi aprovado e você já pode acessar a plataforma"
                : "seu cadastro foi recusado por perfil não compatível";

        // Processar o template
        return template
                .replace(PLACEHOLDER_NOME, interpreterName != null ? interpreterName : "")
                .replace(PLACEHOLDER_RESPOSTA_SOLICITACAO, respostaSolicitacao)
                .replace(PLACEHOLDER_ANO, String.valueOf(Year.now().getValue()));
    }

    private void sendEmailMessage(EmailRequestDTO emailRequest,
                                  List<byte[]> attachments,
                                  List<String> attachmentNames) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(String.format("%s <%s>", senderName, emailFrom));
        helper.setTo(emailRequest.getTo());
        helper.setSubject(emailRequest.getSubject());
        helper.setText(emailRequest.getBody(), true);

        // Adiciona os anexos
        if (Objects.nonNull(attachments)) {
            for (int i = 0; i < attachments.size(); i++) {
                byte[] attachment = attachments.get(i);
                helper.addAttachment(attachmentNames.get(i), new ByteArrayResource(attachment));
            }
        }

        mailSender.send(message);
    }

    /**
     * Envia email de agendamento aceito
     *
     * @param dto DTO com parametros utilizados para envio de email
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendAppointmentAcceptedEmail(AppointmentUpdateEmailDTO dto) {
        dto.setTemplate(getTemplateByKey("APPOINTMENT_ACCEPTED"));
        String html = processAppointmentStatusChangeTemplate(dto);
        EmailRequestDTO emailRequest = new EmailRequestDTO(
                dto.getEmail(),
                "Agendamento Aceito - PointTils",
                html,
                senderName);
        return sendHtmlEmail(emailRequest);
    }

    /**
     * Envia email de agendamento negado
     *
     * @param dto DTO com parametros utilizados para envio de email
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendAppointmentDeniedEmail(AppointmentUpdateEmailDTO dto) {
        dto.setTemplate(getTemplateByKey("APPOINTMENT_DENIED"));
        String html = processAppointmentStatusChangeTemplate(dto);
        EmailRequestDTO emailRequest = new EmailRequestDTO(
                dto.getEmail(),
                "Agendamento Negado - PointTils",
                html,
                senderName);
        return sendHtmlEmail(emailRequest);
    }

    /**
     * Envia email de agendamento cancelado
     *
     * @param dto DTO com parametros utilizados para envio de email
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendAppointmentCanceledEmail(AppointmentUpdateEmailDTO dto) {
        dto.setTemplate(getTemplateByKey("APPOINTMENT_CANCELED"));
        String html = processAppointmentStatusChangeTemplate(dto);
        EmailRequestDTO emailRequest = new EmailRequestDTO(
                dto.getEmail(),
                "Agendamento Cancelado - PointTils",
                html,
                senderName);
        return sendHtmlEmail(emailRequest);
    }

    /**
     * Processa template de mudança de status de agendamento
     *
     * @param dto DTO com parametros utilizados para envio de email
     * @return Template processado
     */
    private String processAppointmentStatusChangeTemplate(AppointmentUpdateEmailDTO dto) {
        if (dto.getTemplate() == null) {
            return getDefaultTemplate("APPOINTMENT_STATUS_CHANGE");
        }

        return dto.getTemplate()
                .replace(PLACEHOLDER_NOME, StringUtils.getIfEmpty(dto.getUserName(), () -> ""))
                .replace("{{appointmentDate}}", StringUtils.getIfEmpty(dto.getAppointmentDate(), () -> ""))
                .replace("{{appointmentDescription}}", StringUtils.getIfEmpty(dto.getAppointmentDescription(), () -> ""))
                .replace("{{appointmentModality}}", StringUtils.getIfEmpty(dto.getAppointmentModality(), () -> ""))
                .replace("{{appointmentLocation}}", StringUtils.getIfEmpty(dto.getAppointmentLocation(), () -> ""))
                .replace("{{subject}}", StringUtils.getIfEmpty(dto.getSubject(), () -> ""))
                .replace("{{subjectName}}", StringUtils.getIfEmpty(dto.getSubjectName(), () -> ""))
                .replace(PLACEHOLDER_ANO, String.valueOf(Year.now().getValue()))
                .replace(PLACEHOLDER_SEND_NAME, senderName);
    }
}

