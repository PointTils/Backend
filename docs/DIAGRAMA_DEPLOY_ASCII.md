# Diagrama de Deploy Pointtils (ASCII Art)

```
+------------------------------------------+
|          GitHub Actions CI/CD            |
+------------------------------------------+
| Código-Fonte --> Pipeline CI/CD          |
|      |                |                  |
|      v                v                  |
| Testes Unitários --> Build Docker Images |
|                       |                  |
+------------------------|------------------+
                         |
                         v
+------------------------------------------------------+
|                       AWS Cloud                      |
|  +------------------+                                |
|  |     AWS ECR      |                                |
|  +--------|---------+                                |
|           |                                          |
|           v                                          |
|  +--------|---------+  +-----------------+           |
|  |   EC2 Instance   |  |     AWS S3      |           |
|  |  +----------+    |  |    Bucket       |           |
|  |  |   Docker |    |  +----|------------+           |
|  |  |  Engine  |    |       ^                        |
|  |  |  +-------v--+ |       |                        |
|  |  |  |App      |-|--------+                        |
|  |  |  |Container|   | |                               |
|  |  |  +--|------+   | |                               |
|  |  |     | Port     | |                               |
|  |  |     v 8080     | |                               |
|  |  |  +--|------+   | |                               |
|  |  |  |DB       |   | |                               |
|  |  |  |Container|   | |                               |
|  |  |  +--|------+   | |                               |
|  |  |     |          | |                               |
|  |  |     v          | |                               |
|  |  |  +-----------+ | |                               |
|  |  |  |Data Volume| | |                               |
|  |  |  +-----------+ | |                               |
|  |  |  +-------v--+  | |                               |
|  |  |  |Prometheus|  | |                               |
|  |  |  |Container |  | |                               |
|  |  |  +--|------+   | |                               |
|  |  |     | Port     | |                               |
|  |  |     v 9090     | |                               |
|  |  |  +--|------+   | |                               |
|  |  |  |Grafana   |  | |                               |
|  |  |  |Container |  | |                               |
|  |  |  +--|------+   | |                               |
|  |  |     | Port     | |                               |
|  |  |     v 3000     | |                               |
|  |  |  +-----------+ | |                               |
|  |  |  |Prometheus | | |                               |
|  |  |  |Data Vol.  | | |                               |
|  |  |  +-----------+ | |                               |
|  |  +----------------+ |                               |
|  +--------------------+                               |
|                                                       |
|  +--------------------+                               |
|  | Security Groups    |                               |
|  +--------------------+                               |
|                                                       |
|  +--------------------+                               |
|  | IAM Roles          |                               |
|  +--------------------+                               |
+-------------------------------------------------------+
                         ^
                         |
+------------------------------------------+
|          Terraform IaC                   |
| Terraform Code --> AWS Resources         |
+------------------------------------------+
```

## Fluxo de Deploy (ASCII Art)

```
Developer           GitHub              GitHub Actions                ECR                 Terraform               EC2                Docker
    |                  |                      |                         |                      |                    |                    |
    |-- Push code ---->|                      |                         |                      |                    |                    |
    |                  |-- Trigger pipeline ->|                         |                      |                    |                    |
    |                  |                      |-- Run tests ----------->|                      |                    |                    |
    |                  |                      |<- Tests complete -------|                      |                    |                    |
    |                  |                      |-- Build images -------->|                      |                    |                    |
    |                  |                      |-- Push images --------->|                      |                    |                    |
    |                  |                      |-- Deploy infra ---------|--------------------->|                    |                    |
    |                  |                      |                         |                      |-- Configure EC2 -->|                    |
    |                  |                      |-- Deploy app ---------->|                      |                    |                    |
    |                  |                      |                         |                      |                    |-- Pull images ---->|
    |                  |                      |                         |<--- Pull images -----|------------------>|                    |
    |                  |                      |                         |                      |                    |-- Run containers ->|
    |                  |                      |-- Health check ---------|----------------------|------------------>|                    |
    |                  |                      |<- Status response ------|----------------------|--------------------|                    |
    |<- Deployment ----|----------------------|                         |                      |                    |                    |
    |   complete       |                      |                         |                      |                    |                    |
```

