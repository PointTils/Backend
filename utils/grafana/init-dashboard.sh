#!/bin/bash

# Aguardar o Grafana iniciar
sleep 10

# Criar diretório de dashboards se não existir
mkdir -p /var/lib/grafana/dashboards

# Verificar se o dashboard foi copiado corretamente
if [ -f "/var/lib/grafana/dashboards/pointtils_dashboard.json" ]; then
    echo "✅ Dashboard PointTils encontrado em /var/lib/grafana/dashboards/pointtils_dashboard.json"
else
    echo "❌ Dashboard PointTils não encontrado!"
fi

# Verificar se o datasource está configurado
echo "Verificando configuração do datasource Prometheus..."
if [ -f "/etc/grafana/provisioning/datasources/prometheus.yml" ]; then
    echo "✅ Datasource Prometheus configurado"
else
    echo "❌ Datasource Prometheus não configurado!"
fi

echo "Inicialização do Grafana concluída!"
