#!/bin/bash
set -e  # Exit on any error

echo "=== Deploy Simples da Aplicação PointTils para PRODUÇÃO ==="

# Parâmetros recebidos do pipeline
ECR_REGISTRY="${1:-969285065739.dkr.ecr.us-east-2.amazonaws.com}"
AWS_REGION="${2:-us-east-2}"

# Imagens já construídas no GitHub Actions
APP_IMAGE="$ECR_REGISTRY/pointtils:latest"
DB_IMAGE="$ECR_REGISTRY/pointtils-db:latest"

echo "ECR Registry: $ECR_REGISTRY"
echo "App Image: $APP_IMAGE"
echo "AWS Region: $AWS_REGION"
echo "Environment: PRODUCTION"

# Corrigir permissões do Docker
echo "Corrigindo permissões do Docker..."
sudo chown ubuntu:ubuntu /home/ubuntu/.docker -R 2>/dev/null || true
sudo chmod 755 /home/ubuntu/.docker 2>/dev/null || true

# Fazer login no ECR
echo "Fazendo login no ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REGISTRY

# Puxar as imagens mais recentes do ECR
echo "Puxando imagens mais recentes do ECR..."
docker pull $APP_IMAGE
docker pull $DB_IMAGE

# Criar rede Docker se não existir
echo "Criando rede Docker pointtils-network se não existir..."
docker network create pointtils-network 2>/dev/null || true

# Parar e remover containers existentes
echo "Parando containers existentes..."
docker stop pointtils pointtils-db 2>/dev/null || true
docker rm pointtils pointtils-db 2>/dev/null || true

# Criar volume se não existir
echo "Criando volume postgres_data se não existir..."
docker volume create postgres_data 2>/dev/null || true

# Iniciar container do banco de dados
echo "Iniciando container do banco de dados..."
docker run -d \
  --name pointtils-db \
  --hostname pointtils-db \
  --network pointtils-network \
  -p 5432:5432 \
  -v postgres_data:/var/lib/postgresql/data \
  --restart unless-stopped \
  $DB_IMAGE

# Aguardar banco iniciar
echo "Aguardando banco de dados iniciar..."
sleep 30

# Iniciar novo container da aplicação
echo "Iniciando novo container da aplicação..."
docker run -d \
  --name pointtils \
  --network pointtils-network \
  -p 8080:8080 \
  --restart unless-stopped \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://pointtils-db:5432/pointtils-db \
  -e SPRING_PROFILES_ACTIVE=prod \
  $APP_IMAGE

# Aguardar aplicação iniciar
echo "Aguardando aplicação iniciar..."
sleep 30

# Verificar status dos containers
echo "Verificando status dos containers:"
docker ps

# Verificar logs da aplicação
echo "Verificando logs da aplicação:"
docker logs --tail=20 pointtils

# Verificar status final dos containers
echo "Verificando status final dos containers:"
docker ps

# Verificar logs da aplicação
echo "Verificando logs da aplicação:"
docker logs --tail=50 pointtils

echo "=== Deploy concluído! ==="
echo "Aplicação disponível em: http://localhost:8080"
echo "Swagger UI: http://localhost:8080/swagger-ui.html"
echo "Actuator Health: http://localhost:8080/actuator/health"
echo "Para verificar logs: docker logs pointtils"
echo "Para verificar status: docker ps -a"
