-- Migration para inserir templates de email na tabela parameters
INSERT INTO parameters (id, key, value) VALUES
(
    uuid_generate_v4(),
    'PENDING_INTERPRETER_ADMIN',
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
                                  <img border="0" style="height: 100px;" alt="Logo Point Tils" src="https://i.ibb.co/GgXzx5s/pointtils.webp">
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
);
