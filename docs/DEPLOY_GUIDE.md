# Guia de Deploy Corrigido - Pointtils

Este guia descreve os passos para fazer deploy da aplicação Pointtils com a nova abordagem usando containers Docker para o PostgreSQL.

## Problemas Identificados no Deploy Anterior

1. **PostgreSQL local não funcionando**: O PostgreSQL instalado localmente na EC2 não estava iniciando corretamente
2. **Configuração de rede**: Problemas de configuração para aceitar conexões externas
3. **Falta de validação**: Script não verificava se o PostgreSQL realmente estava rodando

## Nova Abordagem Implementada

✅ **Usar PostgreSQL em Container Docker**: Mais consistente com a arquitetura Docker da aplicação
✅ **Script de user_data corrigido**: Com validações e tratamento de erros
✅ **Configuração simplificada**: Remove a complexidade de configurar PostgreSQL local

## Passos para o Deploy

### 1. Preparação do Ambiente

```bash
cd terraform/

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
ssh -i /caminho/para/sua/chave.pem ubuntu@<IP_DA_EC2>
```

### 4. Comandos de Verificação na EC2

```bash
# Verificar se os containers estão rodando
sudo docker-compose -f /home/ubuntu/Backend/docker-compose.prod.yaml ps

# Verificar logs da aplicação
sudo docker-compose -f /home/ubuntu/Backend/docker-compose.prod.yaml logs pointtils

# Verificar logs do banco de dados
sudo docker-compose -f /home/ubuntu/Backend/docker-compose.prod.yaml logs pointtils-db

# Testar conexão com a aplicação
curl http://localhost:8080/actuator/health
```

### 5. Script de Deploy Automatizado

Um script de deploy automatizado está disponível em `/home/ubuntu/deploy-app.sh`:

```bash
# Executar o script de deploy (útil para redeploys)
/home/ubuntu/deploy-app.sh
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
