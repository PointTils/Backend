# Guia de Deploy - Pointtils

Este guia descreve os passos para fazer deploy da aplicação Pointtils usando CI/CD automatizado com GitHub Actions e Terraform.

## Arquitetura de Deploy

### Ambientes Implementados

1. **Desenvolvimento (dev)**: 
   - Trigger: Push para branches `dev` e `feature/*`
   - Instância EC2: `t2.medium`
   - VPC: `10.1.0.0/16`
   - ECR: `pointtils-dev`

2. **Produção (main)**:
   - Trigger: Push para `main` e PRs fechados
   - Instância EC2: `t2.medium` 
   - VPC: `10.0.0.0/16`
   - ECR: `pointtils`

## Deploy Automatizado via CI/CD

### Para Desenvolvimento

O deploy para desenvolvimento é totalmente automatizado:

1. **Push para branch `dev` ou `feature/*`**
2. **GitHub Actions executa**:
   - Build e testes da aplicação
   - Push das imagens para ECR
   - Deploy da infraestrutura com Terraform
   - Deploy das imagens na EC2
   - Health check automático

### Para Produção

1. **Merge para branch `main`**
2. **GitHub Actions executa**:
   - Build e testes da aplicação
   - Push das imagens para ECR
   - Deploy da infraestrutura com Terraform
   - Deploy das imagens na EC2
   - Health check automático

## Deploy Manual (Se Necessário)

### 1. Preparação do Ambiente

```bash
# Para produção
cd terraform/

# Para desenvolvimento  
cd terraform-dev/

# Verificar/atualizar as variáveis no terraform.tfvars
nano terraform.tfvars
```

### 2. Executar o Terraform

```bash
# Inicializar o Terraform
terraform init

# Verificar o plano de execução
terraform plan -out=tfplan

# Aplicar as mudanças
terraform apply tfplan
```

### 3. Verificar o Deploy

Após a execução do Terraform, conecte-se na EC2:

```bash
# Conectar na instância EC2 (substitua pelo IP correto)
ssh -i ssh-key-dev/pointtils-dev-key.pem ubuntu@<IP_DA_EC2>
```

### 4. Comandos de Verificação na EC2

```bash
# Verificar se os containers estão rodando
sudo docker-compose -f /home/ubuntu/pointtils/docker-compose.yaml ps

# Verificar logs da aplicação
sudo docker-compose -f /home/ubuntu/pointtils/docker-compose.yaml logs pointtils

# Verificar logs do banco de dados
sudo docker-compose -f /home/ubuntu/pointtils/docker-compose.yaml logs pointtils-db

# Testar conexão com a aplicação
curl http://localhost:8080/actuator/health
```

### 5. Scripts de Deploy Automatizado

Scripts disponíveis para deploy manual:

```bash
# Para produção
./terraform/deploy-app.sh

# Para desenvolvimento
./terraform-dev/deploy-dev-app.sh
```

## Estrutura da Nova Solução

### Arquivos Modificados

1. **`terraform/main.tf`**: Script de user_data completamente reformulado
2. **`terraform/deploy-app.sh`**: Script de deploy automatizado
3. **`docker-compose.prod.yaml`**: Já configurado corretamente para produção

### Configuração de Rede

- **Aplicação**: Exposta na porta 8080
- **PostgreSQL**: Exposto na porta 5432 (apenas para a aplicação)
- **Security Group**: Configurado para permitir tráfego nas portas 8080, 5432 e 22

### Variáveis de Ambiente

O arquivo `.env` é automaticamente criado com:

```env
POSTGRES_USER=seu_usuario
POSTGRES_PASSWORD=sua_senha  
POSTGRES_DB=pointtils
SPRING_DATASOURCE_URL=jdbc:postgresql://pointtils-db:5432/pointtils
SPRING_DATASOURCE_USERNAME=seu_usuario
SPRING_DATASOURCE_PASSWORD=sua_senha
```

## Troubleshooting

### Problemas Comuns e Soluções

1. **PostgreSQL não inicia**:
   ```bash
   sudo docker-compose -f docker-compose.prod.yaml logs pointtils-db
   ```

2. **Aplicação não conecta no banco**:
   ```bash
   sudo docker-compose -f docker-compose.prod.yaml logs pointtils
   ```

3. **Build falha**:
   ```bash
   sudo docker-compose -f docker-compose.prod.yaml build --no-cache
   ```

4. **Redeploy completo**:
   ```bash
   sudo docker-compose -f docker-compose.prod.yaml down --volumes --remove-orphans
   sudo docker-compose -f docker-compose.prod.yaml up -d --build
   ```

### Comandos Úteis

```bash
# Parar todos os containers
sudo docker-compose -f docker-compose.prod.yaml down

# Reiniciar a aplicação
sudo docker-compose -f docker-compose.prod.yaml restart pointtils

# Verificar uso de recursos
sudo docker system df
sudo docker stats

# Limpar recursos não utilizados
sudo docker system prune -f
sudo docker volume prune -f
```

## Monitoramento

A aplicação inclui endpoints de monitoramento:

- **Health Check**: `http://<IP_DA_EC2>:8080/actuator/health`
- **Metrics**: `http://<IP_DA_EC2>:8080/actuator/metrics`
- **Swagger UI**: `http://<IP_DA_EC2>:8080/swagger-ui.html`

## Considerações de Segurança

- ✅ PostgreSQL rodando em container isolado
- ✅ Conexões apenas entre containers na mesma network
- ✅ Security Group restritivo
- ✅ Variáveis sensíveis em arquivo .env
- ✅ Não expõe PostgreSQL para internet externa

Esta solução resolve os problemas de conexão com o banco de dados e fornece uma base mais robusta para deploy em produção.
