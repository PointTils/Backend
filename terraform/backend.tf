# Arquivo para armazenar os valores do tfstate remotamente (recomendado para equipes)
terraform {
  required_version = ">= 1.0.0"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  
  backend "s3" {
    bucket         = "pointtils-terraform-state"  # Precisa criar esse bucket manualmente antes
    key            = "terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "pointtils-terraform-locks"  # Precisa criar essa tabela manualmente antes
  }
}
