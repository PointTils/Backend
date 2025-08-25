# SonarQube Dockerfile
# Baseado na imagem oficial do SonarQube Community Edition
FROM sonarqube:9.9-community

# Pode adicionar configurações personalizadas aqui se necessário
# Exemplo: COPY plugins/ /opt/sonarqube/extensions/plugins/

# Manter as portas e volumes padrão
EXPOSE 9000

# Health check para verificar se o SonarQube está funcionando
HEALTHCHECK --interval=30s --timeout=30s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:9000/api/system/status || exit 1
