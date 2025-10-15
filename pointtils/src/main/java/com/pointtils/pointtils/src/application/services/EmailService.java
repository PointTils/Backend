package com.pointtils.pointtils.src.application.services;

import java.time.Year;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.pointtils.pointtils.src.application.dto.requests.EmailRequestDTO;
import com.pointtils.pointtils.src.core.domain.entities.Parameters;
import com.pointtils.pointtils.src.infrastructure.repositories.ParametersRepository;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final ParametersRepository parametersRepository;

    @Value("${app.mail.from:noreply@pointtils.com}")
    private String emailFrom;

    @Value("${app.mail.name:PointTils}")
    private String senderName;

    /**
     * Envia email simples de texto
     * @param emailRequest DTO com informações do email
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendSimpleEmail(EmailRequestDTO emailRequest) {
        if (emailRequest == null) {
            log.error("EmailRequestDTO não pode ser nulo");
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
     * @param emailRequest DTO com informações do email
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendHtmlEmail(EmailRequestDTO emailRequest) {
        if (emailRequest == null) {
            log.error("EmailRequestDTO não pode ser nulo");
            return false;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(String.format("%s <%s>", senderName, emailFrom));
            helper.setTo(emailRequest.getTo());
            helper.setSubject(emailRequest.getSubject());
            helper.setText(emailRequest.getBody(), true);
            
            mailSender.send(message);
            log.info("Email HTML enviado com sucesso para: {}", emailRequest.getTo());
            return true;
            
        } catch (Exception e) {
            log.error("Erro ao enviar email HTML para {}: {}", emailRequest.getTo(), e.getMessage());
            return false;
        }
    }

    /**
     * Envia email de boas-vindas
     * @param email Email do destinatário
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
            senderName
        );
        return sendHtmlEmail(emailRequest);
    }

    /**
     * Envia email de recuperação de senha
     * @param email Email do destinatário
     * @param userName Nome do usuário
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
            senderName
        );
        return sendHtmlEmail(emailRequest);
    }

    /**
     * Envia email de confirmação de agendamento
     * @param email Email do destinatário
     * @param userName Nome do usuário
     * @param appointmentDate Data do agendamento
     * @param interpreterName Nome do intérprete
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendAppointmentConfirmationEmail(String email, String userName, String appointmentDate, String interpreterName) {
        String template = getTemplateByKey("APPOINTMENT_CONFIRMATION");
        String html = processAppointmentConfirmationTemplate(template, userName, appointmentDate, interpreterName);
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            email,
            "Confirmação de Agendamento - PointTils",
            html,
            senderName
        );
        return sendHtmlEmail(emailRequest);
    }

    /**
     * Envia email de solicitação de cadastro de intérprete para administradores
     * @param adminEmail Email do administrador
     * @param interpreterName Nome do intérprete
     * @param cpf CPF do intérprete
     * @param cnpj CNPJ do intérprete
     * @param email Email do intérprete
     * @param phone Telefone do intérprete
     * @param acceptLink Link para aceitar o cadastro
     * @param rejectLink Link para recusar o cadastro
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendInterpreterRegistrationRequestEmail(String adminEmail, String interpreterName, String cpf, String cnpj, String email, String phone, String acceptLink, String rejectLink) {
        // Buscar template do banco de dados
        String template = getTemplateByKey("PENDING_INTERPRETER_ADMIN");
        
        // Substituir placeholders do template
        String html = processTemplate(template, interpreterName, cpf, cnpj, email, phone, acceptLink, rejectLink);
        
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            adminEmail,
            "Nova Solicitação de Cadastro de Intérprete - PointTils",
            html,
            senderName
        );
        return sendHtmlEmail(emailRequest);
    }

    /**
     * Busca template HTML do banco de dados
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
     * Retorna template padrão baseado na chave
     * @param key Chave do template
     * @return Template padrão
     */
    private String getDefaultTemplate(String key) {
        log.error("Template com chave '{}' não encontrado no banco de dados. Verifique se a migration V12 foi executada.", key);
        return "<html><body><h1>Template não encontrado</h1><p>Template com chave '" + key + "' não está disponível no banco de dados.</p></body></html>";
    }

    /**
     * Processa template de boas-vindas
     * @param template Template do banco
     * @param userName Nome do usuário
     * @return Template processado
     */
    private String processWelcomeTemplate(String template, String userName) {
        if (template == null) {
            return getDefaultTemplate("WELCOME_EMAIL");
        }
        
        return template
                .replace("{{nome}}", userName != null ? userName : "")
                .replace("{{ano}}", String.valueOf(Year.now().getValue()))
                .replace("{{senderName}}", senderName);
    }

    /**
     * Processa template de recuperação de senha
     * @param template Template do banco
     * @param userName Nome do usuário
     * @param resetToken Token de recuperação
     * @return Template processado
     */
    private String processPasswordResetTemplate(String template, String userName, String resetToken) {
        if (template == null) {
            return getDefaultTemplate("PASSWORD_RESET");
        }
        
        return template
                .replace("{{nome}}", userName != null ? userName : "")
                .replace("{{resetToken}}", resetToken != null ? resetToken : "")
                .replace("{{ano}}", String.valueOf(Year.now().getValue()))
                .replace("{{senderName}}", senderName);
    }

    /**
     * Processa template de confirmação de agendamento
     * @param template Template do banco
     * @param userName Nome do usuário
     * @param appointmentDate Data do agendamento
     * @param interpreterName Nome do intérprete
     * @return Template processado
     */
    private String processAppointmentConfirmationTemplate(String template, String userName, String appointmentDate, String interpreterName) {
        if (template == null) {
            return getDefaultTemplate("APPOINTMENT_CONFIRMATION");
        }
        
        return template
                .replace("{{nome}}", userName != null ? userName : "")
                .replace("{{appointmentDate}}", appointmentDate != null ? appointmentDate : "")
                .replace("{{interpreterName}}", interpreterName != null ? interpreterName : "")
                .replace("{{ano}}", String.valueOf(Year.now().getValue()))
                .replace("{{senderName}}", senderName);
    }

   
    /**
     * Envia email de feedback sobre cadastro para o intérprete
     * @param interpreterEmail Email do intérprete
     * @param interpreterName Nome do intérprete
     * @param approved true se o cadastro foi aprovado, false se foi negado
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    public boolean sendInterpreterFeedbackEmail(String interpreterEmail, String interpreterName, boolean approved) {
        String html = createInterpreterFeedbackTemplate(interpreterName, approved);
        String subject = approved ? 
            "Cadastro Aprovado - PointTils" : 
            "Cadastro Não Aprovado - PointTils";
        
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            interpreterEmail,
            subject,
            html,
            senderName
        );
        return sendHtmlEmail(emailRequest);
    }

    /**
     * Processa o template HTML substituindo os placeholders pelos valores reais
     * @param template Template HTML do banco de dados
     * @param interpreterName Nome do intérprete
     * @param cpf CPF do intérprete
     * @param cnpj CNPJ do intérprete
     * @param email Email do intérprete
     * @param phone Telefone do intérprete
     * @param acceptLink Link para aceitar o cadastro
     * @param rejectLink Link para recusar o cadastro
     * @return Template HTML processado
     */
    private String processTemplate(String template, String interpreterName, String cpf, String cnpj, 
                                  String email, String phone, String acceptLink, String rejectLink) {
        if (template == null) {
            return getDefaultTemplate("PENDING_INTERPRETER_ADMIN");
        }
        
        // Substituir placeholders do template do banco
        String processed = template
                .replace("{{nome}}", interpreterName != null ? interpreterName : "")
                .replace("{{cpf}}", cpf != null ? cpf : "")
                .replace("{{cnpj}}", cnpj != null ? cnpj : "")
                .replace("{{email}}", email != null ? email : "")
                .replace("{{telefone}}", phone != null ? phone : "")
                .replace("{{ano}}", String.valueOf(Year.now().getValue()));
        
        // Corrigir os links de aceitar/recusar (o template do banco usa o mesmo placeholder para ambos)
        // Vamos substituir manualmente os links nos botões
        processed = processed.replace(
            "<a href=\"{link_api}\" target=\"_blank\" rel=\"noreferrer noopener\" style=\"background-color: #008000; color: #fff; padding: 10px 20px; border-radius: 5px; text-decoration: none; font-weight: bold;\">Aceitar</a>",
            String.format("<a href=\"%s\" target=\"_blank\" rel=\"noreferrer noopener\" style=\"background-color: #008000; color: #fff; padding: 10px 20px; border-radius: 5px; text-decoration: none; font-weight: bold;\">Aceitar</a>", acceptLink)
        );
        
        processed = processed.replace(
            "<a href=\"{link_api}\" target=\"_blank\" rel=\"noreferrer noopener\" style=\"background-color: #FF0000; color: #fff; padding: 10px 20px; border-radius: 5px; text-decoration: none; font-weight: bold;\">Recusar</a>",
            String.format("<a href=\"%s\" target=\"_blank\" rel=\"noreferrer noopener\" style=\"background-color: #FF0000; color: #fff; padding: 10px 20px; border-radius: 5px; text-decoration: none; font-weight: bold;\">Recusar</a>", rejectLink)
        );
        
        return processed;
    }

    /**
     * Cria template de feedback para o intérprete
     * @param interpreterName Nome do intérprete
     * @param approved true se o cadastro foi aprovado, false se foi negado
     * @return Template HTML processado
     */
    private String createInterpreterFeedbackTemplate(String interpreterName, boolean approved) {
        // Buscar template do banco de dados
        String template = getTemplateByKey("PENDING_INTERPRETER");
        
        // Definir a mensagem de resposta baseada na aprovação
        String respostaSolicitacao = approved ? 
            "seu cadastro foi aprovado e você já pode acessar a plataforma" : 
            "infelizmente seu cadastro não foi aprovado no momento";
        
        // Processar o template
        String processed = template
                .replace("{{nome}}", interpreterName != null ? interpreterName : "")
                .replace("{{respostaSolicitacao}}", respostaSolicitacao)
                .replace("{{ano}}", String.valueOf(Year.now().getValue()));
        
        return processed;
    }
}
