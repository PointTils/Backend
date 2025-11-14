-- Migration: V24__Add_appointment_status_change_email_templates.sql
-- Descrição: Adiciona templates de email para mudanças de status de agendamento (aceito, negado, cancelado)

-- Template de Agendamento Aceito
INSERT INTO parameters (id, key, value) VALUES
(
    uuid_generate_v4(),
    'APPOINTMENT_ACCEPTED',
    '<style type="text/css"></style>
    <table width="100%" style="font-family: Helvetica, Arial, sans-serif; padding-top: 40px; margin:0; padding:0; -webkit-font-smoothing: antialiased" cellspacing="0" cellpadding="0" border="0" align="center">
        <tbody>
            <tr>
                <td>
                    <table width="90%" cellspacing="0" cellpadding="0" border="0" align="center" style="margin-top: 50px; margin-bottom: 100px;">
                        <tbody>
                            <tr>
                                <td width="800" bgcolor="#F3F3F3" style="padding-right: 15px; padding-left: 15px; padding-top: 15px; padding-bottom: 15px">
                                    <table width="800" cellspacing="0" cellpadding="0" border="0" align="center">
                                        <tbody>
                                            <tr>
                                                <td width="800" bgcolor="#ffffff" style="border: 1px solid #e7eeee; border-radius: 5px;">
                                                    <table width="800" cellspacing="0" cellpadding="0" border="0" align="center" style="margin-top: 40px;">
                                                        <table width="600" cellspacing="0" cellpadding="0" border="0" align="center" style="margin-top: 40px;">
                                                            <tbody>
                                                                <tr>
                                                                    <td width="800" style="padding-bottom: 40px; border-bottom: 1px solid #e7eeee;" colspan="2">
                                                                        <center>
                                                                            <img border="0" style="height: 100px;" alt="Logo Point Tils" src="https://i.ibb.co/GgXzx5s/pointtils.webp">
                                                                        </center>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td width="800" style="font-size: 12px; color: #65707a; text-align: left; padding-top:20px; padding-bottom: 20px" colspan="2">
                                                                        <table style="font-size: 12px; color: #65707a; text-align: left; padding-top:20px; padding-bottom: 20px" colspan="2">
                                                                            <div style="margin: 0 auto; padding: 20px;">
                                                                                <div class="content" style="margin: 1em;">
                                                                                    <p>Olá <span style="font-weight: bold">{{nome}}!</span></p>
                                                                                    <p>Ótimas notícias! Seu agendamento foi <span style="color: #28a745; font-weight: bold;">ACEITO</span>! 🎉</p>
                                                                                    <div style="background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); padding: 20px; border-radius: 8px; margin: 20px 0;">
                                                                                        <h3 style="color: #ffffff; margin-top: 0; text-align: center;">✅ Agendamento Confirmado</h3>
                                                                                    </div>
                                                                                    <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0;">
                                                                                        <h3 style="color: #333333; margin-top: 0;">📅 Detalhes do Agendamento</h3>
                                                                                        <p style="color: #666666; margin: 10px 0;">
                                                                                            <strong>Data e Hora:</strong> {{appointmentDate}}
                                                                                        </p>
                                                                                        <p style="color: #666666; margin: 10px 0;">
                                                                                            <strong>Intérprete:</strong> {{interpreterName}}
                                                                                        </p>
                                                                                        <p style="color: #666666; margin: 10px 0;">
                                                                                            <strong>Local:</strong> {{appointmentLocation}}
                                                                                        </p>
                                                                                        <p style="color: #666666; margin: 10px 0;">
                                                                                            <strong>Modalidade:</strong> {{appointmentModality}}
                                                                                        </p>
                                                                                    </div>
                                                                                    <div style="margin-top: 30px; padding: 15px; background-color: #d4edda; border-left: 4px solid #28a745; border-radius: 4px;">
                                                                                        <p style="color: #155724; font-size: 14px; margin: 0; line-height: 1.6;">
                                                                                            <strong>💡 Lembre-se:</strong> Esteja disponível no horário agendado. Recomendamos que você esteja em um ambiente tranquilo para melhor aproveitamento da sessão.
                                                                                        </p>
                                                                                    </div>
                                                                                    <p>Caso precise de alguma alteração ou tenha dúvidas, entre em contato conosco.</p>
                                                                                    <p>Atenciosamente,</p>
                                                                                    <p><strong>Equipe {{senderName}}</strong></p>
                                                                                </div>
                                                                            </div>
                                                                        </table>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td width="800" style="padding-top: 20px; padding-bottom: 20px; color:#4D4D4D" colspan="2">
                                                                        <center>
                                                                            <font style="font-size:10px">
                                                                                &copy; Point Tils {{ano}} - Todos os direitos reservados.
                                                                            </font>
                                                                        </center>
                                                                    </td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </table>
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
        </tbody>
    </table>'
);

