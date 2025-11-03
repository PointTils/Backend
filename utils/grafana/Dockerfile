FROM grafana/grafana:latest

# Copiar configurações personalizadas
COPY grafana.ini /etc/grafana/grafana.ini
COPY provisioning /etc/grafana/provisioning

# Copiar script de inicialização
COPY init-dashboard.sh /init-dashboard.sh
RUN chmod +x /init-dashboard.sh

# Copiar dashboard para diretório de dashboards
COPY provisioning/dashboards/pointtils_dashboard.json /var/lib/grafana/dashboards/pointtils_dashboard.json

# Expor porta padrão do Grafana
EXPOSE 3000

# Comando para iniciar o Grafana com inicialização do dashboard
CMD ["/bin/sh", "-c", "/init-dashboard.sh & grafana-server --config=/etc/grafana/grafana.ini"]
