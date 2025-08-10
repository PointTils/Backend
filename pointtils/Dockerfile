FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /application
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /application/target/pointtils-0.0.1-SNAPSHOT.jar pointtils.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "pointtils.jar"]
