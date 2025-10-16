-- Migration: V13__Add_additional_email_templates.sql
-- Descrição: Adiciona templates de email para boas-vindas, recuperação de senha e confirmação de agendamento

-- Template de Boas-Vindas
INSERT INTO parameters (id, key, value) VALUES
(
    uuid_generate_v4(),
    'WELCOME_EMAIL',
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
                                                                                    <p>Seja muito bem-vindo(a) à plataforma PointTils!</p>
                                                                                    <p>Estamos muito felizes em tê-lo(a) conosco. Sua conta foi criada com sucesso e agora você pode aproveitar todos os recursos da nossa plataforma.</p>
                                                                                    <p>Para começar, acesse a plataforma e explore as funcionalidades disponíveis.</p>
                                                                                    <p style="text-align: center; margin-top: 30px;">
                                                                                        <a href="https://pointtils.com/login" target="_blank" rel="noreferrer noopener" style="background-color: #667eea; color: #fff; padding: 12px 30px; border-radius: 5px; text-decoration: none; font-weight: bold;">Acessar Plataforma</a>
                                                                                    </p>
                                                                                    <p>Se tiver alguma dúvida ou precisar de ajuda, nossa equipe de suporte está à disposição.</p>
                                                                                    <p>Atenciosamente,</p>
                                                                                    <p>Equipe Point Tils</p>
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

-- Template de Recuperação de Senha
INSERT INTO parameters (id, key, value) VALUES
(
    uuid_generate_v4(),
    'PASSWORD_RESET',
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
                                                                                    <p>Recebemos uma solicitação para redefinir a senha da sua conta.</p>
                                                                                    <p>Use o código abaixo para redefinir sua senha:</p>
                                                                                    <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); margin: 30px 0; padding: 20px; border-radius: 8px; text-align: center;">
                                                                                        <p style="color: #ffffff; font-size: 36px; font-weight: bold; letter-spacing: 8px; margin: 0; font-family: ''Courier New'', monospace;">
                                                                                            {{resetToken}}
                                                                                        </p>
                                                                                    </div>
                                                                                    <p style="color: #999999; font-size: 14px; line-height: 1.6;">
                                                                                        ⏱️ Este código expira em <strong>1 hora</strong>
                                                                                    </p>
                                                                                    <div style="margin-top: 30px; padding: 15px; background-color: #fff3cd; border-left: 4px solid #ffc107; border-radius: 4px;">
                                                                                        <p style="color: #856404; font-size: 14px; margin: 0; line-height: 1.6;">
                                                                                            <strong>⚠️ Importante:</strong> Se você não solicitou a recuperação de senha, ignore este email. Sua senha permanecerá inalterada.
                                                                                        </p>
                                                                                    </div>
                                                                                    <p>Atenciosamente,</p>
                                                                                    <p>Equipe Point Tils</p>
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

-- Template de Confirmação de Agendamento
INSERT INTO parameters (id, key, value) VALUES
(
    uuid_generate_v4(),
    'APPOINTMENT_CONFIRMATION',
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
                                                                                    <p>Seu agendamento foi confirmado com sucesso!</p>
                                                                                    <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0;">
                                                                                        <h3 style="color: #333333; margin-top: 0;">📅 Detalhes do Agendamento</h3>
                                                                                        <p style="color: #666666; margin: 10px 0;">
                                                                                            <strong>Data e Hora:</strong> {{appointmentDate}}
                                                                                        </p>
                                                                                        <p style="color: #666666; margin: 10px 0;">
                                                                                            <strong>Intérprete:</strong> {{interpreterName}}
                                                                                        </p>
                                                                                    </div>
                                                                                    <p>Lembre-se de estar disponível no horário agendado para sua sessão.</p>
                                                                                    <div style="margin-top: 30px; padding: 15px; background-color: #d4edda; border-left: 4px solid #28a745; border-radius: 4px;">
                                                                                        <p style="color: #155724; font-size: 14px; margin: 0; line-height: 1.6;">
                                                                                            <strong>💡 Dica:</strong> Recomendamos que você esteja em um ambiente tranquilo para melhor aproveitamento da sessão.
                                                                                        </p>
                                                                                    </div>
                                                                                    <p>Atenciosamente,</p>
                                                                                    <p>Equipe Point Tils</p>
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

-- Template de HTML enviado para admin após aprovação ou negação de cadastro
INSERT INTO parameters (id, key, value) VALUES
(
    uuid_generate_v4(),
    'ADMIN_FEEDBACK',
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
                                            <p><span style="font-weight: bold">{{message}}</span></p>
                                            <br>
                                            <p>Atenciosamente,</p>
                                            <p>Equipe Point Tils</p>
                                          </div>
                                        </div>
                                      </table>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td width="800" style="padding-top: 20px; padding-bottom: 20px; color:#4D4D4D" colspan="2">
                                      <center>
                                        <font style="font-size:10px"> &copy; Point Tils {{ano}} - Todos os direitos reservados. </font>
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