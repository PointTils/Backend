# Utilitários Docker

Esta pasta contém os Dockerfiles e configurações para serviços auxiliares do projeto. -justo

## Estrutura

```
utils/
├── sonarqube/
│   └── Dockerfile          # Configuração do SonarQube
├── postgres/
│   └── Dockerfile          # Configuração do PostgreSQL
└── README.md              # Este arquivo
```

## SonarQube

O Dockerfile do SonarQube é baseado na imagem oficial `sonarqube:9.9-community` e inclui:

- Health check para verificar se o serviço está funcionando
- Configuração padrão de portas (9000)
- Volumes para persistência de dados

**Uso:**
```bash
# Build da imagem
docker build -t sonarqube-custom ./utils/sonarqube

# Ou através do docker-compose unificado
docker-compose up sonarqube
```

## PostgreSQL

O Dockerfile do PostgreSQL é baseado na imagem oficial `postgres:latest` e inclui:

- Health check para verificar conexão com o banco
- Configuração padrão de portas (5432)
- Suporte a variáveis de ambiente padrão do PostgreSQL

**Uso:**
```bash
# Build da imagem
docker build -t postgres-custom ./utils/postgres

# Ou através do docker-compose unificado
docker-compose up pointtils-db
```

## Personalização

Para adicionar configurações personalizadas:

1. **SonarQube**: Adicione plugins copiando para `/opt/sonarqube/extensions/plugins/`
2. **PostgreSQL**: Adicione scripts de inicialização em `/docker-entrypoint-initdb.d/`

Exemplo para SonarQube:
```dockerfile
COPY plugins/ /opt/sonarqube/extensions/plugins/
```

Exemplo para PostgreSQL:
```dockerfile
COPY init.sql /docker-entrypoint-initdb.d/
```

## Integração com Docker Compose

Estes serviços são integrados no docker-compose.yaml principal da raiz do projeto, permitindo:

- Orquestração unificada de todos os serviços
- Network compartilhada entre containers
- Health checks para dependências entre serviços
- Gerenciamento de volumes centralizado
