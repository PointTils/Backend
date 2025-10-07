# PostgreSQL Dockerfile
# Baseado na imagem oficial do PostgreSQL
FROM postgres:latest

# Argumentos de build para variáveis de ambiente
ARG POSTGRES_DB
ARG POSTGRES_USER
ARG POSTGRES_PASSWORD

# Definir variáveis de ambiente
ENV POSTGRES_DB=${POSTGRES_DB}
ENV POSTGRES_USER=${POSTGRES_USER}
ENV POSTGRES_PASSWORD=${POSTGRES_PASSWORD}

# Pode adicionar configurações personalizadas aqui se necessário
# Exemplo: COPY init.sql /docker-entrypoint-initdb.d/

# Manter as portas padrão
EXPOSE 5432

# Health check para verificar se o PostgreSQL está funcionando
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
  CMD pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB} || exit 1
