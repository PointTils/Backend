# Fluxo CI/CD Implementado

## Descrição do Fluxo

O pipeline CI/CD foi configurado para automatizar o deploy da aplicação Pointtils na AWS sempre que houver um **pull request da branch `dev` para a branch `main`**.

## Trigger do Pipeline

O workflow é acionado automaticamente quando:
- **Pull Request**: Quando um PR da branch `dev` para `main` é fechado (merged)
- **Push direto**: Para a branch `main` (caso necessário)
- **Execução manual**: Através do GitHub Actions

## Etapas do Pipeline

### 1. Build e Push das Imagens

**Job**: `build-and-push-images`

**Ações**:
- ✅ Checkout do código
- ✅ Configuração das credenciais AWS
- ✅ Login no Amazon ECR
- ✅ **Construção das imagens usando `docker-compose.prod.yaml`**
- ✅ Tag e push das imagens para o ECR:
  - `pointtils:latest` (aplicação)
  - `pointtils-db:latest` (banco de dados)

### 2. Deploy da Infraestrutura

**Job**: `deploy-infrastructure` (depende do job anterior)

**Ações**:
- ✅ Configuração do Terraform
- ✅ Aplicação da infraestrutura na AWS:
  - VPC e subnets
  - Security Groups
  - Instância EC2 (t2.medium)
  - Elastic IP
  - S3 Bucket
  - ECR Repository
- ✅ Deploy das imagens na EC2
- ✅ Health check da aplicação

## Arquivos Principais

### Workflow
- `.github/workflows/deploy-to-aws.yml` - Pipeline CI/CD

### Terraform
- `terraform/main.tf` - Infraestrutura como código
- `terraform/variables.tf` - Variáveis do Terraform

### Docker
- `docker-compose.prod.yaml` - Configuração de produção
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
- `SSH_PRIVATE_KEY`
- `SSH_PUBLIC_KEY`
- `TF_API_TOKEN`

### Variáveis de Ambiente
- `CLIENT_IBGE_STATE_URL` - Hardcoded na imagem
- `CLIENT_IBGE_CITY_URL` - Hardcoded na imagem
- Configurações Spring/JPA/Flyway
- Configurações JWT
- Configurações Swagger

## Benefícios da Implementação

1. **Automação completa**: Deploy automático após merge
2. **Imagens otimizadas**: Uso do docker-compose.prod.yaml
3. **Infraestrutura como código**: Terraform para gerenciamento
4. **Segurança**: Variáveis sensíveis em secrets
5. **Monitoramento**: Health checks automáticos
6. **Rollback fácil**: Imagens versionadas no ECR

## URLs da Aplicação

Após o deploy bem-sucedido:
- **Aplicação**: `http://<IP_EC2>:8080`
- **Swagger UI**: `http://<IP_EC2>:8080/swagger-ui.html`
- **SSH**: `ssh ubuntu@<IP_EC2>`

## Próximos Passos

1. Configurar domínio customizado
2. Implementar SSL/TLS
3. Configurar monitoramento
4. Implementar backup automático do banco
5. Configurar auto-scaling
