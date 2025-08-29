#!/bin/bash

# Script para facilitar a implantação do Pointtils na AWS

echo "=========================================="
echo "Implantação do Pointtils na AWS"
echo "=========================================="

# Verificar se o Terraform está instalado
if ! command -v terraform &> /dev/null; then
    echo "Erro: Terraform não está instalado. Por favor, instale o Terraform primeiro."
    exit 1
fi

# Verificar se a AWS CLI está instalada
if ! command -v aws &> /dev/null; then
    echo "Erro: AWS CLI não está instalada. Por favor, instale a AWS CLI primeiro."
    exit 1
fi

# Verificar se as credenciais da AWS estão configuradas
if ! aws sts get-caller-identity &> /dev/null; then
    echo "Erro: Credenciais da AWS não configuradas. Execute 'aws configure' primeiro."
    exit 1
fi

# Criar terraform.tfvars se não existir
if [ ! -f terraform.tfvars ]; then
    echo "Criando arquivo terraform.tfvars..."
    echo "Defina a senha do banco de dados:"
    read -s db_password
    echo "Defina o nome de usuário do banco de dados [pointtilsadmin]:"
    read db_username
    db_username=${db_username:-pointtilsadmin}
    
    echo "Informe o caminho para sua chave pública SSH:"
    read ssh_key_path
    
    if [ ! -f "$ssh_key_path" ]; then
        echo "Erro: Arquivo de chave não encontrado. Verifique o caminho e tente novamente."
        exit 1
    fi
    
    ssh_public_key=$(cat "$ssh_key_path")
    
    cat > terraform.tfvars << EOF
aws_region     = "us-east-1"
db_username    = "$db_username"
db_password    = "$db_password"
ssh_public_key = "$ssh_public_key"
EOF
    
    echo "Arquivo terraform.tfvars criado com sucesso!"
fi

# Inicializar o Terraform
echo "Inicializando o Terraform..."
terraform init

# Criar plano de execução
echo "Criando plano de execução..."
terraform plan -out=tfplan

# Perguntar se deseja aplicar o plano
echo "Deseja aplicar o plano de infraestrutura? (s/n)"
read aplicar

if [ "$aplicar" == "s" ] || [ "$aplicar" == "S" ]; then
    echo "Aplicando plano de infraestrutura..."
    terraform apply tfplan
    
    # Extrair informações dos outputs
    app_ip=$(terraform output -raw app_instance_public_ip)
    db_endpoint=$(terraform output -raw database_endpoint)
    
    echo "=========================================="
    echo "Implantação concluída com sucesso!"
    echo "=========================================="
    echo "IP da aplicação: $app_ip"
    echo "Endpoint do banco de dados: $db_endpoint"
    echo ""
    echo "Para acessar a instância EC2:"
    echo "ssh -i /caminho/para/sua/chave.pem ubuntu@$app_ip"
    echo ""
    echo "A aplicação estará disponível em:"
    echo "http://$app_ip:8080"
    echo "=========================================="
else
    echo "Operação cancelada pelo usuário."
fi
