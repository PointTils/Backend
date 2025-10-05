# Guia de Utilização do Sistema de Email - PointTils

## 📧 Visão Geral

O sistema de email do PointTils foi implementado utilizando **SMTP Brevo** e oferece funcionalidades completas para envio de emails automáticos e personalizados. Todos os endpoints são protegidos por autenticação JWT.

## 🔧 Configuração

### Variáveis de Ambiente

```bash
# Brevo SMTP Configuration
BREVO_SMTP_HOST=smtp-relay.brevo.com
BREVO_SMTP_PORT=587
BREVO_SMTP_USERNAME=983141002@smtp-brevo.com
BREVO_SMTP_PASSWORD=shSZvIRTFLr8U3MN
BREVO_SENDER_EMAIL=point.tils.ages@gmail.com
BREVO_SENDER_NAME=PointTils
```

### Configuração Spring Boot

As configurações estão definidas em `application.properties`:

```properties
# Brevo SMTP Configuration
spring.mail.host=${BREVO_SMTP_HOST:smtp-relay.brevo.com}
spring.mail.port=${BREVO_SMTP_PORT:587}
spring.mail.username=${BREVO_SMTP_USERNAME:983141002@smtp-brevo.com}
spring.mail.password=${BREVO_SMTP_PASSWORD:shSZvIRTFLr8U3MN}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# Email Sender Configuration
app.mail.from=${BREVO_SENDER_EMAIL:point.tils.ages@gmail.com}
app.mail.name=${BREVO_SENDER_NAME:PointTils}
```

## 📋 Endpoints Disponíveis

### 1. Envio de Email Simples
**POST** `/v1/email/send`

```bash
curl -X POST "http://localhost:8080/v1/email/send" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -d '{
    "to": "destinatario@email.com",
    "subject": "Assunto do Email",
    "body": "Corpo do email em texto simples",
    "from_name": "PointTils"
  }'
```

### 2. Envio de Email HTML
**POST** `/v1/email/send-html`

```bash
curl -X POST "http://localhost:8080/v1/email/send-html" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -d '{
    "to": "destinatario@email.com",
    "subject": "Assunto do Email HTML",
    "body": "<h1>Título</h1><p>Conteúdo HTML</p>",
    "from_name": "PointTils"
  }'
```

### 3. Email de Boas-Vindas
**POST** `/v1/email/welcome/{email}`

```bash
curl -X POST "http://localhost:8080/v1/email/welcome/usuario@email.com?userName=João" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 4. Email de Recuperação de Senha
**POST** `/v1/email/password-reset/{email}`

```bash
curl -X POST "http://localhost:8080/v1/email/password-reset/usuario@email.com?userName=João&resetToken=ABC123XYZ" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 5. Email de Confirmação de Agendamento
**POST** `/v1/email/appointment-confirmation/{email}`

```bash
curl -X POST "http://localhost:8080/v1/email/appointment-confirmation/usuario@email.com?userName=João&appointmentDate=2025-01-20%2010:00&interpreterName=Maria%20Silva" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

## 🔐 Autenticação

Todos os endpoints de email requerem autenticação JWT. Para obter o token:

```bash
curl -X POST "http://localhost:8080/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "person1@email.com",
    "password": "password"
  }'
```

## 🎨 Templates HTML

### Email de Boas-Vindas
- Design profissional com gradiente
- Botão de acesso à plataforma
- Layout responsivo

### Email de Recuperação de Senha
- Código destacado em caixa colorida
- Informações de expiração
- Aviso de segurança

### Email de Confirmação de Agendamento
- Detalhes do agendamento em caixa informativa
- Dicas para melhor aproveitamento
- Design clean e profissional

## 🚀 Testes

### Teste de Configuração
```bash
curl -X GET "http://localhost:8080/v1/email/test" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### Teste de Email HTML
```bash
curl -X GET "http://localhost:8080/v1/email/test-html" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

## 📊 Respostas da API

Todas as respostas seguem o formato:

```json
{
  "success": true,
  "message": "Email enviado com sucesso",
  "to": "destinatario@email.com"
}
```

## 🔧 Estrutura do Projeto

```
pointtils/src/main/java/com/pointtils/pointtils/src/
├── application/
│   ├── controllers/
│   │   └── EmailController.java
│   ├── dto/
│   │   └── requests/
│   │       └── EmailRequestDTO.java
│   └── services/
│       └── EmailService.java
```

## ✅ Status

- ✅ **SMTP Brevo** - Configurado e funcionando
- ✅ **Autenticação JWT** - Implementada
- ✅ **Templates HTML** - Criados e testados
- ✅ **Endpoints** - Todos funcionando
- ✅ **Padrão snake_case** - Seguido
- ✅ **Docker Compose** - Integrado

## 📞 Suporte

Para problemas ou dúvidas sobre o sistema de email, verifique:
1. Configurações do Brevo SMTP
2. Credenciais de autenticação
3. Logs da aplicação
4. Status do Docker Compose

---

**Sistema 100% operacional e testado!** 🎉
