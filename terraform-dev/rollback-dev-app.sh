#!/bin/bash
set -e  # Exit on any error

echo "=== Iniciando rollback da aplicação PointTils para DESENVOLVIMENTO ==="

# Parâmetros recebidos do pipeline
ECR_REGISTRY="${1:-969285065739.dkr.ecr.us-east-2.amazonaws.com}"
DB_USERNAME="${2:-pointtilsdevadmin}"
DB_PASSWORD="${3:-devpassword123}"
DB_NAME="${4:-pointtils-dev-db}"
JWT_SECRET="${5:-devjwtsecretkey1234567890123456789012345678901234}"
AWS_REGION="${6:-us-east-2}"
S3_BUCKET_NAME="${7:-pointtils-dev-api-tests}"
AWS_ACCESS_KEY_ID="${8}"
AWS_SECRET_ACCESS_KEY="${9}"
ROLLBACK_TAG="${10:-previous}"  # Tag da imagem anterior para rollback

APP_IMAGE="$ECR_REGISTRY/pointtils:$ROLLBACK_TAG"
DB_IMAGE="$ECR_REGISTRY/pointtils-db:$ROLLBACK_TAG"

echo "ECR Registry: $ECR_REGISTRY"
echo "Rollback App Image: $APP_IMAGE"
echo "Rollback DB Image: $DB_IMAGE"
echo "Database: $DB_NAME"
echo "AWS Region: $AWS_REGION"
echo "Rollback Tag: $ROLLBACK_TAG"
echo "Environment: DEVELOPMENT"

# Verificar se as imagens de rollback de DESENVOLVIMENTO existem
echo "Verificando disponibilidade das imagens de rollback de DESENVOLVIMENTO..."
if ! docker pull $APP_IMAGE 2>/dev/null; then
    echo "❌ Imagem de rollback da aplicação de DESENVOLVIMENTO não encontrada: $APP_IMAGE"
    echo "⚠️  Tentando usar imagem 'dev-latest' como fallback..."
    APP_IMAGE="$ECR_REGISTRY/pointtils:dev-latest"
    docker pull $APP_IMAGE
fi

if ! docker pull $DB_IMAGE 2>/dev/null; then
    echo "❌ Imagem de rollback do banco de DESENVOLVIMENTO não encontrada: $DB_IMAGE"
    echo "⚠️  Tentando usar imagem 'dev-latest' como fallback..."
    DB_IMAGE="$ECR_REGISTRY/pointtils-db:dev-latest"
    docker pull $DB_IMAGE
fi

# Fazer login no ECR (caso necessário)
echo "Fazendo login no ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REGISTRY

# Parar e remover containers existentes de DESENVOLVIMENTO
echo "Parando containers de DESENVOLVIMENTO existentes..."
docker stop pointtils-dev pointtils-dev-db 2>/dev/null || true
docker rm pointtils-dev pointtils-dev-db 2>/dev/null || true

# Remover rede existente se houver
docker network rm pointtils-dev-network 2>/dev/null || true

# Criar rede para os containers de DESENVOLVIMENTO
echo "Criando rede para os containers de DESENVOLVIMENTO..."
docker network create pointtils-dev-network

# Iniciar container do banco de dados (rollback de DESENVOLVIMENTO)
echo "Iniciando container do banco de dados (rollback de DESENVOLVIMENTO)..."
docker run -d \
  --name pointtils-dev-db \
  --network pointtils-dev-network \
  -e POSTGRES_DB=$DB_NAME \
  -e POSTGRES_USER=$DB_USERNAME \
  -e POSTGRES_PASSWORD=$DB_PASSWORD \
  -p 5432:5432 \
  -v postgres_dev_data:/var/lib/postgresql/data \
  --health-cmd="pg_isready -U $DB_USERNAME -d $DB_NAME" \
  --health-interval=30s \
  --health-timeout=10s \
  --health-retries=3 \
  --health-start-period=40s \
  --restart unless-stopped \
  $DB_IMAGE

