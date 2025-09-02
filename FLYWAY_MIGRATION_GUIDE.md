# Guia de Migração para Flyway

## Resumo das Mudanças

Este projeto foi migrado para usar **Flyway** para gerenciamento de migrations de banco de dados, substituindo o gerenciamento automático do Hibernate.

## O que foi alterado?

### 1. Dependências Maven
Adicionadas as seguintes dependências no `pom.xml`:
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

### 2. Estrutura de Diretórios
Criada a estrutura padrão do Flyway:
```
pointtils/src/main/resources/db/migration/
├── V1__Create_initial_schema.sql
└── V2__Insert_seed_data.sql
```

### 3. Configurações
- **application.properties**: Alterado `spring.jpa.hibernate.ddl-auto` de `${SPRING_JPA_HIBERNATE_DDL_AUTO}` para `validate`
- **application.properties**: Adicionadas configurações do Flyway usando variáveis de ambiente
- **.env.example**: Atualizado para incluir variáveis do Flyway e mudança no DDL-AUTO

### 4. Arquivos de Migration
- `V1__Create_initial_schema.sql`: Contém toda a estrutura inicial do banco (tabelas, tipos, constraints)
- `V2__Insert_seed_data.sql`: Contém os dados iniciais para desenvolvimento/teste

## Como funciona o Flyway?

### Convenção de Nomenclatura
- **V{versão}__{descrição}.sql**: Para migrations versionadas
- Exemplo: `V1__Create_initial_schema.sql`, `V2__Insert_seed_data.sql`

### Processo de Migration
1. O Flyway cria uma tabela `flyway_schema_history` para controlar as migrations executadas
2. Na primeira execução, executa todas as migrations em ordem
3. Em execuções subsequentes, executa apenas as novas migrations

## Comandos Úteis

### Via Maven (se configurado)
```bash
# Executar migrations
mvn flyway:migrate

# Ver status das migrations
mvn flyway:info

# Limpar banco (CUIDADO: remove todos os dados)
mvn flyway:clean
```

### Via Spring Boot
As migrations são executadas automaticamente na inicialização da aplicação quando:
- `spring.flyway.enabled=true` (padrão)
- A aplicação encontra arquivos de migration não executados

## Boas Práticas

### 1. Nunca altere migrations já executadas
- Migrations são imutáveis
- Para correções, crie uma nova migration

### 2. Nomenclatura clara
```sql
-- ✅ Bom
V3__Add_user_email_index.sql
V4__Update_appointment_status_enum.sql

-- ❌ Ruim
V3__fix.sql
V4__changes.sql
```

### 3. Estrutura de uma migration
```sql
-- V3__Add_user_email_index.sql
-- Descrição: Adiciona índice na coluna email da tabela users para melhorar performance

CREATE INDEX idx_users_email ON users(email);
```

### 4. Rollback
O Flyway Community Edition não suporta rollback automático. Para reverter:
1. Crie uma nova migration que desfaça as alterações
2. Ou use `flyway:clean` + `flyway:migrate` (perde todos os dados)

## Migração de Projetos Existentes

Se você já tem um banco com dados:

### Opção 1: Baseline (Recomendada)
```properties
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=0
```

### Opção 2: Dump e Restore
1. Faça backup dos dados
2. Limpe o banco
3. Execute as migrations
4. Restaure os dados necessários

## Troubleshooting

### Erro: "Migration checksum mismatch"
- **Causa**: Migration foi alterada após execução
- **Solução**: Reverta a alteração ou use `flyway:repair`

### Erro: "Schema validation failed"
- **Causa**: Diferença entre schema atual e esperado pelo JPA
- **Solução**: Verifique se as entities JPA estão alinhadas com as migrations

### Migration não executa
- Verifique se o arquivo está no diretório correto: `src/main/resources/db/migration/`
- Verifique a nomenclatura: deve seguir o padrão `V{número}__{descrição}.sql`
- Verifique se não há erros de sintaxe SQL

## Exemplo de Nova Migration

Para adicionar uma nova coluna:

```sql
-- V3__Add_user_last_login.sql
-- Adiciona coluna para rastrear último login do usuário

ALTER TABLE users ADD COLUMN last_login TIMESTAMP;
```

## Variáveis de Ambiente

O projeto utiliza variáveis de ambiente para configurar o Flyway. No arquivo `.env.example` você encontra:

```bash
# Flyway Configuration
SPRING_FLYWAY_ENABLED=true
SPRING_FLYWAY_LOCATIONS=classpath:db/migration
SPRING_FLYWAY_BASELINE_ON_MIGRATE=true
SPRING_FLYWAY_VALIDATE_ON_MIGRATE=true
```

### Descrição das Variáveis:
- **SPRING_FLYWAY_ENABLED**: Habilita/desabilita o Flyway
- **SPRING_FLYWAY_LOCATIONS**: Localização dos arquivos de migration
- **SPRING_FLYWAY_BASELINE_ON_MIGRATE**: Permite baseline em bancos existentes
- **SPRING_FLYWAY_VALIDATE_ON_MIGRATE**: Valida migrations antes de executar

## Configurações Avançadas

```properties
# Encoding dos arquivos SQL
spring.flyway.encoding=UTF-8

# Placeholder replacement
spring.flyway.placeholder-replacement=true
spring.flyway.placeholders.schema=pointtils

# Outras configurações úteis
spring.flyway.clean-disabled=true
spring.flyway.mixed=false
```

## Benefícios da Migração

1. **Controle de Versão**: Histórico completo de mudanças no banco
2. **Reprodutibilidade**: Mesmo schema em todos os ambientes
3. **Colaboração**: Conflitos de schema são detectados cedo
4. **Auditoria**: Rastreabilidade de todas as mudanças
5. **Automação**: Deployments automatizados incluem mudanças de banco

## Próximos Passos

1. Teste a aplicação com as novas configurações
2. Crie migrations para futuras mudanças no schema
3. Configure CI/CD para executar migrations automaticamente
4. Considere usar profiles diferentes para dev/test/prod
