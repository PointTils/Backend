UPDATE parameters
SET value = '<style type="text/css"></style>
<table width="100%" style="font-family: Helvetica, Arial, sans-serif; padding-top: 40px; margin:0; padding:0; -webkit-font-smoothing: antialiased" cellspacing="0" cellpadding="0" border="0" align="center">
  <tbody>
    <tr>
      <td>
        <table width="90%" cellspacing="0" cellpadding="0" border="0" align="center" style="margin-top: 50px; margin-bottom: 100px;">
          <tbody>
            <tr>
              <td width="800" bgcolor="#F3F3F3" style="padding: 15px;">
                <table width="800" cellspacing="0" cellpadding="0" border="0" align="center">
                  <tbody>
                    <tr>
                      <td width="800" bgcolor="#ffffff" style="border: 1px solid #e7eeee; border-radius: 5px;">
                        <table width="800" cellspacing="0" cellpadding="0" border="0" align="center" style="margin-top: 40px;">
                          <tbody>
                            <tr>
                              <td width="800" style="padding-bottom: 40px; border-bottom: 1px solid #e7eeee;" colspan="2">
                                <center>
                                  <img border="0" style="height: 100px;" alt="Logo Point Tils" src="https://i.ibb.co/DHQkH37B/Dark-Blue-1.png">
                                </center>
                              </td>
                            </tr>
                            <tr>
                              <td width="800" style="font-size: 14px; color: #333; text-align: left; padding:20px;" colspan="2">
                                <p>Olá <span style="font-weight: bold">{{nome}}!</span></p>
                                <p>Você recebeu uma nova solicitação de cadastro de intérprete no aplicativo. Confira abaixo as informações enviadas:</p>

                                <!-- Tabela de informações -->
                                <table width="100%" cellpadding="8" cellspacing="0" border="0" style="border-collapse: collapse; font-size: 13px; margin: 20px 0;">
                                  <tr style="background-color: #f9f9f9;">
                                    <td style="border: 1px solid #e7eeee; font-weight: bold; width: 30%;">Nome</td>
                                    <td style="border: 1px solid #e7eeee;">{{nome}}</td>
                                  </tr>
                                  <tr>
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">CPF</td>
                                    <td style="border: 1px solid #e7eeee;">{{cpf}}</td>
                                  </tr>
                                  <tr style="background-color: #f9f9f9;">
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">CNPJ</td>
                                    <td style="border: 1px solid #e7eeee;">{{cnpj}}</td>
                                  </tr>
                                  <tr>
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">E-mail</td>
                                    <td style="border: 1px solid #e7eeee;">{{email}}</td>
                                  </tr>
                                  <tr style="background-color: #f9f9f9;">
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">Telefone</td>
                                    <td style="border: 1px solid #e7eeee;">{{telefone}}</td>
                                  </tr>
                                  <tr style="background-color: #f9f9f9;">
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">Vídeo</td>
                                    <td style="border: 1px solid #e7eeee;">{{video}}</td>
                                  </tr>
                                </table>

                                <p style="font-weight: bold; margin-top: 20px;">Por favor, analise os dados e escolha uma das opções:</p>
                                <p>
                                  <a href="{{link_accept_api}}" target="_blank" rel="noreferrer noopener" style="background-color: #008000; color: #fff; padding: 10px 20px; border-radius: 5px; text-decoration: none; font-weight: bold;">Aceitar</a>
                                  &nbsp;
                                  <a href="{{link_reject_api}}" target="_blank" rel="noreferrer noopener" style="background-color: #FF0000; color: #fff; padding: 10px 20px; border-radius: 5px; text-decoration: none; font-weight: bold;">Recusar</a>
                                </p>

                                <p>Caso aceite, o intérprete será habilitado para acessar o aplicativo. Em caso de recusa, ele será notificado automaticamente.</p>
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
WHERE key = 'PENDING_INTERPRETER_ADMIN';

UPDATE parameters
SET value = '<style type="text/css"></style>
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
                                    <img border="0" style="height: 100px;" alt="Logo Point Tils" src="https://i.ibb.co/DHQkH37B/Dark-Blue-1.png">
                                  </center>
                                </td>
                              </tr>
                              <tr>
                                <td width="800" style="font-size: 12px; color: #65707a; text-align: left; padding-top:20px; padding-bottom: 20px" colspan="2">
                                  <table style="font-size: 12px; color: #65707a; text-align: left; padding-top:20px; padding-bottom: 20px" colspan="2">
                                    <div style="margin: 0 auto; padding: 20px;">
                                      <div class="content" style="margin: 1em;">
                                        <p>Olá <span style="font-weight: bold">{{nome}}!</span></p>
                                        <p>Informamos que {{respostaSolicitacao}}.</p>
                                        <p>Agradecemos pelo seu interesse em nossa plataforma!</p>
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
WHERE key = 'PENDING_INTERPRETER';

UPDATE parameters
SET value = '<style type="text/css"></style>
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
                                                                            <img border="0" style="height: 100px;" alt="Logo Point Tils" src="https://i.ibb.co/DHQkH37B/Dark-Blue-1.png">
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
WHERE key = 'WELCOME_EMAIL';

UPDATE parameters
SET value = '<style type="text/css"></style>
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
                                                                            <img border="0" style="height: 100px;" alt="Logo Point Tils" src="https://i.ibb.co/DHQkH37B/Dark-Blue-1.png">
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
WHERE key = 'PASSWORD_RESET';

UPDATE parameters
SET value = '<style type="text/css"></style>
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
                                                                            <img border="0" style="height: 100px;" alt="Logo Point Tils" src="https://i.ibb.co/DHQkH37B/Dark-Blue-1.png">
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
WHERE key = 'APPOINTMENT_CONFIRMATION';

UPDATE parameters
SET value = '<style type="text/css"></style>
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
                                        <img border="0" style="height: 100px;" alt="Logo Point Tils" src="https://i.ibb.co/DHQkH37B/Dark-Blue-1.png">
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
WHERE key = 'ADMIN_FEEDBACK';