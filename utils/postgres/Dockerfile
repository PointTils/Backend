# PostgreSQL Dockerfile
# Baseado na imagem oficial do PostgreSQL
FROM postgres:latest

# Pode adicionar configurações personalizadas aqui se necessário
# Exemplo: COPY init.sql /docker-entrypoint-initdb.d/

# Manter as portas padrão
EXPOSE 5432

# Health check para verificar se o PostgreSQL está funcionando
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
  CMD pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB} || exit 1