-- Template de Agendamento Negado/Recusado
INSERT INTO parameters (id, key, value) VALUES
(
    uuid_generate_v4(),
    'APPOINTMENT_DENIED',
    '<style type="text/css"></style>
    <table width="100%" style="font-family: Helvetica, Arial, sans-serif; padding-top: 40px; margin:0; padding:0; -webkit-font-smoothing: antialiased" cellspacing="0" cellpadding="0" border="0" align="center">
        <tbody>
            <tr>
                <td>
                    <table width="90%" cellspacing="0" cellpadding="0" border="0" align="center" style="margin-top: 50px; margin-bottom: 100px;">
                        <tbody>
                            <tr>
                                <td width="800" bgcolor="#F3F3F3" style="padding-right: 15px; padding-left: 15px; padding-top: 15px; padding-bottom: 15px">
                                    <table width="800" cellspacing="0" cellpadding="0" border="0" align="center">
                                        <tbody>
                                            <tr>
                                                <td width="800" bgcolor="#ffffff" style="border: 1px solid #e7eeee; border-radius: 5px;">
                                                    <table width="800" cellspacing="0" cellpadding="0" border="0" align="center" style="margin-top: 40px;">
                                                        <table width="600" cellspacing="0" cellpadding="0" border="0" align="center" style="margin-top: 40px;">
                                                            <tbody>
                                                                <tr>
                                                                    <td width="800" style="padding-bottom: 40px; border-bottom: 1px solid #e7eeee;" colspan="2">
                                                                        <center>
                                                                            <img border="0" style="height: 100px;" alt="Logo Point Tils" src="https://i.ibb.co/GgXzx5s/pointtils.webp">
                                                                        </center>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td width="800" style="font-size: 12px; color: #65707a; text-align: left; padding-top:20px; padding-bottom: 20px" colspan="2">
                                                                        <table style="font-size: 12px; color: #65707a; text-align: left; padding-top:20px; padding-bottom: 20px" colspan="2">
                                                                            <div style="margin: 0 auto; padding: 20px;">
                                                                                <div class="content" style="margin: 1em;">
                                                                                    <p>Olá <span style="font-weight: bold">{{nome}}!</span></p>
                                                                                    <p>Informamos que seu agendamento foi <span style="color: #dc3545; font-weight: bold;">NEGADO</span>.</p>
                                                                                    <div style="background-color: #f8d7da; padding: 20px; border-left: 4px solid #dc3545; border-radius: 8px; margin: 20px 0;">
                                                                                        <h3 style="color: #721c24; margin-top: 0;">ℹ️ Informações do Agendamento</h3>
                                                                                        <p style="color: #721c24; margin: 10px 0;">
                                                                                            <strong>Data e Hora solicitada:</strong> {{appointmentDate}}
                                                                                        </p>
                                                                                        <p style="color: #721c24; margin: 10px 0;">
                                                                                            <strong>Intérprete:</strong> {{interpreterName}}
                                                                                        </p>
                                                                                    </div>
                                                                                    <p>Infelizmente, o intérprete não pôde aceitar seu agendamento. Isso pode ocorrer por diversos motivos, como conflito de horário ou indisponibilidade.</p>
                                                                                    <div style="margin-top: 30px; padding: 15px; background-color: #d1ecf1; border-left: 4px solid #0c5460; border-radius: 4px;">
                                                                                        <p style="color: #0c5460; font-size: 14px; margin: 0; line-height: 1.6;">
                                                                                            <strong>💡 Sugestão:</strong> Você pode tentar agendar com outro intérprete ou escolher um horário diferente. Nossa equipe está sempre disponível para ajudá-lo a encontrar a melhor opção.
                                                                                        </p>
                                                                                    </div>
                                                                                    <p style="text-align: center; margin-top: 30px;">
                                                                                        <a href="https://pointtils.com/appointments/new" target="_blank" rel="noreferrer noopener" style="background-color: #667eea; color: #fff; padding: 12px 30px; border-radius: 5px; text-decoration: none; font-weight: bold;">Fazer Novo Agendamento</a>
                                                                                    </p>
                                                                                    <p>Se tiver alguma dúvida ou precisar de ajuda, entre em contato conosco.</p>
                                                                                    <p>Atenciosamente,</p>
                                                                                    <p><strong>Equipe {{senderName}}</strong></p>
                                                                                </div>
                                                                            </div>
                                                                        </table>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td width="800" style="padding-top: 20px; padding-bottom: 20px; color:#4D4D4D" colspan="2">
                                                                        <center>
                                                                            <font style="font-size:10px">
                                                                                &copy; Point Tils {{ano}} - Todos os direitos reservados.
                                                                            </font>
                                                                        </center>
                                                                    </td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </table>
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
        </tbody>
    </table>'
);

