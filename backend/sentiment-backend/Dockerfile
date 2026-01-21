# VERSIÓN ALTERNATIVA (Más simple, compatible con bind mounts)

# Imagen base con Java 21 y Maven
FROM eclipse-temurin:21-jdk-alpine

# Instalar Maven
RUN apk add --no-cache maven

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# COPIA DE MANERA SELECTIVA
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (esto se cachea)
RUN mvn dependency:go-offline

# EXPOSICIÓN DE CARPETAS PARA BIND MOUNTS
VOLUME ["/app/src", "/app/resources"]

# Puerto expuesto
EXPOSE 8080

# COMANDO PARA DESARROLLO CON BIND MOUNTS
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.profiles=docker"]