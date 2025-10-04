#!/bin/bash
set -e  # Exit on any error

echo "=== Deploy Simples da Aplicação PointTils para DESENVOLVIMENTO ==="

# Parâmetros recebidos do pipeline
ECR_REGISTRY="${1:-969285065739.dkr.ecr.us-east-2.amazonaws.com}"
AWS_REGION="${2:-us-east-2}"
DB_USERNAME="${3:-postgres}"
DB_PASSWORD="${4:-postgres}"
DB_NAME="${5:-postgres}"

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

# Iniciar novo container da aplicação com variáveis de ambiente
echo "Iniciando novo container da aplicação de DESENVOLVIMENTO..."
docker run -d \
  --name pointtils-dev \
  --network pointtils-dev-network \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://pointtils-dev-db:5432/$DB_NAME \
  -e SPRING_DATASOURCE_USERNAME=$DB_USERNAME \
  -e SPRING_DATASOURCE_PASSWORD=$DB_PASSWORD \
  -e SPRING_APPLICATION_NAME=pointtils-dev \
  -e SERVER_PORT=8080 \
  -e JWT_SECRET=testandoUmaNovaSenhaMasterComMaisDeTrintaEdoisCaracteres \
  -e JWT_EXPIRATION_TIME=3600000 \
  -e JWT_REFRESH_EXPIRATION_TIME=604800000 \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
  -e SPRING_JPA_SHOW_SQL=true \
  -e SPRING_FLYWAY_ENABLED=true \
  -e SPRING_FLYWAY_LOCATIONS=classpath:db/migration \
  -e SPRING_FLYWAY_BASELINE_ON_MIGRATE=true \
  -e SPRING_FLYWAY_VALIDATE_ON_MIGRATE=true \
  -e SPRINGDOC_API_DOCS_ENABLED=true \
  -e SPRINGDOC_SWAGGER_UI_ENABLED=true \
  -e SPRINGDOC_SWAGGER_UI_PATH=/swagger-ui.html \
  -e AWS_REGION=$AWS_REGION \
  -e AWS_STACK_AUTO=false \
  --restart unless-stopped \
  $APP_IMAGE

# Aguardar aplicação iniciar com verificações robustas
echo "Aguardando aplicação de DESENVOLVIMENTO iniciar..."
for i in {1..10}; do
  # Verificar se o container está rodando
  if docker ps | grep -q pointtils-dev; then
    echo "✅ Container da aplicação está rodando"
    
    # Verificar logs para ver se a aplicação iniciou
    echo "Verificando logs da aplicação (tentativa $i):"
    docker logs --tail=10 pointtils-dev | grep -E "(Started|ERROR|Exception|failed)" || echo "Aguardando inicialização..."
    
    # Tentar health check com timeout
    echo "Tentativa $i: Health check..."
    HEALTH_RESPONSE=$(timeout 10 curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health 2>/dev/null || echo "000")
    
    if [ "$HEALTH_RESPONSE" = "200" ]; then
        echo "✅ Health check de DESENVOLVIMENTO: SUCCESS (HTTP 200)"
        echo "✅ Deploy de DESENVOLVIMENTO concluído com sucesso!"
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
    echo "⚠️  Deploy executado, mas aplicação pode ter problemas de inicialização"
    echo "Verifique os logs manualmente: docker logs pointtils-dev"
    exit 1
  fi
  
  echo "Aguardando 10 segundos antes da próxima tentativa..."
  sleep 10
done

# Verificar status final dos containers
echo "Verificando status final dos containers de DESENVOLVIMENTO:"
docker ps

# Verificar logs finais da aplicação
echo "Verificando logs finais da aplicação de DESENVOLVIMENTO:"
docker logs --tail=30 pointtils-dev

echo "=== Deploy de DESENVOLVIMENTO concluído com sucesso! ==="
echo "Aplicação de DESENVOLVIMENTO disponível em: http://localhost:8080"
echo "Swagger UI: http://localhost:8080/swagger-ui.html"
echo "Actuator Health: http://localhost:8080/actuator/health"
