# Novo Ambiente de Desenvolvimento PointTils

Este diretório contém a configuração Terraform para um **NOVO ambiente de desenvolvimento completamente separado** do ambiente existente "pointtils-app".

## 🎯 Objetivo

Criar um ambiente de desenvolvimento isolado que **NÃO interfere** com a instância EC2 existente "pointtils-app" que está em execução.

## 🔧 Principais Diferenças

### Recursos Únicos
- **VPC**: `10.2.0.0/16` (diferente do ambiente existente `10.1.0.0/16`)
- **Instância EC2**: `pointtils-dev-app` (diferente de `pointtils-app`)
- **Banco de Dados**: `pointtils-dev-db` (separado)
- **Bucket S3**: `pointtils-dev-api-tests-*` (separado)
- **Security Groups**: `pointtils-dev-app-sg` (separado)
- **IAM Roles**: `pointtils-dev-ec2-role` (separado)

### Configurações de Rede
- **CIDR da VPC**: `10.2.0.0/16`
- **Subnet 1**: `10.2.1.0/24` (us-east-2a)
- **Subnet 2**: `10.2.2.0/24` (us-east-2b)

### Configurações da Aplicação
- **Nome da aplicação**: `pointtils-api-dev`
- **Profile Spring**: `dev`
- **Container names**: `pointtils-dev` e `pointtils-dev-db`
- **Network Docker**: `pointtils-dev-network`

## 🚀 Como Usar

### 1. Configurar Credenciais AWS
```bash
# Usar o script de configuração
./aws-config.sh
```

### 2. Criar Chaves SSH (se necessário)
```bash
# Gerar novas chaves SSH para o novo ambiente
ssh-keygen -t rsa -b 4096 -f pointtils_dev_key -N ""
```

### 3. Inicializar Terraform
```bash
cd terraform-dev
terraform init
```

### 4. Planejar e Aplicar
```bash
# Verificar o que será criado
terraform plan

# Aplicar a infraestrutura
terraform apply
```

## 📋 Recursos Criados

- ✅ **VPC** com subnets públicas
- ✅ **Internet Gateway** e **Route Tables**
- ✅ **Security Groups** para aplicação
- ✅ **IAM Roles** e **Policies**
- ✅ **Instância EC2** t2.micro
- ✅ **Elastic IP** para acesso público
- ✅ **Bucket S3** para testes de API
- ✅ **Script de inicialização** com Docker e Docker Compose

## 🔒 Segurança

- Todas as chaves SSH são separadas
- Security Groups com regras específicas
- Bucket S3 com acesso privado
- IAM Roles com permissões mínimas necessárias

## 🌐 Acesso

Após o deploy, a aplicação estará disponível em:
```
http://[IP_PUBLICO_EC2]:8080
```

Swagger UI:
```
http://[IP_PUBLICO_EC2]:8080/swagger-ui.html
```

SSH:
```bash
ssh -i pointtils_dev_key ubuntu@[IP_PUBLICO_EC2]
```

## ⚠️ Importante

Este ambiente é **completamente independente** do ambiente existente. Você pode:
- ✅ Executar este Terraform sem afetar a instância "pointtils-app"
- ✅ Ter múltiplos ambientes de desenvolvimento simultaneamente
- ✅ Testar novas funcionalidades sem risco para o ambiente principal

## 🗑️ Destruir Ambiente

```bash
cd terraform-dev
terraform destroy
```

Isso removerá **apenas** os recursos deste ambiente específico, mantendo o ambiente "pointtils-app" intacto.
