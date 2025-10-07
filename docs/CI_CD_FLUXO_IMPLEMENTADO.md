# Fluxo CI/CD Implementado

## Descrição do Fluxo

O pipeline CI/CD foi configurado para automatizar o deploy da aplicação Pointtils na AWS em dois ambientes: **desenvolvimento** e **produção**.

## Triggers dos Pipelines

### Desenvolvimento
- **Push**: Para branches `dev` e `feature/*`
- **Pull Request**: Fechado para branch `dev`
- **Execução manual**: Através do GitHub Actions

### Produção
- **Push**: Para branch `main`
- **Pull Request**: Fechado para branch `main`
- **Execução manual**: Através do GitHub Actions

## Etapas dos Pipelines

### 1. Build e Teste

**Job**: `build-and-test`

**Ações**:
- ✅ Checkout do código
- ✅ Cache de dependências Maven
- ✅ Setup JDK 17
- ✅ Execução de testes unitários
- ✅ Configuração de credenciais AWS
- ✅ Login no Amazon ECR
- ✅ Criação de arquivo .env para build
- ✅ Build das imagens usando `docker-compose-dev.yaml`
- ✅ Tag e push das imagens para o ECR:
  - `pointtils-dev:dev-latest` (aplicação)
  - `pointtils-dev-db:dev-latest` (banco de dados)

### 2. Deploy da Infraestrutura

**Job**: `deploy-dev-infrastructure` (depende do job anterior)

**Ações**:
- ✅ Configuração do Terraform
- ✅ Criação de arquivo terraform.tfvars
- ✅ Criação de recursos de backend
- ✅ Aplicação da infraestrutura na AWS:
  - VPC e subnets
  - Security Groups
  - Instância EC2 (t2.medium para ambos os ambientes)
  - Elastic IP
  - S3 Bucket
  - ECR Repository
- ✅ Deploy das imagens na EC2
- ✅ Health check da aplicação
- ✅ Rollback automático em caso de falha

## Arquivos Principais

### Workflows
- `.github/workflows/deploy-to-dev.yml` - Pipeline de desenvolvimento
- `.github/workflows/deploy-to-aws.yml` - Pipeline de produção

### Terraform
- `terraform/main.tf` - Infraestrutura como código (produção)
- `terraform-dev/main.tf` - Infraestrutura como código (desenvolvimento)
- `terraform/variables.tf` - Variáveis do Terraform

### Docker
- `docker-compose.yaml` - Configuração unificada
- `docker-compose.prod.yaml` - Configuração de produção
- `docker-compose-dev.yaml` - Configuração de desenvolvimento
- `pointtils/Dockerfile` - Imagem da aplicação
- `utils/postgres/Dockerfile` - Imagem do banco

## Configuração de Variáveis

### Secrets do GitHub
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_REGION`
- `AWS_ACCOUNT_ID`
- `DB_USERNAME`
- `DB_PASSWORD`
- `DB_NAME`
- `JWT_SECRET`
- `TF_API_TOKEN`

### Variáveis de Ambiente
- `SERVER_PORT` - Porta da aplicação (8080)
- `JWT_EXPIRATION_TIME` - Tempo de expiração do JWT
- `SPRING_JPA_SHOW_SQL` - Mostrar SQL no console
- `SPRING_FLYWAY_LOCATIONS` - Localização das migrations
- `SPRINGDOC_API_DOCS_ENABLED` - Habilitar documentação OpenAPI

## Benefícios da Implementação

1. **Automação completa**: Deploy automático para desenvolvimento e produção
2. **Ambientes isolados**: VPCs separadas para dev e prod
3. **Infraestrutura como código**: Terraform para gerenciamento
4. **Segurança**: Variáveis sensíveis em secrets
5. **Monitoramento**: Health checks automáticos
6. **Rollback automático**: Recuperação em caso de falha
7. **Testes automatizados**: Execução de testes antes do deploy

## URLs da Aplicação

Após o deploy bem-sucedido:
- **Aplicação**: `http://<IP_EC2>:8080`
- **Swagger UI**: `http://<IP_EC2>:8080/swagger-ui.html`
- **Health Check**: `http://<IP_EC2>:8080/actuator/health`
- **SSH**: `ssh ubuntu@<IP_EC2>`

## Funcionalidades Implementadas

### Migrations com Flyway
- ✅ Controle de versão do banco de dados
- ✅ 8 migrations implementadas (V1 a V8)
- ✅ Execução automática na inicialização

### Autenticação JWT
- ✅ Sistema de login com tokens
- ✅ Refresh tokens implementados
- ✅ Configuração de expiração

### Integração AWS
- ✅ S3 para armazenamento
- ✅ ECR para registry de imagens
- ✅ IAM roles para acesso seguro

## Próximos Passos

1. Configurar domínio customizado
2. Implementar SSL/TLS
3. Configurar monitoramento com CloudWatch
4. Implementar backup automático do banco
5. Configurar auto-scaling
6. Implementar CDN para assets estáticos
