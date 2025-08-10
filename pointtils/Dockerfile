# Estágio de build
FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /application

# Copiar apenas os arquivos essenciais primeiro
COPY pointtils/pom.xml .
COPY pointtils/mvnw .
COPY pointtils/.mvn .mvn


# Copiar o restante do código
COPY pointtils/src ./src

# Compilar e empacotar
RUN ./mvnw clean package -DskipTests

# Estágio final
FROM openjdk:17-jdk-slim
WORKDIR /application

COPY --from=build /application/target/pointtils-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
