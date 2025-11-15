#!/bin/bash
set -e  # Exit on any error

echo "=== Deploy Simples da Aplicação PointTils para PRODUÇÃO ==="

# Parâmetros recebidos do pipeline
ECR_REGISTRY="${1:-969285065739.dkr.ecr.us-east-2.amazonaws.com}"
AWS_REGION="${2:-us-east-2}"

# Imagens já construídas no GitHub Actions
APP_IMAGE="$ECR_REGISTRY/pointtils:latest"
DB_IMAGE="$ECR_REGISTRY/pointtils-db:latest"
PROMETHEUS_IMAGE="$ECR_REGISTRY/pointtils-prometheus:latest"
GRAFANA_IMAGE="$ECR_REGISTRY/pointtils-grafana:latest"

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
docker pull $PROMETHEUS_IMAGE
docker pull $GRAFANA_IMAGE

# Criar rede Docker se não existir
echo "Criando rede Docker pointtils-network se não existir..."
docker network create pointtils-network 2>/dev/null || true

# Parar e remover containers existentes
echo "Parando containers existentes..."
docker stop pointtils pointtils-db prometheus grafana 2>/dev/null || true
docker rm pointtils pointtils-db prometheus grafana 2>/dev/null || true

# Remover forçadamente se ainda existirem
echo "Removendo forçadamente se containers ainda existirem..."
docker rm -f pointtils 2>/dev/null || true
docker rm -f pointtils-db 2>/dev/null || true
docker rm -f prometheus 2>/dev/null || true
docker rm -f grafana 2>/dev/null || true

# Criar volumes se não existirem
echo "Criando volumes se não existirem..."
docker volume create postgres_data 2>/dev/null || true
docker volume create prometheus_data 2>/dev/null || true
docker volume create grafana_data 2>/dev/null || true

# Corrigir permissões do volume do Grafana se existir
echo "Corrigindo permissões do volume do Grafana..."
if docker volume inspect grafana_data >/dev/null 2>&1; then
    echo "Volume grafana_data encontrado, corrigindo permissões..."
    # Criar container temporário para corrigir permissões
    docker run --rm -v grafana_data:/var/lib/grafana alpine \
        sh -c "chmod -R 777 /var/lib/grafana && echo 'Permissões do volume grafana_data corrigidas'"
else
    echo "Volume grafana_data não encontrado, será criado automaticamente"
fi

# Iniciar container do banco de dados
echo "Iniciando container do banco de dados..."
docker run -d \
  --name pointtils-db \
  --hostname pointtils-db \
  --network pointtils-network \
  -p 5432:5432 \
  -v postgres_data:/var/lib/postgresql \
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

# Iniciar container do Prometheus
echo "Iniciando container do Prometheus..."
docker run -d \
  --name prometheus \
  --network pointtils-network \
  -p 9090:9090 \
  -v prometheus_data:/prometheus \
  --restart unless-stopped \
  $PROMETHEUS_IMAGE

# Iniciar container do Grafana
echo "Iniciando container do Grafana..."
docker run -d \
  --name grafana \
  --network pointtils-network \
  -p 3000:3000 \
  -v grafana_data:/var/lib/grafana \
  --restart unless-stopped \
  -e GF_SECURITY_ADMIN_USER=admin \
  -e GF_SECURITY_ADMIN_PASSWORD=admin123456 \
  $GRAFANA_IMAGE

# Aguardar serviços de monitoramento iniciarem
echo "Aguardando serviços de monitoramento iniciarem..."
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
echo "Prometheus disponível em: http://localhost:9090"
echo "Grafana disponível em: http://localhost:3000 (admin/admin123456)"
echo "Para verificar logs: docker logs pointtils"
echo "Para verificar status: docker ps -a"
