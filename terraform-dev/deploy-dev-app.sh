#!/bin/bash
set -e  # Exit on any error

echo "=== Deploy Simples da Aplicação PointTils para DESENVOLVIMENTO ==="

# Parâmetros recebidos do pipeline
ECR_REGISTRY="${1:-969285065739.dkr.ecr.us-east-2.amazonaws.com}"
AWS_REGION="${2:-us-east-2}"

APP_IMAGE="$ECR_REGISTRY/pointtils:dev-latest"
DB_IMAGE="$ECR_REGISTRY/pointtils-db:dev-latest"

echo "ECR Registry: $ECR_REGISTRY"
echo "App Image: $APP_IMAGE"
echo "AWS Region: $AWS_REGION"
echo "Environment: DEVELOPMENT"

# Corrigir permissões do Docker
echo "Corrigindo permissões do Docker..."
sudo chown ubuntu:ubuntu /home/ubuntu/.docker -R 2>/dev/null || true
sudo chmod 755 /home/ubuntu/.docker 2>/dev/null || true

# Fazer login no ECR
echo "Fazendo login no ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REGISTRY

# Puxar as imagens mais recentes de DESENVOLVIMENTO do ECR
echo "Puxando imagens de DESENVOLVIMENTO mais recentes do ECR..."
docker pull $APP_IMAGE
docker pull $DB_IMAGE

# Parar e remover containers existentes de DESENVOLVIMENTO
echo "Parando containers de DESENVOLVIMENTO existentes..."
docker stop pointtils-dev pointtils-dev-db 2>/dev/null || true
docker rm pointtils-dev pointtils-dev-db 2>/dev/null || true

# Iniciar container do banco de dados de DESENVOLVIMENTO
echo "Iniciando container do banco de dados de DESENVOLVIMENTO..."
docker run -d \
  --name pointtils-dev-db \
  --network pointtils-dev-network \
  -p 5432:5432 \
  -v postgres_dev_data:/var/lib/postgresql/data \
  --restart unless-stopped \
  $DB_IMAGE

# Aguardar banco iniciar
echo "Aguardando banco de dados de DESENVOLVIMENTO iniciar..."
sleep 30

# Iniciar novo container da aplicação (sem variáveis de ambiente - já estão na imagem)
echo "Iniciando novo container da aplicação de DESENVOLVIMENTO..."
docker run -d \
  --name pointtils-dev \
  --network pointtils-dev-network \
  -p 8080:8080 \
  --restart unless-stopped \
  $APP_IMAGE

# Aguardar aplicação iniciar
echo "Aguardando aplicação de DESENVOLVIMENTO iniciar..."
sleep 30

# Verificar status final dos containers
echo "Verificando status final dos containers de DESENVOLVIMENTO:"
docker ps

# Verificar logs da aplicação
echo "Verificando logs da aplicação de DESENVOLVIMENTO:"
docker logs --tail=50 pointtils-dev

echo "=== Deploy de DESENVOLVIMENTO concluído! ==="
echo "Aplicação de DESENVOLVIMENTO disponível em: http://localhost:8080"
echo "Swagger UI: http://localhost:8080/swagger-ui.html"
echo "Actuator Health: http://localhost:8080/actuator/health"
echo "Para verificar logs: docker logs pointtils-dev"
echo "Para verificar status: docker ps -a"
