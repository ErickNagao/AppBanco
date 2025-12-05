# Multi-stage build for AppBanco
# Stage 1: Build
FROM maven:3.8.8-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package -q

# Stage 2: Runtime
FROM eclipse-temurin:11-jre-jammy
WORKDIR /app
COPY --from=build /app/target/appbanco-1.0.0-SNAPSHOT.jar appbanco.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/appbanco.jar"]
