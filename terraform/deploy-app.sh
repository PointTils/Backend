#!/bin/bash
set -e  # Exit on any error

echo "=== Script de Deploy Automatizado para Pointtils ==="

# Configurações
APP_DIR="/home/ubuntu/Backend"
ENV_FILE="$APP_DIR/.env"
COMPOSE_FILE="$APP_DIR/docker-compose.prod.yaml"

# Verificar se o diretório da aplicação existe
if [ ! -d "$APP_DIR" ]; then
    echo "Erro: Diretório da aplicação não encontrado: $APP_DIR"
    exit 1
fi

cd "$APP_DIR"

# Verificar se o arquivo .env existe
if [ ! -f "$ENV_FILE" ]; then
    echo "Erro: Arquivo .env não encontrado: $ENV_FILE"
    exit 1
fi

# Parar e remover containers existentes
echo "Parando containers existentes..."
sudo docker-compose -f "$COMPOSE_FILE" down --remove-orphans || true

# Limpar recursos Docker não utilizados
echo "Limpando recursos Docker..."
sudo docker system prune -f
sudo docker volume prune -f

# Build das imagens
echo "Fazendo build das imagens Docker..."
sudo docker-compose -f "$COMPOSE_FILE" build

# Iniciar a aplicação
echo "Iniciando a aplicação com Docker Compose..."
sudo docker-compose -f "$COMPOSE_FILE" up -d

# Aguardar a aplicação iniciar
echo "Aguardando a aplicação iniciar..."
sleep 30

# Verificar status dos containers
echo "Verificando status dos containers:"
sudo docker-compose -f "$COMPOSE_FILE" ps

# Verificar logs da aplicação
echo "Verificando logs da aplicação:"
sudo docker-compose -f "$COMPOSE_FILE" logs --tail=50 pointtils

# Verificar logs do banco de dados
echo "Verificando logs do banco de dados:"
sudo docker-compose -f "$COMPOSE_FILE" logs --tail=20 pointtils-db

# Testar conexão com a aplicação
echo "Testando conexão com a aplicação..."
APP_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)
if curl -s --retry 10 --retry-delay 5 "http://localhost:8080/actuator/health" | grep -q '"status":"UP"'; then
    echo "✅ Aplicação está rodando e saudável!"
    echo "📱 Acesse a aplicação em: http://$APP_IP:8080"
    echo "📊 Health check: http://$APP_IP:8080/actuator/health"
    echo "📚 Swagger UI: http://$APP_IP:8080/swagger-ui.html"
else
    echo "❌ Aplicação não está respondendo corretamente"
    echo "Verificando logs detalhados..."
    sudo docker-compose -f "$COMPOSE_FILE" logs pointtils
    exit 1
fi

echo "=== Deploy concluído com sucesso! ==="
