provider "aws" {
  region = var.aws_region
}

# Criando uma VPC para NOVO ambiente de desenvolvimento
resource "aws_vpc" "pointtils_dev_vpc" {
  cidr_block           = "10.2.0.0/16"  # CIDR diferente dos ambientes existentes
  enable_dns_hostnames = true
  tags = {
    Name = "pointtils-dev-vpc"
    Environment = var.environment
  }
}

# Criando subnets públicas para NOVO ambiente de desenvolvimento
resource "aws_subnet" "dev_public_subnet_1" {
  vpc_id                  = aws_vpc.pointtils_dev_vpc.id
  cidr_block              = "10.2.1.0/24"
  availability_zone       = "${var.aws_region}a"
  map_public_ip_on_launch = true
  tags = {
    Name = "pointtils-dev-public-subnet-1"
    Environment = var.environment
  }
}

resource "aws_subnet" "dev_public_subnet_2" {
  vpc_id                  = aws_vpc.pointtils_dev_vpc.id
  cidr_block              = "10.2.2.0/24"
  availability_zone       = "${var.aws_region}b"
  map_public_ip_on_launch = true
  tags = {
    Name = "pointtils-dev-public-subnet-2"
    Environment = var.environment
  }
}

# Internet Gateway para NOVO ambiente de desenvolvimento
resource "aws_internet_gateway" "pointtils_dev_igw" {
  vpc_id = aws_vpc.pointtils_dev_vpc.id
  tags = {
    Name = "pointtils-dev-igw"
    Environment = var.environment
  }
}

# Route Table para as subnets públicas do NOVO ambiente de desenvolvimento
resource "aws_route_table" "dev_public_route_table" {
  vpc_id = aws_vpc.pointtils_dev_vpc.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.pointtils_dev_igw.id
  }
  tags = {
    Name = "pointtils-dev-public-route-table"
    Environment = var.environment
  }
}

# Associando as subnets públicas à route table do NOVO ambiente de desenvolvimento
resource "aws_route_table_association" "dev_public_rta_1" {
  subnet_id      = aws_subnet.dev_public_subnet_1.id
  route_table_id = aws_route_table.dev_public_route_table.id
}

resource "aws_route_table_association" "dev_public_rta_2" {
  subnet_id      = aws_subnet.dev_public_subnet_2.id
  route_table_id = aws_route_table.dev_public_route_table.id
}

# Grupo de segurança para a aplicação do NOVO ambiente de desenvolvimento
resource "aws_security_group" "dev_app_sg" {
  name        = "pointtils-dev-app-sg"
  description = "Security group for Pointtils NEW development application"
  vpc_id      = aws_vpc.pointtils_dev_vpc.id

  # Permitir tráfego de entrada na porta 8080
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow HTTP traffic to the NEW development application"
  }

  # Permitir PostgreSQL
  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow PostgreSQL for NEW development"
  }

  # Permitir SSH
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow SSH for NEW development"
  }

  # Permitir todo o tráfego de saída
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow all outbound traffic"
  }

  tags = {
    Name = "pointtils-dev-app-sg"
    Environment = var.environment
  }
}

# IAM role para a instância EC2 do NOVO ambiente de desenvolvimento
resource "aws_iam_role" "dev_ec2_role" {
  name = "pointtils-dev-ec2-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Name = "pointtils-dev-ec2-role"
    Environment = var.environment
  }
}

# Policy para a instância EC2 do NOVO ambiente de desenvolvimento acessar o ECR
resource "aws_iam_role_policy" "dev_ecr_policy" {
  name = "pointtils-dev-ecr-policy"
  role = aws_iam_role.dev_ec2_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "ecr:GetAuthorizationToken",
          "ecr:BatchCheckLayerAvailability",
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage"
        ]
        Effect   = "Allow"
        Resource = "*"
      }
    ]
  })
}

# Policy para a instância EC2 do NOVO ambiente de desenvolvimento acessar o S3
resource "aws_iam_role_policy" "dev_s3_policy" {
  name = "pointtils-dev-s3-policy"
  role = aws_iam_role.dev_ec2_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "s3:PutObject",
          "s3:GetObject",
          "s3:DeleteObject",
          "s3:ListBucket"
        ]
        Effect = "Allow"
        Resource = [
          "${aws_s3_bucket.pointtils_dev_api_tests.arn}",
          "${aws_s3_bucket.pointtils_dev_api_tests.arn}/*"
        ]
      }
    ]
  })
}

# Profile de instância para associar o IAM role à instância EC2 do NOVO ambiente de desenvolvimento
resource "aws_iam_instance_profile" "dev_ec2_profile" {
  name = "pointtils-dev-ec2-profile"
  role = aws_iam_role.dev_ec2_role.name
}

