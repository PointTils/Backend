#!/bin/bash

# Script para deploy da aplicação Pointtils usando imagens do ECR

echo "=========================================="
echo "Deploy da aplicação Pointtils"
echo "=========================================="

# Verificar se as variáveis de ambiente necessárias estão definidas
if [ -z "$APP_IMAGE" ] || [ -z "$DB_IMAGE" ]; then
    echo "Erro: Variáveis APP_IMAGE e DB_IMAGE devem ser definidas"
    echo "Exemplo:"
    echo "export APP_IMAGE=123456789012.dkr.ecr.us-east-2.amazonaws.com/pointtils:latest"
    echo "export DB_IMAGE=123456789012.dkr.ecr.us-east-2.amazonaws.com/pointtils-db:latest"
    exit 1
fi

# Verificar se o Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "Erro: Docker não está instalado"
    exit 1
fi

# Verificar se o Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    echo "Erro: Docker Compose não está instalado"
    exit 1
fi

# Verificar se a AWS CLI está instalada
if ! command -v aws &> /dev/null; then
    echo "Erro: AWS CLI não está instalada"
    exit 1
fi

# Verificar se as credenciais da AWS estão configuradas
if ! aws sts get-caller-identity &> /dev/null; then
    echo "Erro: Credenciais da AWS não configuradas"
    exit 1
fi

# Fazer login no ECR
echo "Fazendo login no ECR..."
AWS_REGION=$(echo $APP_IMAGE | cut -d'.' -f4)
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $(echo $APP_IMAGE | cut -d'/' -f1)

# Criar diretório para a aplicação
mkdir -p /home/ubuntu/pointtils
cd /home/ubuntu/pointtils

# Criar arquivo .env se não existir
if [ ! -f .env ]; then
    echo "Criando arquivo .env..."
    cat > .env << EOF
# Database Container Configuration
POSTGRES_USER=pointtilsadmin
POSTGRES_PASSWORD=$(openssl rand -base64 32)
POSTGRES_DB=pointtils-db

# Spring Application Configuration
SPRING_APPLICATION_NAME=pointtils-api
SERVER_PORT=8080

# Spring DataSource Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://pointtils-db:5432/pointtils-db
SPRING_DATASOURCE_USERNAME=pointtilsadmin
SPRING_DATASOURCE_PASSWORD=$(openssl rand -base64 32)

# JPA/Hibernate Configuration
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_SHOW_SQL=true

# JWT Configuration
JWT_SECRET=$(openssl rand -base64 64)
JWT_ISSUER=pointtils-api
JWT_EXPIRATION_TIME=900000
JWT_REFRESH_EXPIRATION_TIME=604800000

# Flyway Configuration
SPRING_FLYWAY_ENABLED=true
SPRING_FLYWAY_LOCATIONS=classpath:db/migration
SPRING_FLYWAY_BASELINE_ON_MIGRATE=true
SPRING_FLYWAY_VALIDATE_ON_MIGRATE=true

# Swagger/OpenAPI Configuration
SPRINGDOC_API_DOCS_ENABLED=true
SPRINGDOC_SWAGGER_UI_ENABLED=true
SPRINGDOC_SWAGGER_UI_PATH=/swagger-ui.html

# AWS Configuration
SPRING_CLOUD_AWS_S3_ENABLED=true
CLOUD_AWS_BUCKET_NAME=pointtils-api-tests
AWS_ACCESS_KEY_ID=$(aws configure get aws_access_key_id)
AWS_SECRET_ACCESS_KEY=$(aws configure get aws_secret_access_key)
AWS_REGION=$AWS_REGION
EOF
    echo "Arquivo .env criado com sucesso!"
fi

# Criar docker-compose.yaml
echo "Criando docker-compose.yaml..."
cat > docker-compose.yaml << EOF
version: '3.8'

services:
  pointtils:
    image: $APP_IMAGE
    container_name: pointtils
    env_file: .env
    ports:
      - "8080:8080"
    depends_on:
      pointtils-db:
        condition: service_healthy
    networks:
      - pointtils-network
    restart: unless-stopped

  pointtils-db:
    image: $DB_IMAGE
    container_name: pointtils-db
    env_file: .env
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - pointtils-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U \${POSTGRES_USER} -d \${POSTGRES_DB}"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    restart: unless-stopped

volumes:
  postgres_data:

networks:
  pointtils-network:
    driver: bridge
EOF

# Parar containers existentes
echo "Parando containers existentes..."
docker-compose down

# Iniciar a aplicação
echo "Iniciando a aplicação..."
docker-compose up -d

# Aguardar aplicação iniciar
echo "Aguardando aplicação iniciar..."
sleep 30

# Verificar status dos containers
echo "Verificando status dos containers:"
docker-compose ps

# Verificar logs
echo "Verificando logs da aplicação:"
docker-compose logs --tail=20 pointtils

# Health check
echo "Realizando health check..."
for i in {1..10}; do
    RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health || echo "000")
    if [ "$RESPONSE" = "200" ]; then
        echo "✅ Aplicação respondendo corretamente (HTTP 200)"
        break
    else
        echo "Tentativa $i: Aplicação não respondeu (HTTP $RESPONSE). Aguardando..."
        sleep 15
    fi
done

echo "=========================================="
echo "Deploy concluído com sucesso!"
echo "=========================================="
echo "Aplicação disponível em: http://localhost:8080"
echo "Swagger UI: http://localhost:8080/swagger-ui.html"
echo "Banco de dados: localhost:5432"
echo "=========================================="
