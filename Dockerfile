# Stage 1: Build with Maven and Java 21
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -B -DskipTests clean package

# Stage 2: Run with Java 21 JRE for a smaller image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create non-root user
RUN useradd -r -u 1001 -m appuser
COPY --from=build /app/target/*.jar /app/app.jar
RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]