# Chave SSH para acesso às instâncias EC2 do NOVO ambiente de desenvolvimento
resource "aws_key_pair" "pointtils_key" {
  key_name   = "pointtils_dev_key"
  public_key = file("${path.module}/pointtils_dev_key.pub")
}

# Script de inicialização para a instância EC2 do NOVO ambiente de desenvolvimento
data "template_file" "dev_user_data" {
  template = <<-EOF
              #!/bin/bash
              set -e  # Exit on any error
              echo "=== Iniciando configuração do servidor de NOVO desenvolvimento ==="
              
              # Atualizar pacotes
              sudo apt-get update -y
              sudo apt-get upgrade -y
              
              # Instalar Docker
              sudo apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release
              curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
              echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
              sudo apt-get update -y
              sudo apt-get install -y docker-ce docker-ce-cli containerd.io awscli
              sudo systemctl enable docker
              sudo systemctl start docker
              sudo usermod -aG docker ubuntu
              
              # Instalar Docker Compose
              sudo curl -L "https://github.com/docker/compose/releases/download/v2.15.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
              sudo chmod +x /usr/local/bin/docker-compose
              
              # Criar diretório para a aplicação de NOVO desenvolvimento
              mkdir -p /home/ubuntu/pointtils-dev
              cd /home/ubuntu/pointtils-dev
              
              # Criar arquivo .env para variáveis de ambiente de NOVO desenvolvimento
              cat > /home/ubuntu/pointtils-dev/.env << ENVFILE
              # Database Container Configuration - NEW Development
              POSTGRES_USER=${var.db_username}
              POSTGRES_PASSWORD=${var.db_password}
              POSTGRES_DB=${var.db_name}
              
              # Spring Application Configuration - NEW Development
              SPRING_APPLICATION_NAME=pointtils-api-dev
              SERVER_PORT=8080
              
              # Spring DataSource Configuration - NEW Development
              SPRING_DATASOURCE_URL=jdbc:postgresql://pointtils-dev-db:5432/${var.db_name}
              SPRING_DATASOURCE_USERNAME=${var.db_username}
              SPRING_DATASOURCE_PASSWORD=${var.db_password}
              
              # JPA/Hibernate Configuration - NEW Development
              SPRING_JPA_HIBERNATE_DDL_AUTO=update  # Mais permissivo para desenvolvimento
              SPRING_JPA_SHOW_SQL=true
              
              # JWT Configuration - NEW Development
              JWT_SECRET=${var.jwt_secret}
              JWT_ISSUER=pointtils-api-dev
              JWT_EXPIRATION_TIME=900000
              JWT_REFRESH_EXPIRATION_TIME=604800000
              
              # Flyway Configuration - NEW Development
              SPRING_FLYWAY_ENABLED=true
              SPRING_FLYWAY_LOCATIONS=classpath:db/migration
              SPRING_FLYWAY_BASELINE_ON_MIGRATE=true
              SPRING_FLYWAY_VALIDATE_ON_MIGRATE=true
              
              # Swagger/OpenAPI Configuration - NEW Development
              SPRINGDOC_API_DOCS_ENABLED=true
              SPRINGDOC_SWAGGER_UI_ENABLED=true
              SPRINGDOC_SWAGGER_UI_PATH=/swagger-ui.html
              
              # NEW Development-specific configurations
              SPRING_PROFILES_ACTIVE=dev
              LOGGING_LEVEL_COM_POINTTILS=DEBUG
              ENVFILE
              
              # Fazer login no ECR
              aws ecr get-login-password --region ${var.aws_region} | sudo docker login --username AWS --password-stdin ${var.aws_account_id}.dkr.ecr.${var.aws_region}.amazonaws.com
              
              # Criar docker-compose.yaml para NOVO desenvolvimento
              cat > /home/ubuntu/pointtils-dev/docker-compose.yaml << DOCKERFILE
              services:
                pointtils-dev:
                  image: ${var.app_image}
                  container_name: pointtils-dev
                  environment:
                    - SPRING_DATASOURCE_URL=jdbc:postgresql://pointtils-dev-db:5432/${var.db_name}
                    - SPRING_DATASOURCE_USERNAME=${var.db_username}
                    - SPRING_DATASOURCE_PASSWORD=${var.db_password}
                    - SPRING_APPLICATION_NAME=pointtils-api-dev
                    - SERVER_PORT=8080
                    - JWT_SECRET=${var.jwt_secret}
                    - JWT_EXPIRATION_TIME=900000
                    - SPRING_JPA_HIBERNATE_DDL_AUTO=update
                    - SPRING_JPA_SHOW_SQL=true
                    - SPRINGDOC_API_DOCS_ENABLED=true
                    - SPRINGDOC_SWAGGER_UI_ENABLED=true
                    - SPRINGDOC_SWAGGER_UI_PATH=/swagger-ui.html
                    - CLOUD_AWS_BUCKET_NAME=${aws_s3_bucket.pointtils_dev_api_tests.bucket}
                    - AWS_REGION=${var.aws_region}
                    - SPRING_PROFILES_ACTIVE=dev
                    - LOGGING_LEVEL_COM_POINTTILS=DEBUG
                  ports:
                    - "8080:8080"
                  depends_on:
                    pointtils-dev-db:
                      condition: service_healthy
                  networks:
                    - pointtils-dev-network
                  restart: unless-stopped
              
                pointtils-dev-db:
                  image: ${var.db_image}
                  container_name: pointtils-dev-db
                  environment:
                    POSTGRES_DB: ${var.db_name}
                    POSTGRES_USER: ${var.db_username}
                    POSTGRES_PASSWORD: ${var.db_password}
                  ports:
                    - "5432:5432"
                  volumes:
                    - postgres_dev_data:/var/lib/postgresql/data
                  networks:
                    - pointtils-dev-network
                  healthcheck:
                    test: ["CMD-SHELL", "pg_isready -U ${var.db_username} -d ${var.db_name}"]
                    interval: 30s
                    timeout: 10s
                    retries: 3
                    start_period: 40s
                  restart: unless-stopped
              
              volumes:
                postgres_dev_data:
              
              networks:
                pointtils-dev-network:
                  driver: bridge
              DOCKERFILE
              
              # Iniciar a aplicação com Docker Compose para NOVO desenvolvimento
              cd /home/ubuntu/pointtils-dev
              sudo docker-compose up -d
              
              # Aguardar a aplicação iniciar e verificar status
              echo "Aguardando aplicação de NOVO desenvolvimento iniciar..."
              sleep 30
              
              # Verificar se os containers estão rodando
              echo "Verificando status dos containers de NOVO desenvolvimento:"
              sudo docker-compose ps
              
              # Verificar logs para debugging
              echo "Verificando logs da aplicação de NOVO desenvolvimento:"
              sudo docker-compose logs --tail=20 pointtils-dev
              
              echo "=== Configuração do servidor de NOVO desenvolvimento concluída ==="
              echo "=== Aplicação de NOVO desenvolvimento iniciada com Docker Compose ==="
              EOF
}

