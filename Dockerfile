FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x ./mvnw
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw -B -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Sin variable PORT - Spring Boot usa 8080 por defecto
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]