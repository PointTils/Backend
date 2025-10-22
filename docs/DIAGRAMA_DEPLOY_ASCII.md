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
|  |  |  |Container| | |                               |
|  |  |  +--|------+ | |                               |
|  |  |     | Port   | |                               |
|  |  |     v 8080   | |                               |
|  |  |  +--|------+ | |                               |
|  |  |  |DB       | | |                               |
|  |  |  |Container| | |                               |
|  |  |  +--|------+ | |                               |
|  |  |     |        | |                               |
|  |  |     v        | |                               |
|  |  |  +-----------+ | |                               |
|  |  |  |Data Volume| | |                               |
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
8. Containers são iniciados na EC2
9. Health check é executado para verificar se a aplicação está operacional
10. Se o health check falhar, rollback automático é acionado

## Ambientes de Deploy

- **Produção**: Via `deploy-to-aws.yml` (branch `main`)
- **Desenvolvimento**: Via `deploy-to-dev.yml` (branches `dev`, `feature/*`)
