FROM grafana/grafana:latest

# Copiar configurações personalizadas
COPY grafana.ini /etc/grafana/grafana.ini
COPY provisioning /etc/grafana/provisioning

# Copiar script de inicialização e dashboard
COPY init-dashboard.sh /init-dashboard.sh
COPY provisioning/dashboards/pointtils_dashboard.json /var/lib/grafana/dashboards/pointtils_dashboard.json

# Definir permissões corretas
USER root
RUN chmod +x /init-dashboard.sh && \
    chown grafana:grafana /var/lib/grafana/dashboards/pointtils_dashboard.json && \
    chown grafana:grafana /init-dashboard.sh

# Voltar para usuário grafana
USER grafana

# Expor porta padrão do Grafana
EXPOSE 3000

# Comando para iniciar o Grafana com inicialização do dashboard
CMD ["/bin/sh", "-c", "/init-dashboard.sh & grafana-server --config=/etc/grafana/grafana.ini"]
