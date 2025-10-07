# Guia de Diagramas de Deploy Pointtils

Este documento serve como um guia para acessar e interpretar os diferentes formatos de diagramas de deploy do sistema Pointtils.

## Problema de Renderização

O diagrama Mermaid original em `DIAGRAMA_DEPLOY.md` pode apresentar problemas de renderização em algumas plataformas como GitHub. Para resolver este problema, foram criadas versões alternativas do diagrama em diferentes formatos.

## Formatos Disponíveis

1. **[Diagrama Mermaid Atualizado](DIAGRAMA_DEPLOY.md)**: Versão corrigida do diagrama original usando sintaxe `flowchart` em vez de `graph` e adicionando comentários para melhor legibilidade.

2. **[Diagrama PlantUML](DIAGRAMA_DEPLOY_PLANTUML.md)**: Uma implementação alternativa usando PlantUML, que frequentemente tem melhor compatibilidade em diferentes plataformas.

3. **[Diagrama ASCII](DIAGRAMA_DEPLOY_ASCII.md)**: Uma representação em arte ASCII que renderiza corretamente em qualquer ambiente, independente de suporte a diagramas.

## Qual Versão Utilizar?

- **GitHub/GitLab**: Tente primeiro o Mermaid atualizado. Se não renderizar, utilize o PlantUML.
- **Documentação Offline**: O diagrama ASCII é garantido de funcionar em qualquer lugar.
- **Apresentações**: O PlantUML geralmente produz os diagramas visualmente mais agradáveis.
- **README ou Wikis**: O Mermaid é geralmente a melhor opção quando suportado.

## Visão Geral da Arquitetura

Nossa arquitetura de deploy utiliza:

1. **GitHub Actions** como plataforma de CI/CD, com pipelines separadas para ambientes de produção e desenvolvimento.
2. **Docker** para containerização da aplicação Java Spring Boot e banco de dados PostgreSQL.
3. **AWS ECR** para armazenamento e versionamento de imagens Docker.
4. **AWS EC2** para hospedagem das instâncias de aplicação.
5. **AWS S3** para armazenamento de arquivos e estado do Terraform.
6. **Terraform** para provisionar toda a infraestrutura como código.

## Fluxo de Deploy

O processo de deploy segue estas etapas principais:

1. Código é enviado para o GitHub (push ou pull request)
2. GitHub Actions é acionado com base na branch de destino:
   - `main` para produção (`deploy-to-aws.yml`)
   - `dev` ou `feature/*` para desenvolvimento (`deploy-to-dev.yml`)
3. Os testes unitários são executados
4. As imagens Docker são construídas
5. As imagens são enviadas para o AWS ECR com tags apropriadas
6. Terraform é executado para garantir que a infraestrutura esteja atualizada
7. Scripts de deploy são executados nas instâncias EC2
8. Containers são iniciados e health checks são executados
9. Em caso de falha, um rollback automático é acionado

## Ambientes

O sistema mantém dois ambientes separados:

1. **Produção**:
   - Branch: `main`
   - Workflow: `deploy-to-aws.yml`
   - Repositórios ECR: `pointtils` e `pointtils-db`
   - Instância EC2: `pointtils-app`
   - Containers: `pointtils` e `pointtils-db`

2. **Desenvolvimento**:
   - Branches: `dev` e `feature/*`
   - Workflow: `deploy-to-dev.yml`
   - Repositórios ECR: `pointtils-dev` e `pointtils-dev-db`
   - Instância EC2: `pointtils-dev-app`
   - Containers: `pointtils-dev` e `pointtils-db-dev`

## Estratégia de Rollback

Um aspecto importante da arquitetura é a estratégia de rollback automático:

1. Todas as imagens são tagueadas com `latest`/`dev-latest` e `previous`
2. Health checks robustos verificam a saúde da aplicação após deploy
3. Se os health checks falharem, os scripts de rollback são acionados
4. O sistema volta para a versão anterior (`previous`) automaticamente

Para detalhes completos sobre a arquitetura, consulte os diagramas mencionados acima.
