FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /workspace
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar

# Render asigna puerto automáticamente via env var
ENV PORT=8080
EXPOSE 8080

# Usar el puerto de la variable de entorno
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]