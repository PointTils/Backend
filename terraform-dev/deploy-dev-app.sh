#!/bin/bash
set -e  # Exit on any error

echo "=== Deploy Simples da Aplicação PointTils para DESENVOLVIMENTO ==="

# Parâmetros recebidos do pipeline
ECR_REGISTRY="${1:-969285065739.dkr.ecr.us-east-2.amazonaws.com}"
AWS_REGION="${2:-us-east-2}"

# Imagens já construídas no GitHub Actions
APP_IMAGE="$ECR_REGISTRY/pointtils-dev:dev-latest"
DB_IMAGE="$ECR_REGISTRY/pointtils-dev-db:dev-latest"

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

# Verificar o conteúdo da imagem de aplicação
echo "Verificando a imagem da aplicação..."
docker inspect --format='{{.Config.Cmd}}' $APP_IMAGE
echo "Verificando a imagem do banco de dados..."
docker inspect --format='{{.Config.Cmd}}' $DB_IMAGE

# Criar rede Docker se não existir
echo "Criando rede Docker pointtils-dev-network se não existir..."
docker network create pointtils-dev-network 2>/dev/null || true

# Parar e remover containers existentes de DESENVOLVIMENTO
echo "Parando containers de DESENVOLVIMENTO existentes..."
docker stop pointtils-dev pointtils-db-dev 2>/dev/null || true
docker rm pointtils-dev pointtils-db-dev 2>/dev/null || true

# Remover forçadamente se ainda existirem
echo "Removendo forçadamente se containers ainda existirem..."
docker rm -f pointtils-dev 2>/dev/null || true
docker rm -f pointtils-db-dev 2>/dev/null || true

# Criar volume se não existir
echo "Criando volume postgres_dev_data se não existir..."
docker volume create postgres_dev_data 2>/dev/null || true

# Iniciar container do banco de dados de DESENVOLVIMENTO
echo "Iniciando container do banco de dados de DESENVOLVIMENTO..."
docker run -d \
  --name pointtils-db-dev \
  --hostname pointtils-db-dev \
  --network pointtils-dev-network \
  -p 5432:5432 \
  -v postgres_dev_data:/var/lib/postgresql \
  --restart unless-stopped \
  $DB_IMAGE

# Aguardar banco iniciar
echo "Aguardando banco de dados de DESENVOLVIMENTO iniciar..."
sleep 30

# Iniciar novo container da aplicação
echo "Iniciando novo container da aplicação de DESENVOLVIMENTO..."
docker run -d \
  --name pointtils-dev \
  --network pointtils-dev-network \
  -p 8080:8080 \
  --restart unless-stopped \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://pointtils-db-dev:5432/pointtils-db \
  -e SPRING_PROFILES_ACTIVE=prod \
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
