FROM grafana/grafana:latest

# Copiar configurações personalizadas
COPY grafana.ini /etc/grafana/grafana.ini
COPY provisioning /etc/grafana/provisioning

# Criar diretório de dashboards e copiar dashboard
RUN mkdir -p /var/lib/grafana/dashboards
COPY provisioning/dashboards/ /var/lib/grafana/dashboards/

# Expor porta padrão do Grafana
EXPOSE 3000

# Comando padrão para iniciar o Grafana
CMD ["grafana-server", "--config=/etc/grafana/grafana.ini"]
