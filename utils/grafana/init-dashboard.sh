#!/bin/bash

# Aguardar o Grafana iniciar
sleep 10

# Criar diretório de dashboards se não existir
mkdir -p /var/lib/grafana/dashboards

# Copiar dashboard para o diretório correto
cp /tmp/pointtils_dashboard.json /var/lib/grafana/dashboards/pointtils_dashboard.json

echo "Dashboard PointTils copiado com sucesso!"

# Manter o container rodando
tail -f /dev/null
