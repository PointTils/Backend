provider "aws" {
  region = var.aws_region
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

# Criando subnets privadas para o banco de dados
resource "aws_subnet" "private_subnet_1" {
  vpc_id            = aws_vpc.pointtils_vpc.id
  cidr_block        = "10.0.3.0/24"
  availability_zone = "${var.aws_region}a"
  tags = {
    Name = "pointtils-private-subnet-1"
  }
}

resource "aws_subnet" "private_subnet_2" {
  vpc_id            = aws_vpc.pointtils_vpc.id
  cidr_block        = "10.0.4.0/24"
  availability_zone = "${var.aws_region}b"
  tags = {
    Name = "pointtils-private-subnet-2"
  }
}

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

# Grupo de segurança para o banco de dados
resource "aws_security_group" "db_sg" {
  name        = "pointtils-db-sg"
  description = "Security group for Pointtils database"
  vpc_id      = aws_vpc.pointtils_vpc.id

  # Permitir tráfego de entrada na porta do PostgreSQL apenas da subnet da aplicação
  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.app_sg.id]
    description     = "Allow PostgreSQL traffic from application servers"
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
    Name = "pointtils-db-sg"
  }
}

# Subnet group para o RDS
resource "aws_db_subnet_group" "pointtils_db_subnet_group" {
  name       = "pointtils-db-subnet-group"
  subnet_ids = [aws_subnet.private_subnet_1.id, aws_subnet.private_subnet_2.id]

  tags = {
    Name = "Pointtils DB Subnet Group"
  }
}

# Instância RDS PostgreSQL
resource "aws_db_instance" "pointtils_db" {
  identifier             = "pointtils-db"
  engine                 = "postgres"
  engine_version         = "14"
  instance_class         = "db.t3.micro"
  allocated_storage      = 20
  storage_type           = "gp2"
  db_name                = "pointtilsdb"
  username               = var.db_username
  password               = var.db_password
  parameter_group_name   = "default.postgres14"
  db_subnet_group_name   = aws_db_subnet_group.pointtils_db_subnet_group.name
  vpc_security_group_ids = [aws_security_group.db_sg.id]
  skip_final_snapshot    = true
  publicly_accessible    = false

  tags = {
    Name = "pointtils-db"
  }
}

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

# Profile de instância para associar o IAM role à instância EC2
resource "aws_iam_instance_profile" "ec2_profile" {
  name = "pointtils-ec2-profile"
  role = aws_iam_role.ec2_role.name
}

# Chave SSH para acesso às instâncias EC2
resource "aws_key_pair" "pointtils_key" {
  key_name   = "pointtils-key"
  public_key = var.ssh_public_key
}

# Script de inicialização para a instância EC2
data "template_file" "user_data" {
  template = <<-EOF
              #!/bin/bash
              echo "=== Iniciando configuração do servidor ==="
              
              # Atualizar pacotes
              sudo apt-get update -y
              sudo apt-get upgrade -y
              
              # Instalar Docker
              sudo apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release
              curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
              echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
              sudo apt-get update -y
              sudo apt-get install -y docker-ce docker-ce-cli containerd.io
              sudo systemctl enable docker
              sudo systemctl start docker
              sudo usermod -aG docker ubuntu
              
              # Instalar Docker Compose
              sudo curl -L "https://github.com/docker/compose/releases/download/v2.15.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
              sudo chmod +x /usr/local/bin/docker-compose
              
              # Criar arquivo .env para variáveis de ambiente
              cat > /home/ubuntu/.env << ENVFILE
              SPRING_DATASOURCE_URL=jdbc:postgresql://${aws_db_instance.pointtils_db.endpoint}/pointtilsdb
              SPRING_DATASOURCE_USERNAME=${var.db_username}
              SPRING_DATASOURCE_PASSWORD=${var.db_password}
              ENVFILE
              
              echo "=== Configuração do servidor concluída ==="
              EOF
}

# Instância EC2 para a aplicação Pointtils
resource "aws_instance" "pointtils_app" {
  ami                    = var.ec2_ami
  instance_type          = "t2.micro"
  key_name               = aws_key_pair.pointtils_key.key_name
  vpc_security_group_ids = [aws_security_group.app_sg.id]
  subnet_id              = aws_subnet.public_subnet_1.id
  iam_instance_profile   = aws_iam_instance_profile.ec2_profile.name
  user_data              = data.template_file.user_data.rendered

  tags = {
    Name = "pointtils-app"
  }

  depends_on = [aws_db_instance.pointtils_db]
}

# Elastic IP para a instância EC2
resource "aws_eip" "pointtils_eip" {
  instance = aws_instance.pointtils_app.id
  domain   = "vpc"
  tags = {
    Name = "pointtils-eip"
  }
}
