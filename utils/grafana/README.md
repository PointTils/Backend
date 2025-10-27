# Grafana Configuration

Esta pasta contém a configuração do Grafana para monitoramento da aplicação Pointtils.

## Estrutura

- `Dockerfile` - Imagem Docker personalizada do Grafana
- `grafana.ini` - Configuração principal do Grafana
- `provisioning/` - Configurações de provisionamento automático
  - `datasources/` - Configuração de fontes de dados (Prometheus)
  - `dashboards/` - Configuração de dashboards

## Configuração

O Grafana está configurado para:
- Usar SQLite como banco de dados interno
- Conectar-se ao Prometheus na URL `http://prometheus:9090`
- Usar credenciais padrão: admin/admin
- Provisionar automaticamente datasources e dashboards

## Portas

- **Local**: 3000
- **Desenvolvimento**: 3001
- **Produção**: 3000

## Uso

Para usar o Grafana, execute o docker-compose correspondente ao ambiente:

```bash
# Local
docker-compose up grafana

# Desenvolvimento
docker-compose -f docker-compose-dev.yaml up grafana-dev

# Produção
docker-compose -f docker-compose.prod.yaml up grafana
```

Acesse o Grafana em `http://localhost:3000` (ou `http://localhost:3001` para desenvolvimento) com as credenciais admin/admin.
