# Estágio de build - Compilação do código Java
FROM maven:3.8.4-openjdk-17 AS build

# Definir o diretório de trabalho
WORKDIR /application

# Copiar o arquivo pom.xml diretamente
COPY pom.xml ./

# Baixar todas as dependências - isso será armazenado em cache se o pom.xml não mudar
RUN mvn dependency:go-offline -B

# Copiar o restante do código fonte
COPY src ./src

# Compilar e empacotar o aplicativo
RUN mvn clean package -DskipTests

# Estágio de execução - Imagem final mais leve
FROM openjdk:17-jdk-slim

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o JAR do estágio de build
COPY --from=build /application/target/pointtils-0.0.1-SNAPSHOT.jar app.jar

# Expor a porta que a aplicação usa
EXPOSE 8081

# Comando simplificado para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
