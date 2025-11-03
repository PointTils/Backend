FROM grafana/grafana:latest

# Copiar configurações personalizadas
COPY grafana.ini /etc/grafana/grafana.ini
COPY provisioning /etc/grafana/provisioning

# Copiar dashboard para diretório de dashboards
COPY provisioning/dashboards/pointtils_dashboard.json /var/lib/grafana/dashboards/pointtils_dashboard.json

# Expor porta padrão do Grafana
EXPOSE 3000

# Comando padrão para iniciar o Grafana
CMD ["grafana-server", "--config=/etc/grafana/grafana.ini"]
