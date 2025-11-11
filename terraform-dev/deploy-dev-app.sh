#!/bin/bash
set -e  # Exit on any error

echo "=== Deploy Simples da Aplicação PointTils para DESENVOLVIMENTO ==="

# Parâmetros recebidos do pipeline
ECR_REGISTRY="${1:-969285065739.dkr.ecr.us-east-2.amazonaws.com}"
AWS_REGION="${2:-us-east-2}"

# Imagens já construídas no GitHub Actions
APP_IMAGE="$ECR_REGISTRY/pointtils-dev:dev-latest"
DB_IMAGE="$ECR_REGISTRY/pointtils-dev-db:dev-latest"
PROMETHEUS_IMAGE="$ECR_REGISTRY/pointtils-dev-prometheus:dev-latest"
GRAFANA_IMAGE="$ECR_REGISTRY/pointtils-dev-grafana:dev-latest"

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
docker pull $PROMETHEUS_IMAGE
docker pull $GRAFANA_IMAGE

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
docker stop pointtils-dev pointtils-db-dev prometheus grafana 2>/dev/null || true
docker rm pointtils-dev pointtils-db-dev prometheus grafana 2>/dev/null || true

# Remover forçadamente se ainda existirem
echo "Removendo forçadamente se containers ainda existirem..."
docker rm -f pointtils-dev 2>/dev/null || true
docker rm -f pointtils-db-dev 2>/dev/null || true
docker rm -f prometheus 2>/dev/null || true
docker rm -f grafana 2>/dev/null || true

# Criar volumes se não existirem
echo "Criando volumes se não existirem..."
docker volume create postgres_dev_data 2>/dev/null || true
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

# Iniciar container do Prometheus
echo "Iniciando container do Prometheus..."
docker run -d \
  --name prometheus \
  --network pointtils-dev-network \
  -p 9090:9090 \
  -v prometheus_data:/prometheus \
  --restart unless-stopped \
  $PROMETHEUS_IMAGE

# Iniciar container do Grafana
echo "Iniciando container do Grafana..."
docker run -d \
  --name grafana \
  --network pointtils-dev-network \
  -p 3000:3000 \
  -v grafana_data:/var/lib/grafana \
  --restart unless-stopped \
  -e GF_SECURITY_ADMIN_USER=admin \
  -e GF_SECURITY_ADMIN_PASSWORD=admin123456 \
  $GRAFANA_IMAGE

# Aguardar serviços de monitoramento iniciarem
echo "Aguardando serviços de monitoramento iniciarem..."
sleep 30

# Verificar status final dos containers
echo "Verificando status final dos containers de DESENVOLVIMENTO:"
docker ps

# Verificar logs da aplicação
echo "Verificando logs da aplicação de DESENVOLVIMENTO:"
docker logs --tail=50 pointtils-dev

# Limpar imagens antigas para evitar acúmulo
echo "Limpando imagens antigas para liberar espaço..."
echo "Mantendo apenas as últimas 2 versões de cada imagem..."

# Função para limpar imagens antigas mantendo as últimas N versões
clean_old_images() {
    local repo_name=$1
    local keep_count=2
    
    echo "Limpando imagens antigas de $repo_name..."
    
    # Listar todas as imagens do repositório, ordenadas por data de criação (mais recentes primeiro)
    local images=$(docker images --format "{{.ID}} {{.CreatedAt}} {{.Repository}}:{{.Tag}}" | grep "$repo_name" | sort -r -k2 | head -n 20)
    
    # Separar as imagens a manter (primeiras N)
    local images_to_keep=$(echo "$images" | head -n $keep_count | awk '{print $1}')
    
    # Listar todas as imagens do repositório
    local all_images=$(docker images --format "{{.ID}} {{.Repository}}:{{.Tag}}" | grep "$repo_name" | awk '{print $1}')
    
    # Remover imagens que não estão na lista de manter
    for image_id in $all_images; do
        if ! echo "$images_to_keep" | grep -q "$image_id"; then
            echo "Removendo imagem antiga: $image_id"
            docker rmi "$image_id" 2>/dev/null || true
        fi
    done
}

# Limpar imagens antigas de cada repositório
clean_old_images "pointtils-dev"
clean_old_images "pointtils-dev-grafana" 
clean_old_images "pointtils-dev-prometheus"
clean_old_images "pointtils-dev-db"

# Limpeza adicional: remover imagens dangling (sem tag)
echo "Removendo imagens dangling (sem tag)..."
docker image prune -f

echo "Limpeza de imagens concluída!"

echo "=== Deploy de DESENVOLVIMENTO concluído! ==="
echo "Aplicação de DESENVOLVIMENTO disponível em: http://localhost:8080"
echo "Swagger UI: http://localhost:8080/swagger-ui.html"
echo "Actuator Health: http://localhost:8080/actuator/health"
echo "Prometheus disponível em: http://localhost:9090"
echo "Grafana disponível em: http://localhost:3000"
echo "Para verificar logs: docker logs pointtils-dev"
echo "Para verificar status: docker ps -a"
