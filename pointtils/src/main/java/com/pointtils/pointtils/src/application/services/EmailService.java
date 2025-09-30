package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.requests.EmailRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

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
            log.error("Erro ao enviar email para {}: {}", emailRequest.getTo(), e.getMessage());
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
        String html = createWelcomeTemplate(userName);
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
        String html = createPasswordResetTemplate(userName, resetToken);
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
        String html = createAppointmentConfirmationTemplate(userName, appointmentDate, interpreterName);
        EmailRequestDTO emailRequest = new EmailRequestDTO(
            email,
            "Confirmação de Agendamento - PointTils",
            html,
            senderName
        );
        return sendHtmlEmail(emailRequest);
    }

    // ==================== Templates HTML ====================

    private String createWelcomeTemplate(String userName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 40px 20px; text-align: center;">
                                        <h1 style="color: #ffffff; margin: 0; font-size: 28px;">🎉 Bem-vindo(a)!</h1>
                                    </td>
                                </tr>
                                
                                <!-- Content -->
                                <tr>
                                    <td style="padding: 40px 30px;">
                                        <h2 style="color: #333333; margin-top: 0;">Olá, %s!</h2>
                                        <p style="color: #666666; line-height: 1.6; font-size: 16px;">
                                            Estamos muito felizes em tê-lo(a) conosco! Sua conta foi criada com sucesso.
                                        </p>
                                        <p style="color: #666666; line-height: 1.6; font-size: 16px;">
                                            Agora você pode aproveitar todos os recursos da nossa plataforma.
                                        </p>
                                        
                                        <!-- Button -->
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="margin: 30px 0;">
                                            <tr>
                                                <td align="center">
                                                    <a href="https://pointtils.com/login" 
                                                       style="display: inline-block; background-color: #667eea; color: #ffffff; 
                                                              padding: 14px 40px; text-decoration: none; border-radius: 5px; 
                                                              font-weight: bold; font-size: 16px;">
                                                        Acessar Plataforma
                                                    </a>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <p style="color: #999999; font-size: 14px; line-height: 1.6; margin-top: 30px;">
                                            Caso tenha alguma dúvida, estamos à disposição para ajudar!
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f8f9fa; padding: 20px 30px; text-align: center; border-top: 1px solid #e0e0e0;">
                                        <p style="color: #999999; font-size: 12px; margin: 0;">
                                            Este é um email automático, por favor não responda.<br>
                                            © 2025 %s. Todos os direitos reservados.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """, userName, senderName);
    }

    private String createPasswordResetTemplate(String userName, String resetToken) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                <tr>
                                    <td style="padding: 40px 30px;">
                                        <h1 style="color: #333333; margin-top: 0;">🔑 Recuperação de Senha</h1>
                                        <p style="color: #666666; font-size: 16px; line-height: 1.6;">
                                            Olá, %s! Recebemos uma solicitação para redefinir a senha da sua conta.
                                        </p>
                                        <p style="color: #666666; font-size: 16px; line-height: 1.6;">
                                            Use o código abaixo para redefinir sua senha:
                                        </p>
                                        
                                        <!-- Code Box -->
                                        <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                                                    margin: 30px 0; padding: 20px; border-radius: 8px;">
                                            <p style="color: #ffffff; font-size: 36px; font-weight: bold; 
                                                      letter-spacing: 8px; margin: 0; font-family: 'Courier New', monospace;">
                                                %s
                                            </p>
                                        </div>
                                        
                                        <p style="color: #999999; font-size: 14px; line-height: 1.6;">
                                            ⏱️ Este código expira em <strong>1 hora</strong>
                                        </p>
                                        
                                        <div style="margin-top: 30px; padding: 15px; background-color: #fff3cd; border-left: 4px solid #ffc107; border-radius: 4px;">
                                            <p style="color: #856404; font-size: 14px; margin: 0; line-height: 1.6;">
                                                <strong>⚠️ Importante:</strong> Se você não solicitou a recuperação de senha, 
                                                ignore este email. Sua senha permanecerá inalterada.
                                            </p>
                                        </div>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f8f9fa; padding: 20px 30px; text-align: center; border-top: 1px solid #e0e0e0;">
                                        <p style="color: #999999; font-size: 12px; margin: 0;">
                                            © 2025 %s. Todos os direitos reservados.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """, userName, resetToken, senderName);
    }

    private String createAppointmentConfirmationTemplate(String userName, String appointmentDate, String interpreterName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 40px 20px; text-align: center;">
                                        <h1 style="color: #ffffff; margin: 0; font-size: 28px;">✅ Agendamento Confirmado</h1>
                                    </td>
                                </tr>
                                
                                <!-- Content -->
                                <tr>
                                    <td style="padding: 40px 30px;">
                                        <h2 style="color: #333333; margin-top: 0;">Olá, %s!</h2>
                                        <p style="color: #666666; line-height: 1.6; font-size: 16px;">
                                            Seu agendamento foi confirmado com sucesso!
                                        </p>
                                        
                                        <!-- Appointment Details -->
                                        <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0;">
                                            <h3 style="color: #333333; margin-top: 0;">📅 Detalhes do Agendamento</h3>
                                            <p style="color: #666666; margin: 10px 0;">
                                                <strong>Data e Hora:</strong> %s
                                            </p>
                                            <p style="color: #666666; margin: 10px 0;">
                                                <strong>Intérprete:</strong> %s
                                            </p>
                                        </div>
                                        
                                        <p style="color: #666666; line-height: 1.6; font-size: 16px;">
                                            Lembre-se de estar disponível no horário agendado para sua sessão.
                                        </p>
                                        
                                        <div style="margin-top: 30px; padding: 15px; background-color: #d4edda; border-left: 4px solid #28a745; border-radius: 4px;">
                                            <p style="color: #155724; font-size: 14px; margin: 0; line-height: 1.6;">
                                                <strong>💡 Dica:</strong> Recomendamos que você esteja em um ambiente tranquilo 
                                                para melhor aproveitamento da sessão.
                                            </p>
                                        </div>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f8f9fa; padding: 20px 30px; text-align: center; border-top: 1px solid #e0e0e0;">
                                        <p style="color: #999999; font-size: 12px; margin: 0;">
                                            © 2025 %s. Todos os direitos reservados.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """, userName, appointmentDate, interpreterName, senderName);
    }
}
