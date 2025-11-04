FROM maven:3.9.3-eclipse-temurin-17 AS builder-stage

LABEL name="inventario-service-builder"

WORKDIR /app

# Copiar estructura del servicio de inventario desde el directorio correcto
COPY inventario-service/ .

# Compilar el servicio de inventario usando el pom-docker.xml
RUN mvn clean package -f pom-docker.xml -DskipTests

# Segunda etapa - Runtime
FROM eclipse-temurin:17-jre AS runtime-stage

LABEL name="inventario-service" \
      description="Servicio de Inventario - Backend FullStack" \
      version="1.0.0" \
      port="8082"

WORKDIR /app

# Install netcat for healthcheck
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

COPY --from=builder-stage /app/target/inventario-service-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]

