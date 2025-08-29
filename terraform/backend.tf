# Arquivo para armazenar os valores do tfstate remotamente (recomendado para equipes)
terraform {
  backend "s3" {
    bucket         = "pointtils-terraform-state"  # Precisa criar esse bucket manualmente antes
    key            = "terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "pointtils-terraform-locks"  # Precisa criar essa tabela manualmente antes
  }
}
