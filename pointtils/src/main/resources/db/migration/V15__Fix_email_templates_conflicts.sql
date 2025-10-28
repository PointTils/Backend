-- Migration: V15__Fix_email_templates_conflicts.sql
-- Descrição: Corrige conflitos de templates de email usando UPSERT para evitar duplicatas

-- Template de Boas-Vindas (insere ou atualiza se existir)
INSERT INTO parameters (id, key, value)
SELECT uuid_generate_v4(), 'WELCOME_EMAIL', 
'<style type="text/css"></style>
<div style="font-family: Arial, sans-serif; padding: 20px;">
    <h2>Bem-vindo(a) ao PointTils!</h2>
    <p>Olá {{nome}}!</p>
    <p>Sua conta foi criada com sucesso.</p>
    <p>Atenciosamente,<br>Equipe Point Tils</p>
</div>'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE key = 'WELCOME_EMAIL')
ON CONFLICT (key) DO UPDATE SET value = EXCLUDED.value;

-- Template de Recuperação de Senha (insere ou atualiza se existir)
INSERT INTO parameters (id, key, value)
SELECT uuid_generate_v4(), 'PASSWORD_RESET', 
'<style type="text/css"></style>
<div style="font-family: Arial, sans-serif; padding: 20px;">
    <h2>Recuperação de Senha</h2>
    <p>Olá {{nome}}!</p>
    <p>Use o código abaixo para redefinir sua senha:</p>
    <div style="background: #667eea; color: white; padding: 15px; text-align: center; font-size: 24px; font-weight: bold;">
        {{resetToken}}
    </div>
    <p>Atenciosamente,<br>Equipe Point Tils</p>
</div>'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE key = 'PASSWORD_RESET')
ON CONFLICT (key) DO UPDATE SET value = EXCLUDED.value;

-- Template de Confirmação de Agendamento (insere ou atualiza se existir)
INSERT INTO parameters (id, key, value)
SELECT uuid_generate_v4(), 'APPOINTMENT_CONFIRMATION', 
'<style type="text/css"></style>
<div style="font-family: Arial, sans-serif; padding: 20px;">
    <h2>Agendamento Confirmado</h2>
    <p>Olá {{nome}}!</p>
    <p>Seu agendamento foi confirmado com sucesso!</p>
    <p><strong>Data:</strong> {{appointmentDate}}</p>
    <p><strong>Intérprete:</strong> {{interpreterName}}</p>
    <p>Atenciosamente,<br>Equipe Point Tils</p>
</div>'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE key = 'APPOINTMENT_CONFIRMATION')
ON CONFLICT (key) DO UPDATE SET value = EXCLUDED.value;

-- Template de Feedback para Admin (insere ou atualiza se existir)
INSERT INTO parameters (id, key, value)
SELECT uuid_generate_v4(), 'ADMIN_FEEDBACK', 
'<style type="text/css"></style>
<div style="font-family: Arial, sans-serif; padding: 20px;">
    <h2>Notificação do Sistema</h2>
    <p>{{message}}</p>
    <p>Atenciosamente,<br>Equipe Point Tils</p>
</div>'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE key = 'ADMIN_FEEDBACK')
ON CONFLICT (key) DO UPDATE SET value = EXCLUDED.value;
