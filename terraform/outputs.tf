output "app_instance_public_ip" {
  description = "IP público da instância EC2 da aplicação"
  value       = aws_eip.pointtils_eip.public_ip
}

output "app_instance_public_dns" {
  description = "DNS público da instância EC2 da aplicação"
  value       = aws_instance.pointtils_app.public_dns
}

output "s3_bucket_name" {
  description = "Nome do bucket S3 para testes de API"
  value       = aws_s3_bucket.pointtils_api_tests.bucket
}

output "ecr_repository_url" {
  description = "URL do repositório ECR"
  value       = aws_ecr_repository.pointtils.repository_url
}
