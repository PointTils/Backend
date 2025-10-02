variable "aws_region" {
  description = "Região da AWS onde a infraestrutura de desenvolvimento será criada"
  type        = string
  default     = "us-east-2"  # Ohio, mesma região da produção
}

variable "aws_account_id" {
  description = "ID da conta AWS"
  type        = string
  default     = ""
}

variable "ec2_ami" {
  description = "ID da AMI para a instância EC2 de desenvolvimento"
  type        = string
  default     = "ami-0a59f0e26c55590e9" # Ubuntu 22.04 LTS para us-east-2 (Ohio)
}

variable "db_username" {
  description = "Nome de usuário para o banco de dados PostgreSQL de desenvolvimento"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "Senha para o banco de dados PostgreSQL de desenvolvimento"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "Secret key para JWT de desenvolvimento"
  type        = string
  sensitive   = true
}

variable "db_name" {
  description = "Nome do banco de dados PostgreSQL de desenvolvimento"
  type        = string
  default     = "pointtils-dev-db"
}

variable "app_image" {
  description = "Imagem Docker da aplicação para desenvolvimento"
  type        = string
  default     = ""
}

variable "db_image" {
  description = "Imagem Docker do banco de dados para desenvolvimento"
  type        = string
  default     = ""
}

variable "create_ecr" {
  description = "Indica se o repositório ECR deve ser criado (false se já existir)"
  type        = bool
  default     = false
}

variable "environment" {
  description = "Ambiente da infraestrutura"
  type        = string
  default     = "development"
}
