# Estágio de build - Compilação do código Java
FROM maven:3.8.4-openjdk-17 AS build

# Definir o diretório de trabalho
WORKDIR /application

# Copiar o arquivo pom.xml para baixar as dependências
COPY pom.xml /application/

# Baixar todas as dependências - isso será armazenado em cache se o pom.xml não mudar
RUN mvn dependency:go-offline -B

# Copiar o código fonte
COPY . /application/

# Compilar e empacotar o aplicativo
RUN mvn clean package -DskipTests

# Estágio de execução - Imagem final mais leve
FROM openjdk:17-jdk-slim

# Criar um usuário não-root para executar o aplicativo
RUN groupadd -r springuser && useradd -r -g springuser springuser

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o JAR do estágio de build
COPY --from=build /application/target/pointtils-0.0.1-SNAPSHOT.jar pointtils.jar

# Definir o usuário não-root
USER springuser

# Expor a porta que a aplicação usa
EXPOSE 8081

# Definir variáveis de ambiente para a JVM
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Comando para iniciar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar pointtils.jar"]
