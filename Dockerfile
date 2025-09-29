# Etapa de build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -DskipTests clean package

# Etapa de execução
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-Dspring.profiles.active=prod", "-Dserver.port=8080", "-jar", "app.jar"]