# Aguardar banco ficar saudável
echo "Aguardando banco de dados de DESENVOLVIMENTO ficar saudável..."
for i in {1..30}; do
  if docker inspect --format='{{.State.Health.Status}}' pointtils-dev-db | grep -q "healthy"; then
    echo "✅ Banco de dados de DESENVOLVIMENTO saudável"
    # Testar conexão com as credenciais reais
    echo "Testando conexão com banco de dados de DESENVOLVIMENTO..."
    if docker exec pointtils-dev-db pg_isready -U $DB_USERNAME -d $DB_NAME; then
      echo "✅ Conexão com banco de dados de DESENVOLVIMENTO bem-sucedida"
      break
    else
      echo "❌ Conexão com banco de dados de DESENVOLVIMENTO falhou"
      echo "Credenciais usadas: usuário=$DB_USERNAME, banco=$DB_NAME"
      exit 1
    fi
  else
    echo "Tentativa $i: Banco de DESENVOLVIMENTO ainda não está saudável. Aguardando..."
    sleep 5
  fi
  if [ $i -eq 30 ]; then
    echo "❌ Banco de dados de DESENVOLVIMENTO não ficou saudável após 30 tentativas"
    exit 1
  fi
done

# Iniciar container da aplicação (rollback de DESENVOLVIMENTO)
echo "Iniciando container da aplicação (rollback de DESENVOLVIMENTO)..."
docker run -d \
  --name pointtils-dev \
  --network pointtils-dev-network \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://pointtils-dev-db:5432/$DB_NAME \
  -e SPRING_DATASOURCE_USERNAME=$DB_USERNAME \
  -e SPRING_DATASOURCE_PASSWORD=$DB_PASSWORD \
  -e SPRING_APPLICATION_NAME=pointtils-api-dev \
  -e SERVER_PORT=8080 \
  -e JWT_SECRET=$JWT_SECRET \
  -e JWT_EXPIRATION_TIME=3600000 \
  -e JWT_REFRESH_EXPIRATION_TIME=86400000 \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
  -e SPRING_JPA_SHOW_SQL=true \
  -e SPRINGDOC_API_DOCS_ENABLED=true \
  -e SPRINGDOC_SWAGGER_UI_ENABLED=true \
  -e SPRINGDOC_SWAGGER_UI_PATH=/swagger-ui.html \
  -e CLOUD_AWS_BUCKET_NAME=$S3_BUCKET_NAME \
  -e AWS_REGION=$AWS_REGION \
  -e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID \
  -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e LOGGING_LEVEL_COM_POINTTILS=DEBUG \
  -p 8080:8080 \
  --restart unless-stopped \
  $APP_IMAGE

# Aguardar aplicação iniciar
echo "Aguardando aplicação de DESENVOLVIMENTO iniciar..."
sleep 30

# Verificar status dos containers
echo "Verificando status dos containers de DESENVOLVIMENTO:"
docker ps

# Verificar logs da aplicação
echo "Verificando logs da aplicação de DESENVOLVIMENTO:"
docker logs --tail=20 pointtils-dev

# Health check
echo "Realizando health check após rollback de DESENVOLVIMENTO..."
HEALTH_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health || echo "000")
if [ "$HEALTH_RESPONSE" = "200" ]; then
    echo "✅ Health check de DESENVOLVIMENTO: SUCCESS (HTTP 200)"
    echo "✅ Rollback de DESENVOLVIMENTO concluído com sucesso!"
else
    echo "❌ Health check de DESENVOLVIMENTO: FAILED (HTTP $HEALTH_RESPONSE)"
    echo "❌ Rollback de DESENVOLVIMENTO pode ter problemas. Verifique manualmente."
    exit 1
fi

echo "=== Rollback de DESENVOLVIMENTO concluído! ==="
echo "Aplicação de DESENVOLVIMENTO disponível em: http://localhost:8080"
echo "Swagger UI: http://localhost:8080/swagger-ui.html"
echo "Actuator Health: http://localhost:8080/actuator/health"
