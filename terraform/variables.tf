variable "aws_region" {
  description = "Região da AWS onde a infraestrutura será criada"
  type        = string
  default     = "us-east-1"
}

variable "ec2_ami" {
  description = "ID da AMI para a instância EC2"
  type        = string
  default     = "ami-0c7217cdde317cfec" # Ubuntu 22.04 LTS para us-east-1
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

variable "ssh_public_key" {
  description = "Chave pública SSH para acesso às instâncias EC2"
  type        = string
}