## Ciclo de Deploy e Rollback

```
┌───────────┐     ┌───────────┐     ┌───────────┐     ┌───────────┐     ┌───────────┐
│  Código   │     │  CI/CD    │     │  Build    │     │  Deploy   │     │  Testes   │
│  Fonte    │────>│  Pipeline │────>│  Images   │────>│  na EC2   │────>│  Health   │
└───────────┘     └───────────┘     └───────────┘     └───────────┘     └───────────┘
                                                           │                  │
                                                           │                  │
                                                           │                  ▼
                                                           │             ┌───────────┐
                                                           │             │ Sucesso?  │
                                                           │             └───────────┘
                                                           │                  │
                                                           │                  │
                                                           ▼                  ▼
                                                     ┌───────────┐     ┌───────────┐
                                                     │ Rollback  │<────│   Não     │
                                                     │ Automático│     └───────────┘
                                                     └───────────┘           │
                                                           │                 │
                                                           │                 ▼
                                                           │           ┌───────────┐
                                                           └──────────>│   Fim     │
                                                                       └───────────┘
```

## Descrição do Fluxo de Deploy

1. Desenvolvedor envia código para o GitHub (push ou pull request)
2. GitHub Actions é acionado automaticamente
3. Pipeline executa testes unitários
4. Se os testes passarem, as imagens Docker são construídas
5. Imagens são enviadas para o AWS ECR com múltiplas tags
6. Terraform é executado para provisionar/atualizar a infraestrutura
7. Script de deploy é copiado e executado na EC2
8. Containers são iniciados na EC2 (aplicação, banco, Prometheus, Grafana)
9. Health check é executado para verificar se a aplicação está operacional
10. Se o health check falhar, rollback automático é acionado

## Monitoramento e Observabilidade

### Componentes de Monitoramento
- **Prometheus**: Coleta métricas da aplicação via endpoint `/actuator/prometheus`
- **Grafana**: Dashboard para visualização das métricas coletadas pelo Prometheus
- **Spring Boot Actuator**: Expõe métricas da aplicação para o Prometheus

### Portas de Acesso
- **8080**: Aplicação Pointtils
- **5432**: PostgreSQL Database
- **9090**: Prometheus (métricas)
- **3000**: Grafana (dashboard)

### Métricas Monitoradas
- Status da aplicação e utilização de recursos (CPU, memória)
- Performance HTTP (vazão, tempo de resposta, status codes)
- Métricas JVM (threads, garbage collection, classes carregadas)
- Conexões de banco de dados (HikariCP)
- Taxa de erro e latência

## Ambientes de Deploy

### Pipeline de Produção
- **Workflow**: `deploy-to-aws.yml`
- **Branch**: `main`
- **EC2 Instance**: `pointtils-app`
- **ECR Repositories**: `pointtils`, `pointtils-db`
- **S3 Buckets**: `pointtils-api-tests-[hash]`, `pointtils-terraform-state`
- **Terraform**: `terraform/` directory

### Pipeline de Desenvolvimento
- **Workflow**: `deploy-to-dev.yml`
- **Branches**: `dev`, `feature/*`
- **EC2 Instance**: `pointtils-dev-app`
- **ECR Repositories**: `pointtils-dev`, `pointtils-dev-db`
- **S3 Buckets**: `pointtils-dev-api-tests-[hash]`, `pointtils-terraform-state-dev`
- **Terraform**: `terraform-dev/` directory

### Diferenças entre Ambientes
- **Produção**: Ambiente estável para usuários finais
- **Desenvolvimento**: Ambiente para testes e validação de novas funcionalidades
- **Monitoramento**: Ambos os ambientes possuem Prometheus e Grafana separados
- **Isolamento**: Recursos completamente separados para evitar interferências
