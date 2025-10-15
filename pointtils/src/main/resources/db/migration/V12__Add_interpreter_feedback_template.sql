-- Migration para inserir template de feedback para intérprete na tabela parameters
INSERT INTO parameters (id, key, value) VALUES
(
    uuid_generate_v4(),
    'PENDING_INTERPRETER',
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
);
