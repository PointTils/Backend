provider "aws" {
  region = var.aws_region  # Agora usando us-east-2 (Ohio)
}

# Criando uma VPC
resource "aws_vpc" "pointtils_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  tags = {
    Name = "pointtils-vpc"
  }
}

# Criando subnets públicas
resource "aws_subnet" "public_subnet_1" {
  vpc_id                  = aws_vpc.pointtils_vpc.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "${var.aws_region}a"
  map_public_ip_on_launch = true
  tags = {
    Name = "pointtils-public-subnet-1"
  }
}

resource "aws_subnet" "public_subnet_2" {
  vpc_id                  = aws_vpc.pointtils_vpc.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = "${var.aws_region}b"
  map_public_ip_on_launch = true
  tags = {
    Name = "pointtils-public-subnet-2"
  }
}

# Não precisamos mais de subnets privadas já que não temos RDS

# Internet Gateway
resource "aws_internet_gateway" "pointtils_igw" {
  vpc_id = aws_vpc.pointtils_vpc.id
  tags = {
    Name = "pointtils-igw"
  }
}

# Route Table para as subnets públicas
resource "aws_route_table" "public_route_table" {
  vpc_id = aws_vpc.pointtils_vpc.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.pointtils_igw.id
  }
  tags = {
    Name = "pointtils-public-route-table"
  }
}

# Associando as subnets públicas à route table
resource "aws_route_table_association" "public_rta_1" {
  subnet_id      = aws_subnet.public_subnet_1.id
  route_table_id = aws_route_table.public_route_table.id
}

resource "aws_route_table_association" "public_rta_2" {
  subnet_id      = aws_subnet.public_subnet_2.id
  route_table_id = aws_route_table.public_route_table.id
}

# Grupo de segurança para a aplicação
resource "aws_security_group" "app_sg" {
  name        = "pointtils-app-sg"
  description = "Security group for Pointtils application"
  vpc_id      = aws_vpc.pointtils_vpc.id

  # Permitir tráfego de entrada na porta 8080
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow HTTP traffic to the application"
  }

  # Permitir PostgreSQL
  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow PostgreSQL"
  }

  # Permitir SSH
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow SSH"
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
    Name = "pointtils-app-sg"
  }
}

# Removido grupo de segurança para o banco de dados e subnet group para o RDS

# Nota: Removido RDS PostgreSQL pois não consta no orçamento
# A aplicação usará um banco de dados local no EC2 ou externo gerenciado separadamente

# IAM role para a instância EC2
resource "aws_iam_role" "ec2_role" {
  name = "pointtils-ec2-role"

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
    Name = "pointtils-ec2-role"
  }
  
  # Ignorar erros se o role já existir
  lifecycle {
    ignore_changes = all
  }
}

# Policy para a instância EC2 acessar o ECR
resource "aws_iam_role_policy" "ecr_policy" {
  name = "pointtils-ecr-policy"
  role = aws_iam_role.ec2_role.id

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

# Policy para a instância EC2 acessar o S3
resource "aws_iam_role_policy" "s3_policy" {
  name = "pointtils-s3-policy"
  role = aws_iam_role.ec2_role.id

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
          "${aws_s3_bucket.pointtils_api_tests.arn}",
          "${aws_s3_bucket.pointtils_api_tests.arn}/*"
        ]
      }
    ]
  })
}

# Profile de instância para associar o IAM role à instância EC2
resource "aws_iam_instance_profile" "ec2_profile" {
  name = "pointtils-ec2-profile"
  role = aws_iam_role.ec2_role.name
}

# Chave SSH para acesso às instâncias EC2
resource "aws_key_pair" "pointtils_key" {
  key_name   = "pointtils_key"
  public_key = file("${path.module}/pointtils_key.pub")
  
  # Ignorar erros se a chave já existir
  lifecycle {
    ignore_changes = all
  }
}

