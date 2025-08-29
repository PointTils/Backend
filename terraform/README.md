# Passos para implantar a aplicação Pointtils na AWS com Terraform

Este guia detalha como implantar a aplicação Pointtils na AWS utilizando Terraform para gerenciar a infraestrutura como código.

## Pré-requisitos

1. Instalar o [Terraform](https://www.terraform.io/downloads.html) (versão 1.0.0 ou superior)
2. Instalar a [AWS CLI](https://aws.amazon.com/cli/) e configurá-la com suas credenciais
3. Criar um par de chaves SSH para acesso às instâncias EC2
4. Ter uma conta AWS com permissões adequadas para criar recursos

## Configuração Inicial

1. **Configurar as credenciais AWS**

   Execute o comando abaixo e siga as instruções:
   ```
   aws configure
   ```

2. **Criar um arquivo terraform.tfvars**

   Crie um arquivo `terraform.tfvars` com base no exemplo fornecido:
   ```
   cp terraform.tfvars.example terraform.tfvars
   ```
   
   Edite o arquivo `terraform.tfvars` com seus próprios valores:
   - `db_username`: nome de usuário para o banco de dados
   - `db_password`: senha para o banco de dados
   - `ssh_public_key`: sua chave pública SSH

## Configuração do Estado Remoto (opcional, mas recomendado)

Para equipes trabalhando no mesmo projeto, é recomendável utilizar estado remoto no S3:

1. **Criar um bucket S3** (se estiver usando o backend S3):
   ```
   aws s3api create-bucket --bucket pointtils-terraform-state --region us-east-1
   ```

2. **Criar uma tabela DynamoDB** para gerenciamento de locks:
   ```
   aws dynamodb create-table \
     --table-name pointtils-terraform-locks \
     --attribute-definitions AttributeName=LockID,AttributeType=S \
     --key-schema AttributeName=LockID,KeyType=HASH \
     --billing-mode PAY_PER_REQUEST \
     --region us-east-1
   ```

## Implantação da Infraestrutura

1. **Inicializar o Terraform**:
   ```
   terraform init
   ```

2. **Verificar o plano de execução**:
   ```
   terraform plan
   ```

3. **Aplicar as mudanças**:
   ```
   terraform apply
   ```
   
   Confirme digitando `yes` quando solicitado.

## Implantando a Aplicação

Após a infraestrutura ser provisionada, você pode implantar a aplicação Pointtils:

1. **Conectar-se à instância EC2**:
   ```
   ssh -i /caminho/para/sua/chave.pem ubuntu@<IP_PUBLICO_DA_INSTANCIA>
   ```
   Onde `<IP_PUBLICO_DA_INSTANCIA>` é o valor do output `app_instance_public_ip`.

2. **Criar um diretório para a aplicação**:
   ```
   mkdir -p ~/pointtils
   cd ~/pointtils
   ```

3. **Criar um arquivo docker-compose.yml**:
   ```
   cat > docker-compose.yml << EOF
   version: '3'
   services:
     app:
       image: <URL_DO_SEU_REGISTRY>/pointtils:latest
       ports:
         - "8080:8080"
       env_file:
         - .env
       restart: always
   EOF
   ```

4. **Fazer login no ECR** (se estiver usando Amazon ECR):
   ```
   aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <URL_DO_SEU_REGISTRY>
   ```

5. **Iniciar a aplicação**:
   ```
   docker-compose up -d
   ```

## Acessando a Aplicação

Após a implantação, a aplicação estará disponível em:

```
http://<IP_PUBLICO_DA_INSTANCIA>:8080
```

## Destruindo a Infraestrutura

Para destruir todos os recursos criados:

```
terraform destroy
```

Confirme digitando `yes` quando solicitado.

## Observações Importantes

- A configuração atual cria uma instância EC2 t2.micro e um banco de dados PostgreSQL db.t3.micro, adequados para ambientes de desenvolvimento ou testes.
- Para ambientes de produção, considere utilizar tipos de instâncias maiores e configurar backups regulares.
- Os grupos de segurança estão configurados para permitir acesso SSH e HTTP de qualquer IP. Para maior segurança, limite o acesso apenas aos IPs necessários.
- Armazene as credenciais do banco de dados com segurança e nunca as inclua em repositórios de código.
