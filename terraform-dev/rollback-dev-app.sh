#!/bin/bash
set -e  # Exit on any error

echo "=== Rollback Simples da Aplicação PointTils para DESENVOLVIMENTO ==="

# Parâmetros recebidos do pipeline
ECR_REGISTRY="${1:-969285065739.dkr.ecr.us-east-2.amazonaws.com}"
AWS_REGION="${2:-us-east-2}"
ROLLBACK_TAG="${3:-previous}"  # Tag da imagem anterior para rollback

APP_IMAGE="$ECR_REGISTRY/pointtils-dev:$ROLLBACK_TAG"
DB_IMAGE="$ECR_REGISTRY/pointtils-dev-db:$ROLLBACK_TAG"
PROMETHEUS_IMAGE="$ECR_REGISTRY/pointtils-dev-prometheus:$ROLLBACK_TAG"
GRAFANA_IMAGE="$ECR_REGISTRY/pointtils-dev-grafana:$ROLLBACK_TAG"

echo "ECR Registry: $ECR_REGISTRY"
echo "Rollback App Image: $APP_IMAGE"
echo "Rollback DB Image: $DB_IMAGE"
echo "Rollback Prometheus Image: $PROMETHEUS_IMAGE"
echo "Rollback Grafana Image: $GRAFANA_IMAGE"
echo "AWS Region: $AWS_REGION"
echo "Rollback Tag: $ROLLBACK_TAG"
echo "Environment: DEVELOPMENT"

# Corrigir permissões do Docker
echo "Corrigindo permissões do Docker..."
sudo chown ubuntu:ubuntu /home/ubuntu/.docker -R 2>/dev/null || true
sudo chmod 755 /home/ubuntu/.docker 2>/dev/null || true

# Fazer login no ECR
echo "Fazendo login no ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REGISTRY

# Verificar se as imagens de rollback existem, caso contrário usar dev-latest
echo "Verificando disponibilidade das imagens de rollback de DESENVOLVIMENTO..."
if ! docker pull $APP_IMAGE 2>/dev/null; then
    echo "❌ Imagem de rollback da aplicação de DESENVOLVIMENTO não encontrada: $APP_IMAGE"
    echo "⚠️  Usando imagem 'dev-latest' como fallback..."
    APP_IMAGE="$ECR_REGISTRY/pointtils-dev:dev-latest"
    docker pull $APP_IMAGE
fi

if ! docker pull $DB_IMAGE 2>/dev/null; then
    echo "❌ Imagem de rollback do banco de DESENVOLVIMENTO não encontrada: $DB_IMAGE"
    echo "⚠️  Usando imagem 'dev-latest' como fallback..."
    DB_IMAGE="$ECR_REGISTRY/pointtils-dev-db:dev-latest"
    docker pull $DB_IMAGE
fi

# Verificar imagens de monitoramento (opcional - não falhar se não existirem)
echo "Verificando imagens de monitoramento de DESENVOLVIMENTO..."
if docker pull $PROMETHEUS_IMAGE 2>/dev/null; then
    echo "✅ Imagem de rollback do Prometheus encontrada: $PROMETHEUS_IMAGE"
else
    echo "⚠️  Imagem de rollback do Prometheus não encontrada, usando 'dev-latest'..."
    PROMETHEUS_IMAGE="$ECR_REGISTRY/pointtils-dev-prometheus:dev-latest"
    docker pull $PROMETHEUS_IMAGE || echo "⚠️  Não foi possível puxar imagem do Prometheus, continuando sem rollback de monitoramento"
fi

if docker pull $GRAFANA_IMAGE 2>/dev/null; then
    echo "✅ Imagem de rollback do Grafana encontrada: $GRAFANA_IMAGE"
else
    echo "⚠️  Imagem de rollback do Grafana não encontrada, usando 'dev-latest'..."
    GRAFANA_IMAGE="$ECR_REGISTRY/pointtils-dev-grafana:dev-latest"
    docker pull $GRAFANA_IMAGE || echo "⚠️  Não foi possível puxar imagem do Grafana, continuando sem rollback de monitoramento"
fi

# Parar e remover containers existentes de DESENVOLVIMENTO
echo "Parando containers de DESENVOLVIMENTO existentes..."
docker stop pointtils-dev pointtils-db-dev prometheus grafana 2>/dev/null || true
docker rm pointtils-dev pointtils-db-dev prometheus grafana 2>/dev/null || true

# Remover forçadamente se ainda existirem
echo "Removendo forçadamente se containers ainda existirem..."
docker rm -f pointtils-dev 2>/dev/null || true
docker rm -f pointtils-db-dev 2>/dev/null || true
docker rm -f prometheus 2>/dev/null || true
docker rm -f grafana 2>/dev/null || true

