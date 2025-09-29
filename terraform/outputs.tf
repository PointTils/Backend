output "app_instance_public_ip" {
  description = "IP público da instância EC2 da aplicação"
  value       = "3.142.18.109"  # IP fixo da instância existente
}

output "app_instance_public_dns" {
  description = "DNS público da instância EC2 da aplicação"
  value       = "ec2-3-142-18-109.us-east-2.compute.amazonaws.com"
}

output "s3_bucket_name" {
  description = "Nome do bucket S3 para testes de API"
  value       = "pointtils-api-tests"
}

output "ecr_repository_url" {
  description = "URL do repositório ECR"
  value       = "969285065739.dkr.ecr.us-east-2.amazonaws.com/pointtils"
}
