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

# Gera um par de chaves SSH
resource "tls_private_key" "ssh" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

# Cria o Key Pair no AWS usando a chave pública gerada
resource "aws_key_pair" "pointtils_key" {
  key_name   = "pointtils-dev-key"
  public_key = tls_private_key.ssh.public_key_openssh

  tags = {
    Name        = "pointtils-dev-key"
    Environment = var.environment
  }
}

# Script de inicialização SIMPLIFICADO para a instância EC2 do ambiente de desenvolvimento
data "template_file" "dev_user_data" {
  template = <<-EOF
              #!/bin/bash
              set -e  # Exit on any error
              echo "=== Iniciando configuração SIMPLIFICADA do servidor de desenvolvimento ==="
              
              # Atualizar pacotes
              sudo apt-get update -y
              sudo apt-get upgrade -y
              
              # Instalar APENAS Docker e AWS CLI (sem Docker Compose)
              sudo apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release
              curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
              echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
              sudo apt-get update -y
              sudo apt-get install -y docker-ce docker-ce-cli containerd.io awscli
              sudo systemctl enable docker
              sudo systemctl start docker
              sudo usermod -aG docker ubuntu
              
              # Criar rede Docker para os containers
              sudo docker network create pointtils-dev-network
              
              # Fazer login no ECR
              aws ecr get-login-password --region ${var.aws_region} | sudo docker login --username AWS --password-stdin ${var.aws_account_id}.dkr.ecr.${var.aws_region}.amazonaws.com
              
              echo "=== Configuração SIMPLIFICADA do servidor concluída ==="
              echo "=== A EC2 está pronta para receber containers da ECR via deploy ==="
              EOF
}

# Instância EC2 para a aplicação Pointtils do NOVO ambiente de desenvolvimento (t2.medium para melhor performance)
resource "aws_instance" "pointtils_dev_app" {
  ami                    = var.ec2_ami
  instance_type          = "t2.medium"  # Instância maior para melhor performance
  key_name               = aws_key_pair.pointtils_key.key_name
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

# Output da chave privada (IMPORTANTE!)
output "private_key_pem" {
  description = "Chave privada SSH gerada automaticamente"
  value       = tls_private_key.ssh.private_key_pem
  sensitive   = true
}

output "public_key_openssh" {
  description = "Chave pública SSH gerada automaticamente"
  value       = tls_private_key.ssh.public_key_openssh
}
