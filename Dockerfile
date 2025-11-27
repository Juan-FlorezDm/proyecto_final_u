FROM maven:3.9.4-eclipse-temurin-17 AS builder

# Establecer directorio de trabajo
WORKDIR /workspace

# Copiar el POM primero (para aprovechar cache de Docker)
COPY pom.xml .

# Descargar dependencias (cache si el POM no cambia)
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar y empaquetar la aplicación
RUN mvn clean package -DskipTests

# Segunda etapa - Imagen más liviana para producción
FROM eclipse-temurin:17-jre

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=builder /workspace/target/*.jar app.jar

# Exponer el puerto (ajusta si tu app usa otro puerto)
EXPOSE 8080

# Variable de entorno para el perfil (opcional)
ENV SPRING_PROFILES_ACTIVE=prod

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]