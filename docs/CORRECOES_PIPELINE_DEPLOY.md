# Correções no Pipeline de Deploy

## Problemas Identificados e Corrigidos

### 1. Clone do Repositório na EC2 (Removido)
**Problema**: O arquivo `terraform/main.tf` continha um clone do repositório GitHub no `user_data` que não deveria existir.

**Correção**: Removido o trecho que clonava o repositório e substituído por:
- Criação de diretório `/home/ubuntu/pointtils`
- Uso exclusivo de imagens do ECR

### 2. Inconsistência no Uso de Imagens (Corrigido)
**Problema**: 
- Pipeline GitHub Actions construía e fazia push das imagens para o ECR
- Terraform não usava essas imagens do ECR
- Em vez disso, clonava o repositório e fazia build local na EC2

**Correção**: 
- Modificado o `user_data` para usar as imagens do ECR
- Adicionado login no ECR
- Criado `docker-compose.yaml` dinâmico que usa as imagens do ECR

### 3. Docker Compose Incorreto (Corrigido)
**Problema**: O `docker-compose.prod.yaml` estava configurado para fazer build local

**Correção**: Atualizado para usar imagens do ECR:
```yaml
services:
  pointtils:
    image: ${APP_IMAGE:-pointtils:latest}
    # ... resto da configuração
  
  pointtils-db:
    image: ${DB_IMAGE:-postgres:15}
    # ... resto da configuração
```

## Fluxo Corrigido

### 1. Pipeline GitHub Actions
- ✅ Build das imagens Docker
- ✅ Push para ECR
- ✅ Passa as URLs das imagens para o Terraform

### 2. Terraform
- ✅ Cria EC2 com IAM role para acessar ECR
- ✅ **NÃO** clona mais o repositório
- ✅ Usa as imagens do ECR passadas pelo pipeline
- ✅ Faz login no ECR automaticamente
- ✅ Executa containers usando imagens do ECR

### 3. EC2
- ✅ Instala Docker e Docker Compose
- ✅ Faz login no ECR usando IAM role
- ✅ Puxa imagens do ECR
- ✅ Executa containers
- ✅ **NÃO** contém código fonte do repositório

## Arquivos Modificados

### 1. `terraform/main.tf`
- Removido clone do repositório GitHub
- Adicionado login no ECR
- Criado `docker-compose.yaml` dinâmico que usa imagens do ECR
- Corrigidos comandos de verificação para usar docker-compose correto

### 2. `docker-compose.prod.yaml`
- Substituído `build` por `image`
- Adicionadas variáveis `APP_IMAGE` e `DB_IMAGE`
- Agora usa imagens do ECR em vez de build local

### 3. `terraform/deploy-app.sh` (Novo)
- Script para deploy manual usando imagens do ECR
- Verifica dependências
- Faz login no ECR
- Cria arquivos de configuração
- Executa containers

## Benefícios das Correções

### ✅ Segurança
- Não há código fonte na EC2
- Credenciais gerenciadas via IAM roles
- Imagens escaneadas pelo ECR

### ✅ Eficiência
- Deploy mais rápido (não precisa fazer build)
- Menor uso de recursos na EC2
- Imagens pré-construídas e testadas

### ✅ Manutenibilidade
- Fluxo consistente entre CI e CD
- Imagens versionadas no ECR
- Rollback fácil usando tags anteriores

### ✅ Melhores Práticas
- Uso adequado de containers
- Separação entre build e deploy
- Infraestrutura imutável

## Como Testar

### 1. Pipeline Automático
```bash
# Fazer merge da branch dev para main
# O pipeline será executado automaticamente
```

### 2. Deploy Manual
```bash
# Na EC2
export APP_IMAGE=123456789012.dkr.ecr.us-east-2.amazonaws.com/pointtils:latest
export DB_IMAGE=123456789012.dkr.ecr.us-east-2.amazonaws.com/pointtils-db:latest
./terraform/deploy-app.sh
```

### 3. Verificação
```bash
# Verificar se não há clone do repositório
ls -la /home/ubuntu/
# Deve mostrar apenas 'pointtils', não 'Backend'

# Verificar containers
docker-compose ps

# Health check
curl http://localhost:8080/actuator/health
```

## Próximos Passos Recomendados

1. **Destruir infraestrutura atual** para remover o clone existente
2. **Executar pipeline** para criar nova infraestrutura com as correções
3. **Verificar** que a aplicação funciona sem o clone do repositório
4. **Documentar** o novo fluxo para a equipe

## Observações Importantes

- A aplicação existente no servidor atual deve ser destruída
- O novo deploy usará apenas imagens do ECR
- Não haverá mais código fonte na EC2
- Todas as atualizações futuras serão feitas via pipeline
