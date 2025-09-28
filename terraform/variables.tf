variable "aws_region" {
  description = "Região da AWS onde a infraestrutura será criada"
  type        = string
  default     = "us-east-2"  # Ohio, conforme orçamento
}

variable "aws_account_id" {
  description = "ID da conta AWS"
  type        = string
  default     = ""
}

variable "ec2_ami" {
  description = "ID da AMI para a instância EC2"
  type        = string
  default     = "ami-0a59f0e26c55590e9" # Ubuntu 22.04 LTS para us-east-2 (Ohio)
}

variable "db_username" {
  description = "Nome de usuário para o banco de dados PostgreSQL"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "Senha para o banco de dados PostgreSQL"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "Secret key para JWT"
  type        = string
  sensitive   = true
  default     = "testandoUmaNovaSenhaMasterComMaisDeTrintaEdoisCaracteres"
}

variable "app_image" {
  description = "Imagem Docker da aplicação"
  type        = string
  default     = ""
}

variable "db_image" {
  description = "Imagem Docker do banco de dados"
  type        = string
  default     = ""
}

variable "create_ecr" {
  description = "Indica se o repositório ECR deve ser criado (false se já existir)"
  type        = bool
  default     = false
}
