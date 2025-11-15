# PointTils Backend

## Visão Geral
Backend desenvolvido em Java Spring Boot 3.5.4 para uma plataforma de agendamento de intérpretes de libras. Segue uma arquitetura limpa com separação clara de camadas e implementa autenticação JWT, gerenciamento de migrações com Flyway, e CI/CD automatizado.

## 🚀 Tecnologias e Versões

- **Java 17** - Linguagem de programação
- **Spring Boot 3.5.4** - Framework principal
- **PostgreSQL** - Banco de dados
- **Flyway** - Migrações de banco de dados
- **Spring Security + JWT** - Autenticação e autorização
- **SpringDoc OpenAPI 2.8.6** - Documentação da API
- **AWS S3** - Armazenamento de arquivos
- **Docker** - Containerização
- **Maven** - Gerenciamento de dependências
- **SonarQube** - Análise de qualidade de código
- **Jacoco** - Cobertura de testes (mínimo 70%)
- **Prometheus** - Monitoramento e métricas
- **Grafana** - Visualização de métricas
- **Terraform** - Infraestrutura como código

## Arquitetura
```
┌─────────────────────────────────────────────────┐
│                    API REST                     │
│ - Spring Boot 3.5.4                             │
│ - Spring Security + JWT                         │
│ - Swagger/OpenAPI 2.8.6                         │
│ - Spring Data JPA + PostgreSQL                  │
│ - Flyway Migrations                             │
│ - AWS S3 Integration                            │
│ - Prometheus Metrics                            │
└─────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│                 Controllers (REST)              │
│ - AuthController (Login/Refresh)                │
│ - UserController (Gestão de usuários)           │
│ - AppointmentController (Agendamentos)          │
│ - InterpreterController (Intérpretes)           │
│ - EnterpriseController (Empresas)               │
│ - SpecialtyController (Especialidades)          │
│ - EmailController (Envio de emails)             │
│ - ParametersController (Parâmetros do sistema)  │
│ - RatingController (Avaliações)                 │
│ - ScheduleController (Horários)                 │
│ - StateController (Estados)                     │
│ - UserAppController (Usuários do app)           │
│ - UserPictureController (Fotos de usuário)      │
│ - UserSpecialtyController (Especialidades)      │
└─────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│                    Services                     │
│ - AuthService (Autenticação)                    │
│ - UserService (Gestão de usuários)              │
│ - AppointmentService (Agendamentos)             │
│ - InterpreterService (Intérpretes)              │
│ - EnterpriseService (Empresas)                  │
│ - SpecialtyService (Especialidades)             │
│ - EmailService (Envio de emails via Brevo)      │
│ - S3Service (Armazenamento AWS S3)              │
│ - ParametersService (Parâmetros)                │
│ - RatingService (Avaliações)                    │
│ - ScheduleService (Horários)                    │
│ - StateService (Estados)                        │
│ - NotificationService (Notificações)            │
│ - MemoryResetTokenService (Tokens)              │
└─────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│                  Repositories                   │
│ - UserRepository                                │
│ - AppointmentRepository                         │
│ - InterpreterRepository                         │
│ - EnterpriseRepository                          │
│ - SpecialtyRepository                           │
│ - ParametersRepository                          │
│ - RatingRepository                              │
│ - ScheduleRepository                            │
│ - StateRepository                               │
└─────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│                     Domain                      │
│ - User (Usuário)                                │
│ - Appointment (Agendamento)                     │
│ - Interpreter (Intérprete)                      │
│ - Enterprise (Empresa)                          │
│ - Specialty (Especialidade)                     │
│ - Parameters (Parâmetros do sistema)            │
│ - Rating (Avaliação)                            │
│ - Schedule (Horário)                            │
│ - State (Estado)                                │
└─────────────────────────────────────────────────┘
```

