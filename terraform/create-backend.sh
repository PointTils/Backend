#!/bin/bash
set -e  # Exit on any error

echo "=== Criando bucket S3 para backend do Terraform de PRODUÇÃO ==="

AWS_REGION="${1:-us-east-2}"
BUCKET_NAME="pointtils-terraform-state"

echo "AWS Region: $AWS_REGION"
echo "Bucket Name: $BUCKET_NAME"

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
    
    # Permitir acesso público (leitura apenas)
    aws s3api put-public-access-block \
        --bucket "$BUCKET_NAME" \
        --public-access-block-configuration \
            "BlockPublicAcls=false,IgnorePublicAcls=false,BlockPublicPolicy=false,RestrictPublicBuckets=false"
    
    # Configurar política de bucket para permitir acesso público de leitura
    aws s3api put-bucket-policy \
        --bucket "$BUCKET_NAME" \
        --policy '{
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Sid": "PublicReadGetObject",
                    "Effect": "Allow",
                    "Principal": "*",
                    "Action": "s3:GetObject",
                    "Resource": "arn:aws:s3:::'"$BUCKET_NAME"'/*"
                }
            ]
        }'
    
    echo "✅ Bucket S3 '$BUCKET_NAME' criado com sucesso"
fi

echo "=== Backend do Terraform de PRODUÇÃO configurado com sucesso! ==="
echo "Bucket S3: $BUCKET_NAME"
