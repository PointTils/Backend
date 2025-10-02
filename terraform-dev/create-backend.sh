#!/bin/bash
set -e  # Exit on any error

echo "=== Criando bucket S3 para backend do Terraform de desenvolvimento ==="

AWS_REGION="${1:-us-east-2}"
BUCKET_NAME="pointtils-terraform-state-dev"
DYNAMODB_TABLE="pointtils-terraform-locks-dev"

echo "AWS Region: $AWS_REGION"
echo "Bucket Name: $BUCKET_NAME"
echo "DynamoDB Table: $DYNAMODB_TABLE"

# Verificar se o bucket já existe
echo "Verificando se o bucket S3 já existe..."
if aws s3api head-bucket --bucket "$BUCKET_NAME" --region "$AWS_REGION" 2>/dev/null; then
    echo "✅ Bucket S3 '$BUCKET_NAME' já existe"
else
    echo "Criando bucket S3 '$BUCKET_NAME'..."
    
    # Criar bucket S3
    aws s3api create-bucket \
        --bucket "$BUCKET_NAME" \
        --region "$AWS_REGION" \
        --create-bucket-configuration LocationConstraint="$AWS_REGION"
    
    # Habilitar versionamento
    aws s3api put-bucket-versioning \
        --bucket "$BUCKET_NAME" \
        --versioning-configuration Status=Enabled
    
    # Habilitar criptografia
    aws s3api put-bucket-encryption \
        --bucket "$BUCKET_NAME" \
        --server-side-encryption-configuration '{
            "Rules": [
                {
                    "ApplyServerSideEncryptionByDefault": {
                        "SSEAlgorithm": "AES256"
                    }
                }
            ]
        }'
    
    # Bloquear acesso público
    aws s3api put-public-access-block \
        --bucket "$BUCKET_NAME" \
        --public-access-block-configuration \
            "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"
    
    echo "✅ Bucket S3 '$BUCKET_NAME' criado com sucesso"
fi

# Verificar se a tabela DynamoDB já existe
echo "Verificando se a tabela DynamoDB já existe..."
if aws dynamodb describe-table --table-name "$DYNAMODB_TABLE" --region "$AWS_REGION" 2>/dev/null; then
    echo "✅ Tabela DynamoDB '$DYNAMODB_TABLE' já existe"
else
    echo "Criando tabela DynamoDB '$DYNAMODB_TABLE'..."
    
    # Criar tabela DynamoDB para locks
    aws dynamodb create-table \
        --table-name "$DYNAMODB_TABLE" \
        --attribute-definitions AttributeName=LockID,AttributeType=S \
        --key-schema AttributeName=LockID,KeyType=HASH \
        --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
        --region "$AWS_REGION"
    
    # Aguardar tabela ficar ativa
    echo "Aguardando tabela DynamoDB ficar ativa..."
    aws dynamodb wait table-exists --table-name "$DYNAMODB_TABLE" --region "$AWS_REGION"
    
    echo "✅ Tabela DynamoDB '$DYNAMODB_TABLE' criada com sucesso"
fi

echo "=== Backend do Terraform configurado com sucesso! ==="
echo "Bucket S3: $BUCKET_NAME"
echo "DynamoDB Table: $DYNAMODB_TABLE"
echo "Agora você pode executar 'terraform init'"