## Estrutura de Pastas
```
.
├── pointtils/                        # Aplicação principal
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/pointtils/pointtils/
│   │   │   │   ├── src/                  # Código fonte principal
│   │   │   │   │   ├── application/      # Lógica de aplicação
│   │   │   │   │   │   ├── controllers/  # Controladores REST
│   │   │   │   │   │   ├── dto/          # Objetos de transferência
│   │   │   │   │   │   ├── mapper/       # Mapeadores DTO-Entity
│   │   │   │   │   │   └── services/     # Serviços de negócio
│   │   │   │   │   ├── core/             # Núcleo do domínio
│   │   │   │   │   │   └── domain/       # Entidades e enums
│   │   │   │   │   └── infrastructure/   # Infraestrutura
│   │   │   │   │       ├── configs/      # Configurações
│   │   │   │   │       └── repositories/ # Repositórios
│   │   ├── resources/                # Arquivos de configuração
│   │   │   ├── application.properties    # Configurações gerais
│   │   │   ├── application-prod.properties # Configurações produção
│   │   │   └── db/migration/         # Migrações Flyway (V1-V23)
│   └── test/                         # Testes unitários
├── utils/                            # Utilitários e serviços auxiliares
│   ├── sonarqube/                    # Configuração SonarQube
│   │   └── Dockerfile
│   ├── postgres/                     # Configuração PostgreSQL
│   │   └── Dockerfile
│   ├── prometheus/                   # Monitoramento Prometheus
│   │   ├── Dockerfile
│   │   ├── prometheus.yml            # Configuração Prometheus
│   │   ├── alerts.yml                # Alertas
│   │   └── recording_rules.yml       # Regras de gravação
│   └── grafana/                      # Dashboard Grafana
│       ├── Dockerfile
│       ├── grafana.ini               # Configuração Grafana
│       └── provisioning/             # Provisionamento automático
│           ├── datasources/prometheus.yml
│           └── dashboards/           # Dashboards pré-configurados
├── docker-compose.yaml               # Orquestração unificada de containers
├── docker-compose.prod.yaml          # Configuração para produção
├── docker-compose-dev.yaml           # Configuração para desenvolvimento
├── terraform/                        # Infraestrutura como código (Produção)
│   ├── main.tf
│   ├── variables.tf
│   ├── deploy-app.sh                 # Script de deploy
│   └── rollback-app.sh               # Script de rollback automático
├── terraform-dev/                    # Infraestrutura como código (Desenvolvimento)
│   ├── main.tf
│   ├── variables.tf
│   ├── backend.tf
│   └── terraform.tfvars
├── scripts/                          # Scripts utilitários
│   ├── check_deadlines.py            # Verificação de prazos
│   └── README.md
├── .github/workflows/                # Pipelines CI/CD
│   ├── deploy-to-aws.yml             # Pipeline de produção
│   ├── deploy-to-dev.yml             # Pipeline de desenvolvimento
│   ├── destroy-infrastructure.yml    # Destruir infraestrutura
│   ├── discord-pr-notification.yml   # Notificações Discord
│   ├── mirror-to-gitlab.yml          # Mirror para GitLab
│   ├── notify-deadlines.yml          # Notificações de prazos
│   └── sonarcloud.yaml               # Análise SonarCloud
└── docs/                             # Documentação organizada
    ├── README.md
    ├── IMPLEMENTACAO_DEV_E_MELHORIAS.md
    ├── CI_CD_FLUXO_IMPLEMENTADO.md
    ├── DEPLOY_GUIDE.md
    ├── EMAIL_API_GUIDE.md
    ├── FLYWAY_MIGRATION_GUIDE.md
    ├── JWT_REFRESH_TOKEN_IMPLEMENTATION.md
    └── [outros arquivos de documentação]
```

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+
- Docker e Docker Compose
- Git

### Configuração Inicial

1. **Clone o repositório:**
```bash
git clone https://github.com/PointTils/Backend.git
cd Backend
```

2. **Configure as variáveis de ambiente:**
```bash
cp .env.example .env
# Edite o arquivo .env com suas configurações
```

### Execução com Docker (Recomendado)

**Executar todos os serviços (aplicação + banco + monitoramento + SonarQube):**
```bash
docker-compose up --build
```

**Executar em background:**
```bash
docker-compose up -d --build
```

**Executar apenas serviços específicos:**
```bash
docker-compose up pointtils pointtils-db  # Apenas app + banco
docker-compose up prometheus grafana     # Apenas monitoramento
docker-compose up sonarqube              # Apenas SonarQube
```

**Comandos Docker úteis:**
```bash
# Ver status dos containers
docker-compose ps

# Parar todos os serviços
docker-compose down

# Parar e remover volumes (dados)
docker-compose down -v

# Ver logs de um serviço específico
docker-compose logs pointtils
docker-compose logs prometheus
docker-compose logs grafana
docker-compose logs sonarqube

# Rebuildar e executar
docker-compose up --build
```

