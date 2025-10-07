# Diagnóstico e Solução para Problema de Conexão com Banco de Dados

## Problema Identificado
A aplicação em produção (EC2) não está respondendo aos endpoints de autenticação porque não consegue conectar ao banco de dados. O erro específico é:

```
FATAL: password authentication failed for user "pointtilsadmin"
```

## Causa do Problema
A aplicação está usando os valores padrão do `application.properties`:
- **Usuário:** `pointtilsadmin` (valor padrão)
- **Senha:** `password` (valor padrão)

Mas o banco de dados está configurado para:
- **Usuário:** `postgres` (definido no .env)
- **Senha:** `postgres` (definido no .env)

## Possíveis Soluções

### Solução 1: Configurar Variáveis de Ambiente no EC2
No servidor EC2, defina as variáveis de ambiente:

```bash
# Conectar ao servidor EC2 via SSH
ssh -i sua-chave.pem usuario@ec2-3-142-18-109.us-east-2.compute.amazonaws.com

# Configurar variáveis de ambiente
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pointtils
export JWT_SECRET=sua-chave-jwt-super-secreta-aqui
```

### Solução 2: Modificar o application.properties para Produção
Crie um `application-prod.properties` com as credenciais corretas:

```properties
# application-prod.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pointtils
spring.datasource.username=postgres
spring.datasource.password=postgres
security.jwt.secret-key=sua-chave-jwt-super-secreta-aqui
```

### Solução 3: Usar Docker Compose com .env no EC2
Certifique-se de que o arquivo `.env` esteja presente no EC2 e execute:

```bash
# No EC2, na pasta do projeto
docker-compose -f docker-compose.prod.yaml up -d
```

### Solução 4: Verificar se o Banco está Rodando
Conecte ao EC2 e verifique se o PostgreSQL está rodando:

```bash
# Verificar se o container do banco está rodando
docker ps

# Verificar logs do container do banco
docker logs pointtils-db

# Testar conexão com o banco
psql -h localhost -U postgres -d pointtils
```

## Passo a Passo para Resolver Imediatamente

1. **Conectar ao EC2:**
   ```bash
   ssh -i sua-chave.pem usuario@ec2-3-142-18-109.us-east-2.compute.amazonaws.com
   ```

2. **Verificar containers em execução:**
   ```bash
   docker ps
   docker ps -a  # para ver containers parados também
   ```

3. **Verificar variáveis de ambiente:**
   ```bash
   echo $SPRING_DATASOURCE_USERNAME
   echo $SPRING_DATASOURCE_PASSWORD
   ```

4. **Se necessário, reiniciar os containers:**
   ```bash
   docker-compose -f docker-compose.prod.yaml down
   docker-compose -f docker-compose.prod.yaml up -d
   ```

5. **Verificar logs da aplicação:**
   ```bash
   docker logs pointtils
   ```

## Configuração Recomendada para Produção

### Arquivo .env para Produção
```env
# Database
POSTGRES_USER=postgres
POSTGRES_PASSWORD=uma-senha-segura-aqui
POSTGRES_DB=pointtils

# Spring
SPRING_DATASOURCE_URL=jdbc:postgresql://pointtils-db:5432/pointtils
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=uma-senha-segura-aqui

# JWT (use uma chave segura de pelo menos 32 caracteres)
JWT_SECRET=chave-super-secreta-com-minimo-32-caracteres-aleatorios
JWT_EXPIRATION_TIME=900000
JWT_REFRESH_EXPIRATION_TIME=604800000
```

## Verificação de Funcionamento

Após aplicar as correções, teste os endpoints:

```bash
# Testar endpoint público (deve funcionar)
curl http://ec2-3-142-18-109.us-east-2.compute.amazonaws.com:8080/v3/api-docs

# Testar login (deve funcionar após correção)
curl -X POST http://ec2-3-142-18-109.us-east-2.compute.amazonaws.com:8080/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"usuario@exemplo.com","password":"senha123"}'
```

## Prevenção Futura

1. **Use perfis do Spring Boot** para diferentes ambientes
2. **Não use valores padrão inseguros** em application.properties
3. **Valide as configurações** antes de deploy em produção
4. **Use secrets management** (AWS Secrets Manager, HashiCorp Vault) para credenciais

Este problema é comum em ambientes de produção quando as variáveis de ambiente não são configuradas corretamente ou quando há incompatibilidade entre as configurações de desenvolvimento e produção.
