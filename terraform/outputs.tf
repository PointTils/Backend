output "app_instance_public_ip" {
  description = "IP público da instância EC2 da aplicação"
  value       = aws_eip.pointtils_eip.public_ip
  # Garantir que o output é uma string para evitar erros de formatação
  depends_on  = [aws_eip.pointtils_eip]
}

output "app_instance_public_dns" {
  description = "DNS público da instância EC2 da aplicação"
  value       = aws_instance.pointtils_app.public_dns
}

output "s3_bucket_name" {
  description = "Nome do bucket S3 para testes de API"
  value       = aws_s3_bucket.pointtils_api_tests.bucket
  depends_on  = [aws_s3_bucket.pointtils_api_tests]
}

output "ecr_repository_url" {
  description = "URL do repositório ECR"
  value       = var.create_ecr && length(aws_ecr_repository.pointtils) > 0 ? aws_ecr_repository.pointtils[0].repository_url : "pointtils-repository-already-exists"
}