### Execução Local (Sem Docker)

**Executar a aplicação:**
```bash
cd pointtils
./mvnw spring-boot:run
```

**Executar testes:**
```bash
./mvnw test
```

**Executar com cobertura de testes:**
```bash
./mvnw test -Pcoverage
```

**Buildar o projeto:**
```bash
./mvnw clean package
```

## 📋 Variáveis de Ambiente

O projeto utiliza um arquivo `.env` para configurações. Veja `.env.example` para todas as variáveis disponíveis:

### Configurações Principais
```env
# Database
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=pointtils-db

# Spring Application
SPRING_APPLICATION_NAME=pointtils-api
SERVER_PORT=8080

# DataSource
SPRING_DATASOURCE_URL=jdbc:postgresql://pointtils-db:5432/pointtils-db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# JWT
JWT_SECRET=sua-chave-secreta-aqui
JWT_EXPIRATION_TIME=900000
JWT_REFRESH_EXPIRATION_TIME=604800000

# AWS
AWS_REGION=us-east-2
AWS_ACCESS_KEY_ID=seu-access-key
AWS_SECRET_ACCESS_KEY=seu-secret-key
CLOUD_AWS_BUCKET_NAME=seu-bucket

# Email (Brevo SMTP)
BREVO_SMTP_HOST=smtp-relay.brevo.com
BREVO_SMTP_PORT=587
BREVO_SMTP_USERNAME=seu-username
BREVO_SMTP_PASSWORD=sua-senha
BREVO_SENDER_EMAIL=seu-email
BREVO_SENDER_NAME=PointTils

# Monitoramento
PROMETHEUS_PORT=9090
GRAFANA_PORT=3000
```

## 🏗️ CI/CD e Deploy

### Pipelines Implementados

**Desenvolvimento** (`.github/workflows/deploy-to-dev.yml`):
- Trigger: Push para `dev` e `feature/*`
- Cache de dependências Maven
- Testes automáticos
- Deploy automático para ambiente de desenvolvimento
- Rollback automático

**Produção** (`.github/workflows/deploy-to-aws.yml`):
- Trigger: Push para `main`, PR closed
- Cache de dependências Maven
- Deploy para AWS com aprovação manual
- Rollback automático

**Análise de Qualidade** (`.github/workflows/sonarcloud.yaml`):
- Análise SonarCloud em cada PR
- Verificação de cobertura de testes
- Análise de vulnerabilidades

### Infraestrutura como Código

**Produção** (`terraform/`):
- VPC: `10.0.0.0/16`
- Instância EC2: `t2.micro` (Ohio - us-east-2) - Alterado para economia
- Configurações de produção
- Elastic IP para IP público fixo

**Desenvolvimento** (`terraform-dev/`):
- VPC: `10.2.0.0/16` (isolada)
- Instância EC2: `t2.micro` (Ohio - us-east-2) - Alterado para economia
- Configurações específicas para desenvolvimento

### Scripts de Deploy

- `terraform/deploy-app.sh` - Script de deploy
- `terraform/rollback-app.sh` - Script de rollback automático
- `terraform-dev/deploy-dev-app.sh` - Deploy para desenvolvimento

## 📊 Monitoramento

### Prometheus
Para coleta de métricas da aplicação:
```bash
docker-compose up prometheus
```
Acesse: `http://localhost:9090`

### Grafana
Para visualização de dashboards:
```bash
docker-compose up grafana
```
Acesse: `http://localhost:3000`
- Usuário: `admin`
- Senha: `admin123456` (dev) / `admin` (prod)

### SonarQube
Para análise de qualidade de código:
```bash
docker-compose up sonarqube
```
Acesse: `http://localhost:9000`

### Health Checks
A aplicação expõe endpoints de health check:
```
GET /actuator/health
GET /actuator/metrics
GET /actuator/prometheus
```

## 📚 Documentação da API

### Swagger UI
A documentação interativa da API está disponível em:
```
http://localhost:8080/swagger-ui/index.html
```

### Endpoints Principais

**Autenticação:**
- `POST /auth/login` - Login de usuário
- `POST /auth/refresh` - Refresh token
- `POST /auth/logout` - Logout

**Usuários:**
- `GET /users` - Listar usuários
- `POST /users` - Criar usuário
- `GET /users/{id}` - Buscar usuário por ID

