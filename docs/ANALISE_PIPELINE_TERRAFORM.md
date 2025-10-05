# Análise do Pipeline de Deploy e Configurações Terraform

## Situação Atual

### 1. Pipeline GitHub Actions (`deploy-to-aws.yml`)
✅ **Funcionalidades implementadas:**
- Build da aplicação Java com Maven
- Push da imagem Docker para ECR
- Provisionamento de infraestrutura com Terraform
- Deploy automático na instância EC2
- Configuração de banco de dados PostgreSQL local

### 2. Configuração Terraform
✅ **Recursos provisionados:**
- VPC com subnets públicas
- Instância EC2 t2.medium (Ubuntu 22.04)
- Security Group com portas 8080, 5432, 22 abertas
- Elastic IP fixo
- Bucket S3 para testes
- Repositório ECR (opcional)
- PostgreSQL instalado localmente na instância

### 3. Problema Identificado
❌ **Aplicação não está implantada no EC2**
- No servidor `ec2-3-142-18-109.us-east-2.compute.amazonaws.com` existe um projeto Python em `/home/ec2-user/backend/`
- O container "pointtils" não existe
- A aplicação PointTils Java não está em execução

## Análise de Problemas

### 1. Incompatibilidade de Credenciais de Banco

**Terraform provisiona:**
```bash
# user_data script configura:
sudo -u postgres psql -c "CREATE USER pointtilsadmin WITH PASSWORD '${var.db_password}';"
```

**Application.properties espera:**
```properties
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:pointtilsadmin}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:password}  # ← SENHA PADRÃO
```

**Solução:** O workflow precisa garantir que as variáveis de ambiente `SPRING_DATASOURCE_PASSWORD` sejam definidas com o mesmo valor de `var.db_password`.

### 2. Problema no Script de Deploy

No passo `Deploy application to EC2` do workflow, o script:

1. ✅ Faz login no ECR
2. ✅ Baixa a imagem Docker
3. ✅ Cria docker-compose.yml
4. ❌ **Mas não implanta a aplicação PointTils corretamente**

O problema é que o script tenta usar `docker-compose up -d` mas não verifica se a aplicação Java foi realmente implantada.

### 3. Configuração de Rede no Docker Compose

O `docker-compose.yml` gerado usa:
```yaml
network_mode: "host"
```

Isso é correto para conectar com PostgreSQL local, mas pode causar conflitos de porta se outras aplicações estiverem rodando.

## Correções Necessárias

### 1. Atualizar o Workflow GitHub Actions

**No passo `Deploy application to EC2`, adicionar verificação:**

```yaml
- name: Verify Application Deployment
  run: |
    # Verificar se a aplicação PointTils está rodando
    ssh ubuntu@${{ steps.terraform-outputs.outputs.app_ip }} \
      "docker ps | grep pointtils && echo '✅ Application running' || echo '❌ Application not found'"
    
    # Verificar se é a aplicação Java correta
    ssh ubuntu@${{ steps.terraform-outputs.outputs.app_ip }} \
      "docker exec $(docker ps -q -f name=pointtils) java -version"
```

### 2. Corrigir Credenciais no Docker Compose

**Atualizar o template do docker-compose.yml no workflow:**

```yaml
environment:
  - SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pointtilsdb
  - SPRING_DATASOURCE_USERNAME=pointtilsadmin
  - SPRING_DATASOURCE_PASSWORD=${{ secrets.DB_PASSWORD }}  # ← Usar a mesma senha
  - JWT_SECRET=${{ secrets.JWT_SECRET }}
```

### 3. Adicionar Cleanup no EC2

**Antes do deploy, remover aplicações existentes:**

```yaml
# No passo Deploy application to EC2, adicionar:
echo "=== Removendo aplicações existentes ==="
sudo rm -rf /home/ec2-user/backend  # Remove projeto Python existente
docker stop $(docker ps -aq) || true
docker rm $(docker ps -aq) || true
```

### 4. Melhorar Verificação de Deploy

**Adicionar health checks robustos:**

```yaml
- name: Verify Application Health
  run: |
    MAX_RETRIES=10
    for i in $(seq 1 $MAX_RETRIES); do
      if curl -s http://${{ steps.terraform-outputs.outputs.app_ip }}:8080/actuator/health | grep -q "UP"; then
        echo "✅ Application health check passed"
        exit 0
      fi
      echo "Attempt $i/$MAX_RETRIES: Application not ready..."
      sleep 15
    done
    echo "❌ Application health check failed after $MAX_RETRIES attempts"
    exit 1
```

## Passos para Corrigir o Deploy Atual

### 1. Executar o Pipeline Corretamente

```bash
# No GitHub, ir para Actions → Deploy to AWS → Run workflow
# Garantir que todas as secrets estão configuradas:
# - AWS_ACCESS_KEY_ID
# - AWS_SECRET_ACCESS_KEY  
# - AWS_REGION
# - DB_USERNAME
# - DB_PASSWORD
# - JWT_SECRET
# - SSH_PRIVATE_KEY
# - SSH_PUBLIC_KEY
```

### 2. Verificar Secrets no GitHub

As seguintes secrets devem estar configuradas no repositório:
- `AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY`
- `AWS_REGION` (us-east-2)
- `DB_USERNAME` e `DB_PASSWORD` 
- `JWT_SECRET` (chave segura)
- `SSH_PRIVATE_KEY` e `SSH_PUBLIC_KEY`

### 3. Execução Manual para Debug

```bash
# Conectar ao EC2 existente
ssh -i chave.pem ec2-user@ec2-3-142-18-109.us-east-2.compute.amazonaws.com

# Parar aplicação existente
sudo systemctl stop postgresql
docker stop $(docker ps -aq)
docker rm $(docker ps -aq)

# Remover projeto Python
rm -rf /home/ec2-user/backend

# Executar Terraform manualmente para recriar a instância
cd pointtils-backend/terraform
terraform destroy  # Cuidado: isso apagará a instância atual
terraform apply
```

## Recomendações para Melhoria

### 1. Usar Banco de Dados Gerenciado
Considerar usar Amazon RDS em vez de PostgreSQL local para melhor confiabilidade.

### 2. Implementar Rolling Deploy
Adicionar estratégia de deploy com zero downtime.

### 3. Melhorar Monitoramento
Adicionar CloudWatch alarms e logs para monitorar a aplicação.

### 4. Usar Variáveis de Ambiente Consistentes
Garantir que todas as credenciais usem as mesmas variáveis em todo o pipeline.

## Conclusão

O pipeline está bem arquitetado mas precisa de ajustes nas credenciais e verificação de deploy. A aplicação existente no EC2 é um projeto Python diferente que precisa ser removida antes do deploy correto da aplicação PointTils Java.

Execute o workflow "Deploy to AWS" no GitHub após configurar todas as secrets necessárias para implantar a aplicação corretamente.
