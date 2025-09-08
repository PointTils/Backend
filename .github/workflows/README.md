# Configuração do GitHub Actions para Deploy na AWS

Este documento descreve a configuração necessária para o GitHub Actions fazer o deploy da aplicação Pointtils na AWS usando Terraform.

## Segredos do GitHub (GitHub Secrets)

Para que a pipeline funcione corretamente, é necessário configurar os seguintes segredos no repositório do GitHub:

1. **`AWS_ROLE_TO_ASSUME`**: ARN do papel IAM que o GitHub Actions vai assumir para acessar os recursos da AWS
   - Exemplo: `arn:aws:iam::123456789012:role/github-actions-role`

2. **`AWS_REGION`**: Região da AWS onde a aplicação será implantada
   - Exemplo: `us-east-1`

3. **`ECR_REPOSITORY`**: Nome do repositório ECR onde as imagens Docker serão armazenadas
   - Exemplo: `pointtils`

4. **`DB_USERNAME`**: Nome de usuário do banco de dados PostgreSQL
   - Exemplo: `pointtilsadmin`

5. **`DB_PASSWORD`**: Senha do banco de dados PostgreSQL
   - Exemplo: `SenhaSegura123!`

6. **`SSH_PUBLIC_KEY`**: Chave SSH pública para acesso às instâncias EC2
   - Exemplo: `ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC...`

7. **`SSH_PRIVATE_KEY`**: Chave SSH privada para que o GitHub Actions possa se conectar à instância EC2
   - Deve ser a chave privada correspondente à chave pública fornecida

8. **`TF_API_TOKEN`**: (Opcional) Token da API do Terraform Cloud, se estiver usando Terraform Cloud

## Configuração do IAM na AWS

### Criar um papel IAM para o GitHub Actions

1. Acesse o console da AWS e navegue até o serviço IAM
2. Vá para "Roles" e clique em "Create role"
3. Escolha "Web identity" como tipo de entidade confiável
4. Em "Identity provider", selecione "Token.Actions.githubusercontent.com"
5. Configure as seguintes condições:
   - Audience: `sts.amazonaws.com`
   - Subject: `repo:PointTils/Backend:ref:refs/heads/main`
6. Clique em "Next"
7. Anexe as seguintes políticas gerenciadas:
   - `AmazonECR-FullAccess`
   - `AmazonEC2FullAccess`
   - `AmazonRDSFullAccess`
   - `AmazonS3FullAccess` (para armazenamento do estado do Terraform)
   - `AmazonDynamoDBFullAccess` (para gerenciamento de locks do Terraform)
   - `AmazonVPCFullAccess`
8. Nomeie o papel (por exemplo, `github-actions-pointtils-role`) e crie-o
9. Anote o ARN do papel criado, pois você precisará dele para configurar o segredo `AWS_ROLE_TO_ASSUME` no GitHub

### Criar um repositório ECR

1. Acesse o console da AWS e navegue até o serviço ECR
2. Clique em "Create repository"
3. Nomeie o repositório (por exemplo, `pointtils`)
4. Configure as demais opções conforme necessário
5. Anote o nome do repositório para configurar o segredo `ECR_REPOSITORY` no GitHub

## Preparação do Terraform State Remoto

É recomendável configurar o armazenamento remoto do estado do Terraform para trabalhar em equipe:

1. **Criar um bucket S3**:
   ```
   aws s3api create-bucket --bucket pointtils-terraform-state --region us-east-1
   ```

2. **Habilitar versionamento no bucket S3**:
   ```
   aws s3api put-bucket-versioning --bucket pointtils-terraform-state --versioning-configuration Status=Enabled
   ```

3. **Criar uma tabela DynamoDB** para gerenciamento de locks:
   ```
   aws dynamodb create-table \
     --table-name pointtils-terraform-locks \
     --attribute-definitions AttributeName=LockID,AttributeType=S \
     --key-schema AttributeName=LockID,KeyType=HASH \
     --billing-mode PAY_PER_REQUEST \
     --region us-east-1
   ```

## Como a Pipeline Funciona

1. Quando ocorre um push na branch `main` ou quando acionada manualmente:
   - Compila o código Java da aplicação usando Maven
   - Constrói a imagem Docker e a envia para o ECR
   - Inicializa o Terraform e aplica a configuração de infraestrutura
   - Conecta-se à instância EC2 criada e implanta a aplicação usando Docker Compose

2. Ao final do processo, a aplicação estará disponível no endereço:
   ```
   http://<IP_DA_INSTANCIA_EC2>:8080
   ```

## Observações Importantes

- Certifique-se de que a infraestrutura atual foi destruída antes de executar a pipeline, para evitar conflitos
- Para ambientes de produção, considere adicionar etapas de teste e ambientes de homologação
- O Terraform State remoto é essencial para trabalho em equipe e para evitar conflitos de infraestrutura
- Avalie cuidadosamente as permissões IAM para seguir o princípio do privilégio mínimo
