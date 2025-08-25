# PointTils Backend

## Visão Geral
Backend desenvolvido em Java Spring Boot para gerenciamento de End-points. Segue uma arquitetura limpa com separação clara de camadas.

## Arquitetura
```
┌─────────────────────────────────────────────────┐
│                    API REST                     │
└─────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│                 Controllers (REST)              │
│ - PointController                               │
└─────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│                    Services                     │
│ - PointService                                  │
└─────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│                  Repositories                   │
│ - PointRepository                               │
└─────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│                     Domain                      │
│ - Point (Entidade)                              │
│ - PointType (Enum)                              │
└─────────────────────────────────────────────────┘
```

## Estrutura de Pastas
```
.
├── pointtils/                        # Aplicação principal
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/pointtils/pointtils/
│   │   │   │   ├── src/                  # Código fonte principal
│   │   │   │   │   ├── application/      # Lógica de aplicação
│   │   │   │   │   │   ├── controllers/  # Controladores REST
│   │   │   │   │   │   ├── dto/          # Objetos de transferência
│   │   │   │   │   │   ├── mapper/       # Mapeadores DTO-Entity
│   │   │   │   │   │   └── services/     # Serviços de negócio
│   │   │   │   │   ├── core/             # Núcleo do domínio
│   │   │   │   │   │   └── domain/       # Entidades e enums
│   │   │   │   │   └── infrastructure/   # Infraestrutura
│   │   │   │   │       ├── configs/      # Configurações
│   │   │   │   │       └── repositories/ # Repositórios
│   │   ├── resources/                # Arquivos de configuração
│   └── test/                         # Testes unitários
├── utils/                            # Utilitários e serviços auxiliares
│   ├── sonarqube/                    # Configuração SonarQube
│   │   └── Dockerfile
│   └── postgres/                     # Configuração PostgreSQL
│       └── Dockerfile
├── docker-compose.yaml               # Orquestração unificada de containers
└── sonarqube-docker-compose.yaml     # Docker-compose antigo (legado)
```

## Como Executar

### Pré-requisitos
- Java 17+
- Maven
- Docker (opcional)

### Comandos Úteis

**Executar localmente:**
```bash
cd pointtils
./mvnw spring-boot:run
```

**Buildar e executar com Docker (nova estrutura unificada):**
```bash
# Executa todos os serviços: aplicação, PostgreSQL e SonarQube
docker-compose up --build

# Ou para executar em background:
docker-compose up -d --build

# Executar apenas serviços específicos:
docker-compose up pointtils pointtils-db  # Apenas app + banco
docker-compose up sonarqube               # Apenas SonarQube
```

**Comandos Docker úteis:**
```bash
# Ver status dos containers
docker-compose ps

# Parar todos os serviços
docker-compose down

# Parar e remover volumes (dados)
docker-compose down -v

# Ver logs de um serviço específico
docker-compose logs pointtils
docker-compose logs sonarqube
```

**Executar testes:**
```bash
./mvnw test
```

**Gerar documentação Swagger:**
Documentação disponível no seguinte endereço:
```
https://backend-v5gs.onrender.com/swagger-ui/index.html
```

## Dicas de Desenvolvimento

1. **Padrão de Commits**: Siga o Conventional Commits
2. **Testes**: Adicione novos testes em `src/test/java`
3. **DTOs**: Sempre use DTOs para comunicação externa
4. **Documentação**: Mantenha atualizada a documentação Swagger
5. **Docker**: Use `docker-compose` para ambiente consistente

## Configurações
As principais configurações estão em:
- `src/main/resources/application.properties` 
- `src/main/java/.../configs/OpenApiConfig.java`

### Variáveis de Ambiente
O projeto utiliza um arquivo `.env.example` como template para configurações sensíveis. Para executar o projeto:

1. Copie o arquivo `.env.example` para `.env`:
```bash
cp .env.example .env
```

2. Edite o `.env` com seus valores reais (não versionado no Git)

Variáveis comuns:
```
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
JWT_SECRET=
```

3. Para Docker, certifique-se que as variáveis estão definidas no `docker-compose.yaml` ou no `.env`
