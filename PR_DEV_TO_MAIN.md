📍 Título
**Sincronização de Branch Dev para Main - Implementação de Funcionalidades e Correções**

📌 Descrição
Este PR sincroniza a branch `dev` com a `main`, trazendo todas as funcionalidades implementadas, correções de bugs e melhorias realizadas nos últimos 21 commits. A branch `dev` está significativamente à frente da `main` com múltiplas implementações de User Stories, correções críticas e melhorias de infraestrutura.

🛠️ O que foi feito?

-   [x] Implementação de nova funcionalidade
-   [x] Correção de bug
-   [x] Refatoração de código
-   [x] Atualização de documentação

🔍 Arquivos novos/modificados?

path: `.github/workflows/deploy-to-aws.yml`
path: `.github/workflows/deploy-to-dev.yml`
path: `docker-compose.yaml`
path: `docker-compose.prod.yaml`
path: `docker-compose-dev.yaml`
path: `README.md`
path: `pointtils/src/main/java/com/pointtils/pointtils/src/application/controllers/`
path: `pointtils/src/main/java/com/pointtils/pointtils/src/application/services/`
path: `pointtils/src/main/resources/db/migration/V17__Insert_additional_data.sql`
path: `pointtils/src/main/resources/db/migration/V18__Add_video_url_to_interpreter.sql`
path: `pointtils/src/main/resources/db/migration/V19__Update_password_reset_template.sql`
path: `pointtils/src/main/resources/db/migration/V20__Update_client_users_to_person.sql`
path: `pointtils/src/main/resources/db/migration/V21__Create_user_app_table.sql`
path: `pointtils/src/main/resources/db/migration/V22__Update_email_templates_logo.sql`
path: `pointtils/src/main/resources/db/migration/V23__Fix_email_templates_logo.sql`
path: `utils/prometheus/`
path: `utils/grafana/`
path: `docs/DIAGRAMA_DEPLOY.md`
path: `docs/DIAGRAMA_DEPLOY_ASCII.md`
path: `docs/DIAGRAMA_DEPLOY_PLANTUML.md`

🧪 Testes realizados:
- Testes unitários implementados e cobertura aumentada
- Testes de integração para endpoints de email
- Testes de validação de código de verificação
- Testes de autenticação e autorização
- Testes de envio de email com templates atualizados
- Testes de migrações de banco de dados

👀 Problemas conhecidos:
- Nenhum problema crítico identificado
- Todas as funcionalidades foram testadas em ambiente de desenvolvimento
- Migrações de banco foram validadas e testadas

📷 Anexos

**Arquitetura de Monitoramento:**
```yaml
# Configuração Prometheus
services:
  prometheus:
    build: ./utils/prometheus
    ports: ["9090:9090"]
  
  grafana:
    build: ./utils/grafana  
    ports: ["3000:3000"]
```

**Templates de Email Atualizados:**
```html
<!-- Logo da aplicação em todos os templates -->
<img src="https://pointtils-api-tests-d9396dcc.s3.us-east-2.amazonaws.com/logo_pointils.png">
```

✅ Checklist

-   [x] Testes foram adicionados/atualizados
-   [x] Documentação foi atualizada (se necessário)
-   [x] O código segue os padrões do projeto

📎 Referências

**Issues e User Stories Implementadas:**
- #208 - Correção de bugs identificados durante testes
- #207 - Rota DELETE para documentos de intérprete (US02-206)
- #205 - Correções em templates de email (US07-196)
- #203 - Validação de código de verificação (US05-202)
- #201 - Correções em agendamentos (US13-151)
- #195 - Configuração Firebase
- #198 - Correções em migrações de banco

**Commits Principais:**
- `3457104` - docs: atualizar README.md com informações completas do projeto
- `24f8bd4` - Merge pull request #208 from PointTils/fix/patch-appointments
- `03f655e` - Corrige bugs identificados durante testes
- `b608621` - feat: aplicar melhorias do deploy-to-dev no deploy-to-aws
- `4224a87` - Merge pull request #207 from PointTils/feat/us02-206
- `414593c` - feat: adiciona rota de delete em interpreter document
- `bd08512` - Merge pull request #205 from PointTils/fix/us07-196

**Estatísticas:**
- 21 commits divergentes
- 157 arquivos modificados
- 6 User Stories implementadas
- 5 correções críticas de bugs
- Sistema de monitoramento implementado
- Templates de email atualizados com logo
- Migrações de banco V17-V23 implementadas
