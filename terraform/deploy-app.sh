#!/bin/bash
set -e  # Exit on any error

echo "=== Iniciando deploy da aplicação PointTils ==="

# Parâmetros recebidos do pipeline
ECR_REGISTRY="${1:-969285065739.dkr.ecr.us-east-2.amazonaws.com}"
DB_USERNAME="${2:-pointtilsadmin}"
DB_PASSWORD="${3:-password}"
DB_NAME="${4:-pointtils-db}"
JWT_SECRET="${5:-defaultsecretkey1234567890123456789012345678901234}"
AWS_REGION="${6:-us-east-2}"
S3_BUCKET_NAME="${7:-pointtils-api-tests-d9396dcc}"
AWS_ACCESS_KEY_ID="${8}"
AWS_SECRET_ACCESS_KEY="${9}"

APP_IMAGE="$ECR_REGISTRY/pointtils:latest"
DB_IMAGE="$ECR_REGISTRY/pointtils-db:latest"

echo "ECR Registry: $ECR_REGISTRY"
echo "App Image: $APP_IMAGE"
echo "DB Image: $DB_IMAGE"
echo "Database: $DB_NAME"
echo "AWS Region: $AWS_REGION"
echo "S3 Bucket: $S3_BUCKET_NAME"

# Fazer login no ECR
echo "Fazendo login no ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REGISTRY

# Puxar as imagens mais recentes do ECR
echo "Puxando imagens mais recentes do ECR..."
docker pull $APP_IMAGE
docker pull $DB_IMAGE

# Parar e remover containers existentes
echo "Parando containers existentes..."
docker stop pointtils pointtils-db 2>/dev/null || true
docker rm pointtils pointtils-db 2>/dev/null || true

# Remover rede existente se houver
docker network rm pointtils-network 2>/dev/null || true

# Criar rede para os containers
echo "Criando rede para os containers..."
docker network create pointtils-network

# Iniciar container do banco de dados
echo "Iniciando container do banco de dados..."
docker run -d \
  --name pointtils-db \
  --network pointtils-network \
  -e POSTGRES_DB=$DB_NAME \
  -e POSTGRES_USER=$DB_USERNAME \
  -e POSTGRES_PASSWORD=$DB_PASSWORD \
  -p 5432:5432 \
  -v postgres_data:/var/lib/postgresql/data \
  --health-cmd="pg_isready -U $DB_USERNAME -d $DB_NAME" \
  --health-interval=30s \
  --health-timeout=10s \
  --health-retries=3 \
  --health-start-period=40s \
  --restart unless-stopped \
  $DB_IMAGE

# Aguardar banco ficar saudável
echo "Aguardando banco de dados ficar saudável..."
for i in {1..30}; do
  if docker inspect --format='{{.State.Health.Status}}' pointtils-db | grep -q "healthy"; then
    echo "✅ Banco de dados saudável"
    break
  else
    echo "Tentativa $i: Banco ainda não está saudável. Aguardando..."
    sleep 5
  fi
  if [ $i -eq 30 ]; then
    echo "❌ Banco de dados não ficou saudável após 30 tentativas"
    exit 1
  fi
done

# Iniciar container da aplicação
echo "Iniciando container da aplicação..."
docker run -d \
  --name pointtils \
  --network pointtils-network \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://pointtils-db:5432/$DB_NAME \
  -e SPRING_DATASOURCE_USERNAME=$DB_USERNAME \
  -e SPRING_DATASOURCE_PASSWORD=$DB_PASSWORD \
  -e SPRING_APPLICATION_NAME=pointtils-api \
  -e SERVER_PORT=8080 \
  -e JWT_SECRET=$JWT_SECRET \
  -e JWT_EXPIRATION_TIME=3600000 \
  -e JWT_REFRESH_EXPIRATION_TIME=86400000 \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=validate \
  -e SPRING_JPA_SHOW_SQL=false \
  -e SPRINGDOC_API_DOCS_ENABLED=true \
  -e SPRINGDOC_SWAGGER_UI_ENABLED=true \
  -e SPRINGDOC_SWAGGER_UI_PATH=/swagger-ui.html \
  -e CLOUD_AWS_BUCKET_NAME=$S3_BUCKET_NAME \
  -e AWS_REGION=$AWS_REGION \
  -e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID \
  -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \
  -e SPRING_PROFILES_ACTIVE=prod \
  -p 8080:8080 \
  --restart unless-stopped \
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

# Health check
echo "Realizando health check..."
HEALTH_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health || echo "000")
if [ "$HEALTH_RESPONSE" = "200" ]; then
    echo "✅ Health check: SUCCESS (HTTP 200)"
else
    echo "❌ Health check: FAILED (HTTP $HEALTH_RESPONSE)"
    exit 1
fi

echo "=== Deploy concluído com sucesso! ==="
echo "Aplicação disponível em: http://localhost:8080"
echo "Swagger UI: http://localhost:8080/swagger-ui.html"
echo "Actuator Health: http://localhost:8080/actuator/health"