# Script de inicialização para a instância EC2
data "template_file" "user_data" {
  template = <<-EOF
              #!/bin/bash
              set -e  # Exit on any error
              echo "=== Iniciando configuração do servidor ==="
              
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
              
              # Criar diretório para a aplicação
              mkdir -p /home/ubuntu/pointtils
              cd /home/ubuntu/pointtils
              
              # Criar arquivo .env para variáveis de ambiente
              cat > /home/ubuntu/pointtils/.env << ENVFILE
              # Database Container Configuration
              POSTGRES_USER=${var.db_username}
              POSTGRES_PASSWORD=${var.db_password}
              POSTGRES_DB=${var.db_name}
              
              # Spring Application Configuration
              SPRING_APPLICATION_NAME=pointtils-api
              SERVER_PORT=8080
              
              # Spring DataSource Configuration
              SPRING_DATASOURCE_URL=jdbc:postgresql://pointtils-db:5432/${var.db_name}
              SPRING_DATASOURCE_USERNAME=${var.db_username}
              SPRING_DATASOURCE_PASSWORD=${var.db_password}
              
              # JPA/Hibernate Configuration
              SPRING_JPA_HIBERNATE_DDL_AUTO=validate
              SPRING_JPA_SHOW_SQL=true
              
              # JWT Configuration
              JWT_SECRET=${var.jwt_secret}
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
              ENVFILE
              
              # Fazer login no ECR
              aws ecr get-login-password --region ${var.aws_region} | sudo docker login --username AWS --password-stdin ${var.aws_account_id}.dkr.ecr.${var.aws_region}.amazonaws.com
              
              # Criar docker-compose.yaml para usar imagens do ECR
              cat > /home/ubuntu/pointtils/docker-compose.yaml << DOCKERFILE
              services:
                pointtils:
                  image: ${var.app_image}
                  container_name: pointtils
                  environment:
                    - SPRING_DATASOURCE_URL=jdbc:postgresql://pointtils-db:5432/${var.db_name}
                    - SPRING_DATASOURCE_USERNAME=${var.db_username}
                    - SPRING_DATASOURCE_PASSWORD=${var.db_password}
                    - SPRING_APPLICATION_NAME=pointtils-api
                    - SERVER_PORT=8080
                    - JWT_SECRET=${var.jwt_secret}
                    - JWT_EXPIRATION_TIME=900000
                    - SPRING_JPA_HIBERNATE_DDL_AUTO=validate
                    - SPRING_JPA_SHOW_SQL=true
                    - SPRINGDOC_API_DOCS_ENABLED=true
                    - SPRINGDOC_SWAGGER_UI_ENABLED=true
                    - SPRINGDOC_SWAGGER_UI_PATH=/swagger-ui.html
                    - CLOUD_AWS_BUCKET_NAME=pointtils-api-tests-d9396dcc
                    - AWS_REGION=${var.aws_region}
                  ports:
                    - "8080:8080"
                  depends_on:
                    pointtils-db:
                      condition: service_healthy
                  networks:
                    - pointtils-network
                  restart: unless-stopped
              
                pointtils-db:
                  image: ${var.db_image}
                  container_name: pointtils-db
                  environment:
                    POSTGRES_DB: ${var.db_name}
                    POSTGRES_USER: ${var.db_username}
                    POSTGRES_PASSWORD: ${var.db_password}
                  ports:
                    - "5432:5432"
                  volumes:
                    - postgres_data:/var/lib/postgresql/data
                  networks:
                    - pointtils-network
                  healthcheck:
                    test: ["CMD-SHELL", "pg_isready -U ${var.db_username} -d ${var.db_name}"]
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
              DOCKERFILE
              
              # Iniciar a aplicação com Docker Compose usando imagens do ECR
              cd /home/ubuntu/pointtils
              sudo docker-compose up -d
              
              # Aguardar a aplicação iniciar e verificar status
              echo "Aguardando aplicação iniciar..."
              sleep 30
              
              # Verificar se os containers estão rodando
              echo "Verificando status dos containers:"
              sudo docker-compose -f docker-compose.prod.yaml ps
              
              # Verificar logs para debugging
              echo "Verificando logs da aplicação:"
              sudo docker-compose -f docker-compose.prod.yaml logs --tail=20 pointtils
              
              echo "=== Configuração do servidor concluída ==="
              echo "=== Aplicação iniciada com Docker Compose de produção ==="
              EOF
}

# Instância EC2 para a aplicação Pointtils (Conforme orçamento: t2.medium em Ohio)
resource "aws_instance" "pointtils_app" {
  ami                    = "ami-0a59f0e26c55590e9" # Ubuntu 22.04 LTS para us-east-2 (Ohio)
  instance_type          = "t2.medium"
  key_name               = aws_key_pair.pointtils_key.key_name
  vpc_security_group_ids = [aws_security_group.app_sg.id]
  subnet_id              = aws_subnet.public_subnet_1.id
  iam_instance_profile   = aws_iam_instance_profile.ec2_profile.name
  user_data              = data.template_file.user_data.rendered

  tags = {
    Name = "pointtils-app"
  }
}

# Elastic IP para a instância EC2
resource "aws_eip" "pointtils_eip" {
  instance = aws_instance.pointtils_app.id
  domain   = "vpc"
  tags = {
    Name = "pointtils-eip"
  }
}

# Amazon S3 Bucket para armazenamento de APIs para teste (conforme orçamento)
resource "aws_s3_bucket" "pointtils_api_tests" {
  bucket = "pointtils-api-tests-${random_id.bucket_suffix.hex}"

  tags = {
    Name = "pointtils-api-tests"
  }
}

resource "random_id" "bucket_suffix" {
  byte_length = 4
}

# Configuração de acesso ao bucket S3
resource "aws_s3_bucket_public_access_block" "pointtils_api_tests" {
  bucket = aws_s3_bucket.pointtils_api_tests.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# Amazon ECR Repository (conforme orçamento)
# Verificar se o ECR já existe antes de tentar criar
data "aws_ecr_repository" "existing_repo" {
  count = var.create_ecr ? 0 : 1
  name  = "pointtils"
}

resource "aws_ecr_repository" "pointtils" {
  # Só criar se a variável create_ecr for verdadeira
  count                = var.create_ecr ? 1 : 0
  name                 = "pointtils"
  image_tag_mutability = "MUTABLE"
  
  image_scanning_configuration {
    scan_on_push = true
  }
  
  tags = {
    Name = "pointtils-ecr"
  }
  
  # Ignorar erros se o repositório já existir
  lifecycle {
    ignore_changes = all
    prevent_destroy = false
  }
}

# Lifecycle Policy para o ECR (limitar o armazenamento a 20GB conforme orçamento)
resource "aws_ecr_lifecycle_policy" "pointtils" {
  # Só criar se o repositório ECR também foi criado
  count      = var.create_ecr ? 1 : 0
  repository = aws_ecr_repository.pointtils[0].name

  policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Keep only the latest 10 images"
        selection = {
          tagStatus     = "any"
          countType     = "imageCountMoreThan"
          countNumber   = 10
        }
        action = {
          type = "expire"
        }
      }
    ]
  })
}
