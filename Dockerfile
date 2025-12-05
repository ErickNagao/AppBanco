# Multi-stage build for AppBanco
# Stage 1: Build
FROM maven:3.9.0-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:11-jre-focal
WORKDIR /app
COPY --from=build /app/target/appbanco-1.0.0-SNAPSHOT.jar appbanco.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/appbanco.jar"]