# Instância EC2 para a aplicação Pointtils do NOVO ambiente de desenvolvimento (t2.micro para economia)
resource "aws_instance" "pointtils_dev_app" {
  ami                    = var.ec2_ami
  instance_type          = "t2.micro"  # Instância menor para desenvolvimento
  key_name               = aws_key_pair.pointtils_dev_key.key_name
  vpc_security_group_ids = [aws_security_group.dev_app_sg.id]
  subnet_id              = aws_subnet.dev_public_subnet_1.id
  iam_instance_profile   = aws_iam_instance_profile.dev_ec2_profile.name
  user_data              = data.template_file.dev_user_data.rendered

  tags = {
    Name = "pointtils-dev-app"
    Environment = var.environment
  }
}

# Elastic IP para a instância EC2 do NOVO ambiente de desenvolvimento
resource "aws_eip" "pointtils_dev_eip" {
  instance = aws_instance.pointtils_dev_app.id
  domain   = "vpc"
  tags = {
    Name = "pointtils-dev-eip"
    Environment = var.environment
  }
}

# Amazon S3 Bucket para armazenamento de APIs para teste do NOVO ambiente de desenvolvimento
resource "aws_s3_bucket" "pointtils_dev_api_tests" {
  bucket = "pointtils-dev-api-tests-${random_id.dev_bucket_suffix.hex}"

  tags = {
    Name = "pointtils-dev-api-tests"
    Environment = var.environment
  }
}

resource "random_id" "dev_bucket_suffix" {
  byte_length = 4
}

# Configuração de acesso ao bucket S3 do NOVO ambiente de desenvolvimento
resource "aws_s3_bucket_public_access_block" "pointtils_dev_api_tests" {
  bucket = aws_s3_bucket.pointtils_dev_api_tests.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# Outputs para NOVO ambiente de desenvolvimento
output "dev_app_instance_id" {
  description = "ID da instância EC2 do NOVO ambiente de desenvolvimento"
  value       = aws_instance.pointtils_dev_app.id
}

output "dev_app_public_ip" {
  description = "IP público da instância EC2 do NOVO ambiente de desenvolvimento"
  value       = aws_eip.pointtils_dev_eip.public_ip
}

output "dev_app_url" {
  description = "URL da aplicação do NOVO ambiente de desenvolvimento"
  value       = "http://${aws_eip.pointtils_dev_eip.public_ip}:8080"
}

output "dev_s3_bucket" {
  description = "Nome do bucket S3 do NOVO ambiente de desenvolvimento"
  value       = aws_s3_bucket.pointtils_dev_api_tests.bucket
}
