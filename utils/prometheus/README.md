# Monitoramento com Prometheus - PointTils

Este diretório contém as configurações para monitoramento da aplicação PointTils usando Prometheus e Grafana.

## Configuração

### Serviços Monitorados

- **PointTils Application**: Porta 8080, endpoint `/actuator/prometheus`
- **PostgreSQL Database**: Porta 5432 (requer exporter adicional)
- **Prometheus**: Porta 9090
- **Grafana**: Porta 3000 (dev: 3001)

### Arquivos de Configuração

- `prometheus.yml`: Configuração principal do Prometheus
- `alerts.yml`: Regras de alerta
- `recording_rules.yml`: Regras de gravação de métricas
- `Dockerfile`: Imagem Docker do Prometheus

## Como Usar

### Desenvolvimento

```bash
# Subir todos os serviços incluindo monitoramento
docker-compose -f docker-compose-dev.yaml up -d

# Acessar Prometheus
http://localhost:9090

# Acessar Grafana
http://localhost:3001
```

### Produção

```bash
# Subir serviços de produção com monitoramento
docker-compose -f docker-compose.prod.yaml up -d

# Acessar Prometheus
http://localhost:9090

# Acessar Grafana
http://localhost:3000
```

## Métricas Disponíveis

A aplicação PointTils expõe as seguintes métricas via Actuator:

- **JVM Metrics**: Uso de memória, CPU, garbage collection
- **HTTP Metrics**: Tempo de resposta, contagem de requisições
- **Database Metrics**: Conexões HikariCP
- **Custom Metrics**: Métricas específicas da aplicação

## Alertas Configurados

- **ServiceDown**: Serviço offline por mais de 1 minuto
- **HighResponseTime**: Tempo de resposta acima de 2 segundos
- **HighMemoryUsage**: Uso de memória acima de 80%
- **HighCPUUsage**: Uso de CPU acima de 80%
- **HighDatabaseConnections**: Conexões de banco acima de 80%
- **HighErrorRate**: Taxa de erro 5xx acima de 10%
- **FrequentGarbageCollection**: GC muito frequente
- **LongGarbageCollection**: GC demorando mais de 1 segundo
- **PointTilsServiceDown**: Aplicação PointTils offline
- **DatabaseServiceDown**: Banco de dados offline

## Configuração da Aplicação

Para expor métricas do Prometheus, a aplicação PointTils inclui:

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

E as seguintes configurações no `application.properties`:

```properties
management.endpoints.web.exposure.include=health,info,metrics,env,beans,prometheus
management.metrics.export.prometheus.enabled=true
```

## Dashboard Queries

Para criar dashboards no Grafana, consulte o arquivo `dashboard_queries.md` para exemplos de queries PromQL.

## Troubleshooting

1. **Métricas não aparecem no Prometheus**
   - Verifique se a aplicação está rodando
   - Confirme se o endpoint `/actuator/prometheus` está acessível
   - Verifique as configurações do actuator

2. **Alertas não funcionam**
   - Confirme se as regras estão carregadas no Prometheus
   - Verifique a sintaxe das expressões PromQL
   - Confirme os labels dos serviços

3. **Grafana não consegue conectar ao Prometheus**
   - Verifique se o Prometheus está rodando
   - Confirme a URL de conexão no Grafana
   - Verifique as configurações de rede do Docker
