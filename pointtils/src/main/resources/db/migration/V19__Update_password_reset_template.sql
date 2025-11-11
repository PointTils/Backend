-- Migration: V19__Update_password_reset_template.sql
-- Descrição: Atualiza o template de recuperação de senha para o formato aprovado pelo time de templates

UPDATE parameters 
SET value = '<table width="100%" style="font-family: Helvetica, Arial, sans-serif; padding-top: 40px; margin:0; padding:0; -webkit-font-smoothing: antialiased" cellspacing="0" cellpadding="0" border="0" align="center">
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
                                
                                <p>Recebemos sua solicitação para redefinir a senha de acesso ao aplicativo.</p>
                                
                                <p>Para continuar, utilize o seguinte código de verificação no app:</p>
                                
                                <center>
                                  <div style="background-color:#f3f3f3; border: 1px dashed #cccccc; padding: 15px; font-size: 18px; font-weight: bold; color:#333; letter-spacing: 3px; margin: 20px 0; display: inline-block;">
                                    {{resetToken}}
                                  </div>
                                </center>
                                
                                <p>Insira esse código no aplicativo para concluir a troca de senha. Caso você não tenha solicitado essa alteração, por favor desconsidere este e-mail.</p>
                                
                                <p style="margin-top: 20px;">Atenciosamente,<br>Equipe Point Tils</p>
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
WHERE key = 'PASSWORD_RESET';