**Agendamentos:**
- `GET /appointments` - Listar agendamentos
- `POST /appointments` - Criar agendamento
- `PUT /appointments/{id}` - Atualizar agendamento

**Intérpretes:**
- `GET /interpreters` - Listar intérpretes
- `POST /interpreters` - Criar intérprete
- `GET /interpreters/{id}` - Buscar intérprete por ID

## 🗄️ Migrações de Banco (Flyway)

O projeto utiliza Flyway para gerenciar migrações de banco de dados. As migrações estão em `pointtils/src/main/resources/db/migration/`:

- `V1__Create_initial_schema.sql` - Schema inicial
- `V2__Insert_seed_data.sql` - Dados iniciais
- `V3-V4__Update_user_type_and_data.sql` - Atualizações de usuário
- `V5__Update_specialty_names.sql` - Nomes de especialidades
- `V6__Update_address_data.sql` - Dados de endereço
- `V7__Insert_seed_specialties_and_update_schedule_enum.sql` - Especialidades e horários
- `V8__Add_test_appointments.sql` - Agendamentos de teste
- `V9__Remove_min_max_value_from_interpreter.sql` - Remoção de valores min/max
- `V10__Add_unique_constraint_to_parameters.sql` - Constraint única
- `V11-V13__Insert_email_templates.sql` - Templates de email
- `V14__Update_appointment_date.sql` - Atualização de datas
- `V15__Add_create_at_and_modified_at_all_collums.sql` - Timestamps
- `V16__Insert_parameters_faq.sql` - FAQ do sistema
- `V17__Insert_additional_data.sql` - Dados adicionais
- `V18__Add_video_url_to_interpreter.sql` - URL de vídeo para intérpretes
- `V19__Update_password_reset_template.sql` - Template de reset de senha
- `V20__Update_client_users_to_person.sql` - Atualização de usuários
- `V21__Create_user_app_table.sql` - Tabela de usuários do app
- `V22-V23__Update_email_templates_logo.sql` - Templates de email com logo

## 🧪 Testes

### Executar Testes
```bash
cd pointtils
./mvnw test
```

### Cobertura de Testes
O projeto utiliza Jacoco para cobertura de testes com os seguintes requisitos mínimos:
- Linhas: 70%
- Branch: 70%
- Instruções: 75%
- Métodos: 70%
- Classes: 90%

### Executar com Cobertura
```bash
./mvnw test -Pcoverage
```

## 🔧 Configurações Avançadas

### Configurações de Produção
As configurações específicas para produção estão em:
- `pointtils/src/main/resources/application-prod.properties`
- `docker-compose.prod.yaml`

### Templates de Email
O sistema utiliza templates de email configurados na tabela `parameters`:
- `WELCOME_EMAIL` - Email de boas-vindas
- `PASSWORD_RESET` - Redefinição de senha
- `APPOINTMENT_CONFIRMATION` - Confirmação de agendamento
- `PENDING_INTERPRETER` - Intérprete pendente
- `PENDING_INTERPRETER_ADMIN` - Notificação para admin
- `ADMIN_FEEDBACK` - Feedback para admin

Todos os templates utilizam o logo da aplicação em: `https://pointtils-api-tests-d9396dcc.s3.us-east-2.amazonaws.com/logo_pointils.png`

## 🤝 Contribuição

### Padrões de Desenvolvimento

1. **Commits**: Siga o Conventional Commits
2. **Branches**: Use `feature/`, `fix/`, `hotfix/`
3. **Code Review**: Todas as PRs precisam de review
4. **Testes**: Adicione testes para novas funcionalidades
5. **Documentação**: Mantenha a documentação atualizada

### Fluxo de Trabalho

1. Crie uma branch a partir de `dev`
2. Desenvolva a feature/fix
3. Adicione testes
4. Execute `./mvnw test` para verificar
5. Faça commit seguindo Conventional Commits
6. Abra PR para `dev`
7. Aguarde code review
8. Após aprovação, merge para `dev`

## 📞 Suporte

Para dúvidas ou problemas:
1. Consulte a documentação em `docs/`
2. Verifique os logs da aplicação
3. Abra uma issue no GitHub
4. Entre em contato com a equipe de desenvolvimento

---

**PointTils Backend** - Plataforma de agendamento de intérpretes de libras
# Trigger deployment - Tue Nov  4 01:51:17 UTC 2025
