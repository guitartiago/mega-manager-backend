# Etapa de build
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# 1) Baixa dependências em cache (rápido em rede lenta)
COPY pom.xml .
RUN mvn -B -e -DskipTests dependency:go-offline

# 2) Copia o código e faz o build com logs detalhados
COPY src ./src
RUN mvn -B -e -DskipTests clean package

# Etapa de execução
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-Dspring.profiles.active=prod", "-Dserver.port=8080", "-jar", "app.jar"]