# Verificar e liberar portas em conflito
echo "Verificando e liberando portas em conflito..."
PORTS_TO_CHECK="5432 8080 9090 3000"
for PORT in $PORTS_TO_CHECK; do
  CONTAINER_USING_PORT=$(docker ps -q --filter "publish=$PORT")
  if [ -n "$CONTAINER_USING_PORT" ]; then
    echo "Parando container usando porta $PORT: $CONTAINER_USING_PORT"
    docker stop $CONTAINER_USING_PORT 2>/dev/null || true
    docker rm $CONTAINER_USING_PORT 2>/dev/null || true
  fi
done

# Iniciar container do banco de dados (rollback de DESENVOLVIMENTO)
echo "Iniciando container do banco de dados (rollback de DESENVOLVIMENTO)..."
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

# Iniciar container de rollback da aplicação
echo "Iniciando container de rollback da aplicação de DESENVOLVIMENTO..."
docker run -d \
  --name pointtils-dev \
  --network pointtils-dev-network \
  -p 8080:8080 \
  --restart unless-stopped \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://pointtils-db-dev:5432/pointtils-db \
  -e SPRING_PROFILES_ACTIVE=prod \
  $APP_IMAGE

# Aguardar aplicação iniciar com verificações robustas
echo "Aguardando aplicação de DESENVOLVIMENTO iniciar após rollback..."
for i in {1..10}; do
  # Verificar se o container está rodando
  if docker ps | grep -q pointtils-dev; then
    echo "✅ Container da aplicação está rodando"
    
    # Tentar health check com timeout
    echo "Tentativa $i: Health check..."
    HEALTH_RESPONSE=$(timeout 10 curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health 2>/dev/null || echo "000")
    
    if [ "$HEALTH_RESPONSE" = "200" ]; then
        echo "✅ Health check de DESENVOLVIMENTO: SUCCESS (HTTP 200)"
        echo "✅ Rollback de DESENVOLVIMENTO concluído com sucesso!"
        break
    elif [ "$HEALTH_RESPONSE" != "000" ]; then
        echo "⚠️  Health check retornou HTTP $HEALTH_RESPONSE - aplicação pode estar iniciando"
    else
        echo "❌ Health check falhou (sem resposta) - aplicação ainda não está respondendo"
    fi
  else
    echo "❌ Container da aplicação não está rodando"
    exit 1
  fi
  
  if [ $i -eq 10 ]; then
    echo "❌ Health check de DESENVOLVIMENTO: FAILED após 10 tentativas"
    echo "⚠️  Rollback executado, mas aplicação pode ter problemas de inicialização"
    echo "Verifique os logs manualmente: docker logs pointtils-dev"
    exit 1
  fi
  
  echo "Aguardando 10 segundos antes da próxima tentativa..."
  sleep 10
done

# Reiniciar containers de monitoramento se as imagens foram encontradas
echo "Reiniciando containers de monitoramento de DESENVOLVIMENTO..."
if docker images | grep -q "pointtils-dev-prometheus"; then
    echo "Iniciando container do Prometheus (rollback)..."
    docker run -d \
      --name prometheus \
      --network pointtils-dev-network \
      -p 9090:9090 \
      -v prometheus_data:/prometheus \
      --restart unless-stopped \
      $PROMETHEUS_IMAGE
fi

if docker images | grep -q "pointtils-dev-grafana"; then
    echo "Iniciando container do Grafana (rollback)..."
    docker run -d \
      --name grafana \
      --network pointtils-dev-network \
      -p 3000:3000 \
      -v grafana_data:/var/lib/grafana \
      --restart unless-stopped \
      -e GF_SECURITY_ADMIN_USER=admin \
      -e GF_SECURITY_ADMIN_PASSWORD=admin123456 \
      $GRAFANA_IMAGE
fi

# Aguardar serviços de monitoramento iniciarem
echo "Aguardando serviços de monitoramento de DESENVOLVIMENTO iniciarem..."
sleep 20

# Verificar status final dos containers
echo "Verificando status final dos containers de DESENVOLVIMENTO:"
docker ps

echo "=== Rollback de DESENVOLVIMENTO concluído! ==="
echo "Aplicação de DESENVOLVIMENTO disponível em: http://localhost:8080"
echo "Swagger UI: http://localhost:8080/swagger-ui.html"
echo "Actuator Health: http://localhost:8080/actuator/health"
echo "Prometheus disponível em: http://localhost:9090"
echo "Grafana disponível em: http://localhost:3000 (admin/admin123456)"
