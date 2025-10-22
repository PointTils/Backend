# PointTils Backend

## Visão Geral
Backend desenvolvido em Java Spring Boot para uma plataforma de agendamento de intérpretes de libras. Segue uma arquitetura limpa com separação clara de camadas e implementa autenticação JWT, gerenciamento de migrações com Flyway, e CI/CD automatizado.

## Arquitetura
```
┌─────────────────────────────────────────────────┐
│                    API REST                     │
│ - Spring Boot 3.5.4                             │
│ - Spring Security + JWT                         │
│ - Swagger/OpenAPI                               │
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
│   └── test/                         # Testes unitários
├── utils/                            # Utilitários e serviços auxiliares
│   ├── sonarqube/                    # Configuração SonarQube
│   │   └── Dockerfile
│   └── postgres/                     # Configuração PostgreSQL
│       └── Dockerfile
├── docker-compose.yaml               # Orquestração unificada de containers
├── docker-compose.prod.yaml          # Configuração para produção
├── sonarqube-docker-compose.yaml     # Docker-compose antigo (legado)
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
├── .github/workflows/                # Pipelines CI/CD
│   ├── deploy-to-aws.yml             # Pipeline de produção
│   ├── deploy-to-dev.yml             # Pipeline de desenvolvimento
│   ├── destroy-infrastructure.yml    # Destruir infraestrutura
│   ├── discord-pr-notification.yml   # Notificações Discord
│   ├── mirror-to-gitlab.yml          # Mirror para GitLab
│   └── sonarcloud.yaml               # Análise SonarCloud
└── docs/                             # Documentação organizada
    ├── README.md
    ├── IMPLEMENTACAO_DEV_E_MELHORIAS.md
    └── [outros arquivos de documentação]
```

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Maven
- Docker (opcional)
- Git

### Comandos Úteis

**Executar localmente:**
```bash
cd pointtils
./mvnw spring-boot:run
```

**Buildar e executar com Docker (nova estrutura unificada):**
```bash
# Executa todos os serviços: aplicação, PostgreSQL e SonarQube
docker-compose up --build

# Ou para executar em background:
docker-compose up -d --build

# Executar apenas serviços específicos:
docker-compose up pointtils pointtils-db  # Apenas app + banco
docker-compose up sonarqube               # Apenas SonarQube
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
docker-compose logs sonarqube
```

**Executar testes:**
```bash
./mvnw test
```

**Gerar documentação Swagger:**
Documentação disponível no seguinte endereço:
```
https://backend-v5gs.onrender.com/swagger-ui/index.html
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

### Infraestrutura como Código

**Produção** (`terraform/`):
- VPC: `10.0.0.0/16`
- Instância EC2: `t2.medium`
- Configurações de produção

**Desenvolvimento** (`terraform-dev/`):
- VPC: `10.1.0.0/16` (isolada)
- Instância EC2: `t2.micro` (econômica)
- Configurações específicas para desenvolvimento

### Scripts de Deploy

- `terraform/deploy-app.sh` - Script de deploy
- `terraform/rollback-app.sh` - Script de rollback automático

## Dicas de Desenvolvimento

1. **Padrão de Commits**: Siga o Conventional Commits
2. **Testes**: Adicione novos testes em `src/test/java`
3. **DTOs**: Sempre use DTOs para comunicação externa
4. **Documentação**: Mantenha atualizada a documentação Swagger
5. **Docker**: Use `docker-compose` para ambiente consistente

## Configurações
As principais configurações estão em:
- `src/main/resources/application.properties` 
- `src/main/java/.../configs/OpenApiConfig.java`

### Variáveis de Ambiente
O projeto utiliza um arquivo `.env.example` como template para configurações sensíveis. Para executar o projeto:

1. Copie o arquivo `.env.example` para `.env`:
```bash
cp .env.example .env
```

2. Edite o `.env` com seus valores reais (não versionado no Git)

Variáveis comuns:
```
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
JWT_SECRET=
```

3. Para Docker, certifique-se que as variáveis estão definidas no `docker-compose.yaml` ou no `.env`