-- Template de Agendamento Cancelado
INSERT INTO parameters (id, key, value) VALUES
(
    uuid_generate_v4(),
    'APPOINTMENT_CANCELED',
    '<style type="text/css"></style>
    <table width="100%" style="font-family: Helvetica, Arial, sans-serif; padding-top: 40px; margin:0; padding:0; -webkit-font-smoothing: antialiased" cellspacing="0" cellpadding="0" border="0" align="center">
        <tbody>
            <tr>
                <td>
                    <table width="90%" cellspacing="0" cellpadding="0" border="0" align="center" style="margin-top: 50px; margin-bottom: 100px;">
                        <tbody>
                            <tr>
                                <td width="800" bgcolor="#F3F3F3" style="padding-right: 15px; padding-left: 15px; padding-top: 15px; padding-bottom: 15px">
                                    <table width="800" cellspacing="0" cellpadding="0" border="0" align="center">
                                        <tbody>
                                            <tr>
                                                <td width="800" bgcolor="#ffffff" style="border: 1px solid #e7eeee; border-radius: 5px;">
                                                    <table width="800" cellspacing="0" cellpadding="0" border="0" align="center" style="margin-top: 40px;">
                                                        <table width="600" cellspacing="0" cellpadding="0" border="0" align="center" style="margin-top: 40px;">
                                                            <tbody>
                                                                <tr>
                                                                    <td width="800" style="padding-bottom: 40px; border-bottom: 1px solid #e7eeee;" colspan="2">
                                                                        <center>
                                                                            <img border="0" style="height: 100px;" alt="Logo Point Tils" src="https://i.ibb.co/GgXzx5s/pointtils.webp">
                                                                        </center>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td width="800" style="font-size: 12px; color: #65707a; text-align: left; padding-top:20px; padding-bottom: 20px" colspan="2">
                                                                        <table style="font-size: 12px; color: #65707a; text-align: left; padding-top:20px; padding-bottom: 20px" colspan="2">
                                                                            <div style="margin: 0 auto; padding: 20px;">
                                                                                <div class="content" style="margin: 1em;">
                                                                                    <p>Olá <span style="font-weight: bold">{{nome}}!</span></p>
                                                                                    <p>Informamos que o agendamento foi <span style="color: #ffc107; font-weight: bold;">CANCELADO</span>.</p>
                                                                                    <div style="background-color: #fff3cd; padding: 20px; border-left: 4px solid #ffc107; border-radius: 8px; margin: 20px 0;">
                                                                                        <h3 style="color: #856404; margin-top: 0;">ℹ️ Informações do Agendamento Cancelado</h3>
                                                                                        <p style="color: #856404; margin: 10px 0;">
                                                                                            <strong>Data e Hora:</strong> {{appointmentDate}}
                                                                                        </p>
                                                                                        <p style="color: #856404; margin: 10px 0;">
                                                                                            <strong>Intérprete:</strong> {{interpreterName}}
                                                                                        </p>
                                                                                        <p style="color: #856404; margin: 10px 0;">
                                                                                            <strong>Motivo:</strong> {{cancelReason}}
                                                                                        </p>
                                                                                    </div>
                                                                                    <p>Lamentamos o cancelamento. Se o cancelamento partiu do intérprete, pode ser devido a imprevistos ou conflitos de agenda.</p>
                                                                                    <div style="margin-top: 30px; padding: 15px; background-color: #d1ecf1; border-left: 4px solid #0c5460; border-radius: 4px;">
                                                                                        <p style="color: #0c5460; font-size: 14px; margin: 0; line-height: 1.6;">
                                                                                            <strong>💡 Sugestão:</strong> Você pode fazer um novo agendamento quando desejar. Nossa plataforma está disponível 24/7 para você!
                                                                                        </p>
                                                                                    </div>
                                                                                    <p style="text-align: center; margin-top: 30px;">
                                                                                        <a href="https://pointtils.com/appointments/new" target="_blank" rel="noreferrer noopener" style="background-color: #667eea; color: #fff; padding: 12px 30px; border-radius: 5px; text-decoration: none; font-weight: bold;">Fazer Novo Agendamento</a>
                                                                                    </p>
                                                                                    <p>Se tiver alguma dúvida ou precisar de ajuda, entre em contato conosco.</p>
                                                                                    <p>Atenciosamente,</p>
                                                                                    <p><strong>Equipe {{senderName}}</strong></p>
                                                                                </div>
                                                                            </div>
                                                                        </table>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td width="800" style="padding-top: 20px; padding-bottom: 20px; color:#4D4D4D" colspan="2">
                                                                        <center>
                                                                            <font style="font-size:10px">
                                                                                &copy; Point Tils {{ano}} - Todos os direitos reservados.
                                                                            </font>
                                                                        </center>
                                                                    </td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </table>
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
        </tbody>
    </table>'
);
