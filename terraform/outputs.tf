output "app_instance_public_ip" {
  description = "IP público da instância EC2 da aplicação"
  value       = aws_eip.pointtils_eip.public_ip
}

output "app_instance_public_dns" {
  description = "DNS público da instância EC2 da aplicação"
  value       = aws_instance.pointtils_app.public_dns
}

output "database_endpoint" {
  description = "Endpoint do banco de dados PostgreSQL"
  value       = aws_db_instance.pointtils_db.endpoint
}

output "database_name" {
  description = "Nome do banco de dados PostgreSQL"
  value       = aws_db_instance.pointtils_db.db_name
}
