FROM maven:3.9.3-eclipse-temurin-17 AS builder-stage

LABEL name="productos-service-builder"

WORKDIR /app

# Copiar estructura del servicio de productos desde el directorio correcto
COPY productos-service/ .

# Compilar el servicio de productos usando el pom-docker.xml
RUN mvn clean package -f pom-docker.xml -DskipTests

# Segunda etapa - Runtime
FROM eclipse-temurin:17-jre AS runtime-stage

LABEL name="productos-service" \
      description="Servicio de Productos - Backend FullStack" \
      version="1.0.0" \
      port="8081"

WORKDIR /app

# Install netcat for healthcheck
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

COPY --from=builder-stage /app/target/productos-service-*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]

