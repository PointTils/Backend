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
                                  <img border="0" style="height: 100px;" alt="Logo Point Tils" src="https://pointtils-api-tests-d9396dcc.s3.us-east-2.amazonaws.com/logo_pointils.png">
                                </center>
                              </td>
                            </tr>
                            <tr>
                              <td width="800" style="font-size: 14px; color: #333; text-align: left; padding:20px;" colspan="2">
                                <p>Olá <span style="font-weight: bold">{{nome}}!</span></p>
                                <p>Ótimas notícias! Seu agendamento foi <span style="color: #28a745; font-weight: bold;">ACEITO</span>!</p> 📅 Confira abaixo os detalhes do agendamento:</p>

                                <!-- Tabela de informações -->
                                <table width="100%" cellpadding="8" cellspacing="0" border="0" style="border-collapse: collapse; font-size: 13px; margin: 20px 0;">
                                  <tr style="background-color: #f9f9f9;">
                                    <td style="border: 1px solid #e7eeee; font-weight: bold; width: 30%;">Data e Hora</td>
                                    <td style="border: 1px solid #e7eeee;">{{appointmentDate}}</td>
                                  </tr>
                                  <tr>
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">Descrição</td>
                                    <td style="border: 1px solid #e7eeee;">{{appointmentDescription}}</td>
                                  </tr>
                                  <tr>
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">{{subject}}</td>
                                    <td style="border: 1px solid #e7eeee;">{{subjectName}}</td>
                                  </tr>
                                  <tr>
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">Modalidade</td>
                                    <td style="border: 1px solid #e7eeee;">{{appointmentModality}}</td>
                                  </tr>
                                  <tr style="background-color: #f9f9f9;">
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">Local</td>
                                    <td style="border: 1px solid #e7eeee;">{{appointmentLocation}}</td>
                                  </tr>
                                </table>

                                <p style="font-weight: bold; margin-top: 20px;">💡 Lembre-se:</p>
                                <p>Esteja disponível no horário agendado. Caso necessário, você consegue realizar o cancelamento do agendamento através do aplicativo.</p>
                                <br>
                                <p>Atenciosamente,</p>
                                <p>Equipe {{senderName}}</p>
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
                                  <img border="0" style="height: 100px;" alt="Logo Point Tils" src="https://pointtils-api-tests-d9396dcc.s3.us-east-2.amazonaws.com/logo_pointils.png">
                                </center>
                              </td>
                            </tr>
                            <tr>
                              <td width="800" style="font-size: 14px; color: #333; text-align: left; padding:20px;" colspan="2">
                                <p>Olá <span style="font-weight: bold">{{nome}}!</span></p>
                                <p>Informamos que sua solicitação de agendamento foi <span style="color: #dc3545; font-weight: bold;">NEGADA</span>.</p> 📅 Confira abaixo os detalhes da solicitação:</p>

                                <!-- Tabela de informações -->
                                <table width="100%" cellpadding="8" cellspacing="0" border="0" style="border-collapse: collapse; font-size: 13px; margin: 20px 0;">
                                  <tr style="background-color: #f9f9f9;">
                                    <td style="border: 1px solid #e7eeee; font-weight: bold; width: 30%;">Data e Hora</td>
                                    <td style="border: 1px solid #e7eeee;">{{appointmentDate}}</td>
                                  </tr>
                                  <tr>
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">Descrição</td>
                                    <td style="border: 1px solid #e7eeee;">{{appointmentDescription}}</td>
                                  </tr>
                                  <tr>
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">{{subject}}</td>
                                    <td style="border: 1px solid #e7eeee;">{{subjectName}}</td>
                                  </tr>
                                  <tr>
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">Modalidade</td>
                                    <td style="border: 1px solid #e7eeee;">{{appointmentModality}}</td>
                                  </tr>
                                  <tr style="background-color: #f9f9f9;">
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">Local</td>
                                    <td style="border: 1px solid #e7eeee;">{{appointmentLocation}}</td>
                                  </tr>
                                </table>

                                <p style="font-weight: bold; margin-top: 20px;">💡 Sugestão:</p>
                                <p>Tente agendar com outro intérprete ou escolher um horário diferente no aplicativo.</p>
                                <br>
                                <p>Atenciosamente,</p>
                                <p>Equipe {{senderName}}</p>
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
                                  <img border="0" style="height: 100px;" alt="Logo Point Tils" src="https://pointtils-api-tests-d9396dcc.s3.us-east-2.amazonaws.com/logo_pointils.png">
                                </center>
                              </td>
                            </tr>
                            <tr>
                              <td width="800" style="font-size: 14px; color: #333; text-align: left; padding:20px;" colspan="2">
                                <p>Olá <span style="font-weight: bold">{{nome}}!</span></p>
                                <p>Informamos que o agendamento foi <span style="color: #ffc107; font-weight: bold;">CANCELADO</span>.</p> 📅 Confira abaixo os detalhes do agendamento:</p>

                                <!-- Tabela de informações -->
                                <table width="100%" cellpadding="8" cellspacing="0" border="0" style="border-collapse: collapse; font-size: 13px; margin: 20px 0;">
                                  <tr style="background-color: #f9f9f9;">
                                    <td style="border: 1px solid #e7eeee; font-weight: bold; width: 30%;">Data e Hora</td>
                                    <td style="border: 1px solid #e7eeee;">{{appointmentDate}}</td>
                                  </tr>
                                  <tr>
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">Descrição</td>
                                    <td style="border: 1px solid #e7eeee;">{{appointmentDescription}}</td>
                                  </tr>
                                  <tr>
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">{{subject}}</td>
                                    <td style="border: 1px solid #e7eeee;">{{subjectName}}</td>
                                  </tr>
                                  <tr>
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">Modalidade</td>
                                    <td style="border: 1px solid #e7eeee;">{{appointmentModality}}</td>
                                  </tr>
                                  <tr style="background-color: #f9f9f9;">
                                    <td style="border: 1px solid #e7eeee; font-weight: bold;">Local</td>
                                    <td style="border: 1px solid #e7eeee;">{{appointmentLocation}}</td>
                                  </tr>
                                </table>

                                <p style="font-weight: bold; margin-top: 20px;">💡 Sugestão:</p>
                                <p>Tente agendar com outro intérprete ou escolher um horário diferente no aplicativo.</p>
                                <br>
                                <p>Atenciosamente,</p>
                                <p>Equipe {{senderName}}</p>
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
);
