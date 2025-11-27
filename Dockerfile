### Multi-stage Dockerfile for Spring Boot (Maven) application
# Stage 1: build the app with Maven
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Cache dependencies first to speed up rebuilds
COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -B -DskipTests package

# Stage 2: minimal runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# copy fat JAR from the builder stage
COPY --from=builder /workspace/target/*.jar app.jar

# Application listens on 8080 by default
EXPOSE 8080

# sensible default memory options (easy to override at runtime)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Start the Spring Boot app
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
